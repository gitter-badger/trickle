package trickle.executor

import java.util.concurrent.ExecutorService

import trickle.Visitor
import trickle.step._
import trickle.syntax.step._
import shapeless.ops.hlist.{RightFolder, LeftFolder}
import shapeless.{HNil, HList, Poly2, Poly}

import scala.concurrent._
import scalaz.concurrent.Task
import scalaz.{Failure, Success}

trait ExecutionUtils[S <: ExecutorState[S]] { self: Visitor[S] =>

  override def visit[O](step: SourceStep[O], state: stateType): stateType = {
    state.processStep(step) {
      state.get(step) match {
        case None => state.put(step, StepResult(InputMissingException("missing input").failureIO[O]))
        case _ => state
      }
    }
  }

  override def visit[I, O](step: JunctionStep[I, O], state: stateType): stateType = {
    state.processStep(step) {
      val newState = step.parent.accept(this, state)
      val input = newState.get(step.parent).get.result
      val branch = applySafe(step.f)(input)
      branch match {
        case Success(Some(s)) => {
          val newNewState = s.accept(this, newState)
          newNewState.put(step, newNewState.get(s).get)
        }
        case Success(None) => newState.put(step, StepResult(None.successIO))
        case Failure(nel) => newState.put(step, StepResult(Failure(nel)))
      }
    }
  }

  object visitParents extends Poly {
    implicit def caseStep[O] = use((state: stateType, step: Step[O]) => state.putAll(step.accept(self, state)))
  }

  object getResults extends Poly2 {
    implicit def case1[O, T <: HList] = at[Step[O], (T, stateType)] {
      case (step, (acc, state)) => (state.get(step).get.result :: acc, state)
    }
  }

  def process[T <: HList, I <: HList, P <: I, O](step: ApplyStep[T, I, O], state: stateType)
                                                (implicit leftFolder: LeftFolder.Aux[T, stateType, visitParents.type, stateType],
                                                 rightFolder: RightFolder.Aux[T, (HNil.type, stateType), getResults.type, (P, stateType)]) = {
    state.processStep(step) {
      val newState = step.parents.foldLeft(state)(visitParents)
      val (input, newState2) = step.parents.foldRight((HNil, newState))(getResults)
      newState2.put(step, StepResult(applySafe(step.f)(input)))
    }
  }

  def applySafe[I, O](mapper: I => StepIO[O])(e: I) = {
    try {
      mapper(e)
    } catch {
      case e: Exception => e.failureIO[O]
    }
  }

}

trait ParallelExecutionUtils[S <: ExecutorState[S]] extends ExecutionUtils[S] { self: Visitor[S] =>

  implicit val executor: ExecutorService

  object visitParentsParallel extends Poly2 {
    implicit def case1[O] = at[Step[O], (List[Task[stateType]], stateType)] {
      case (step, (tasks, state)) => ( tasks :+ Task { blocking { state.putAll(step.accept(self, state)) }}, state)
    }
  }

  def processParallel[T <: HList, I <: HList, P <: I, O](step: ApplyStep[T, I, O], state: stateType)
                                                        (implicit rightFolder: RightFolder.Aux[T, (List[Task[stateType]], stateType), visitParentsParallel.type, (List[Task[stateType]], stateType)],
                                                         rightFolder2: RightFolder.Aux[T, (HNil.type, stateType), getResults.type, (P, stateType)]) = {
    state.processStep(step) {
      val (tasks, newState) = step.parents.foldRight((Nil.asInstanceOf[List[Task[stateType]]], state))(visitParentsParallel)
      Task.gatherUnordered(tasks).run
      val (input, newState2) = step.parents.foldRight((HNil, newState))(getResults)
      newState2.put(step, StepResult(applySafe(step.f)(input)))
    }
  }
}
