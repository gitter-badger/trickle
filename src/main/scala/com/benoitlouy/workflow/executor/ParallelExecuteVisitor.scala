package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.{Visitor, ConcurrentHMap}
import com.benoitlouy.workflow.step._
import com.benoitlouy.workflow.step.StepIOOperators._
import org.apache.commons.pool2.{PooledObject, BaseKeyedPooledObjectFactory}
import org.apache.commons.pool2.impl.{GenericKeyedObjectPoolConfig, DefaultPooledObject, GenericKeyedObjectPool}
import shapeless.PolyDefns.{~>>, ~>}
import shapeless._
import shapeless.ops.hlist._
import shapeless.ops.tuple.IsComposite
import scalaz.concurrent.Task

import scala.concurrent.{Await, Future, blocking}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class State(val content: ConcurrentHMap[(OptionStep ~?> StepResult)#λ]) {

  private object lockPoolFactory extends BaseKeyedPooledObjectFactory[OutputStep[_], Any] {
    override def wrap(value: Any): PooledObject[Any] = new DefaultPooledObject[Any](value)
    override def create(key: OutputStep[_]): AnyRef = AnyRef
  }
  private object lockPoolConfig extends GenericKeyedObjectPoolConfig {
    setMaxTotalPerKey(1)
  }
  private object lockPool extends GenericKeyedObjectPool(lockPoolFactory, lockPoolConfig)

  implicit object constraint extends (OptionStep ~?> StepResult)

  def get[O](k: OptionStep[O]): Option[StepResult[O]] = content.get(k)

  def +=[O](kv: (OptionStep[O], StepResult[O])): State = {
    content += kv
    this
  }

  def ++=(other: State): State = {
    content ++= other.content
    this
  }

  def processStep[O](step: OptionStep[O])(f: => State): State = {
    val lock = lockPool.borrowObject(step)
    try {
      get(step) match {
        case None => f
        case _ => this
      }
    } finally {
      lockPool.returnObject(step, lock)
    }
  }


}

class ParallelExecuteVisitor extends Visitor[State] with Executor { self =>
  override def visit[O](sourceStep: SourceStep[O], state: stateType): stateType = {
    state.processStep(sourceStep) {
      state.get(sourceStep) match  {
        case None => state += (sourceStep, StepResult(InputMissingException("missing input").failure[O]))
        case _ => state
      }
    }
  }

  object visitParents extends Poly {
    implicit def caseStep[O] = use((state: stateType, step: OptionStep[O]) => state ++= step.accept(self, state))
  }

  object getResults extends Poly2 {
    implicit def case1[O, T <: HList] = at[OptionStep[O], (T, stateType)] {
      case (step, (acc, state)) => (state.get(step).get.result :: acc, state)
    }
  }

  def inputWithState[T <: HList, I <: HList, P](step: ApplyStep[T, I, _], state: stateType)
                                               (implicit leftFolder: LeftFolder.Aux[T, stateType, visitParents.type, stateType],
                                                rightFolder: RightFolder.Aux[T, (HNil.type, stateType), getResults.type, P]): P = {
    val newState = step.parents.foldLeft(state)(visitParents)
    step.parents.foldRight((HNil, newState))(getResults)
  }

  object visitParentsAsync extends Poly2 {
    implicit def case1[O] = at[OptionStep[O], (List[Task[stateType]], stateType)] {
      case (step, (tasks, state)) => ( tasks :+ Task { blocking { state ++= step.accept(self, state) }}, state)
    }
  }

  def tasksWithState[T <: HList, I <: HList, P](step: ApplyStep[T, I, _], state: stateType)
                                               (implicit rightFolder: RightFolder.Aux[T, (HNil.type, stateType), visitParentsAsync.type , P]): P = {
    step.parents.foldRight((HNil, state))(visitParentsAsync)
  }


  def inputWithStateAsync[T <: HList, I <: HList, P](step: ApplyStep[T, I, _], state: stateType)
                                                  (implicit rightFolder: RightFolder.Aux[T, (List[Task[stateType]], stateType), visitParentsAsync.type, (List[Task[stateType]], stateType)],
                                                  rightFolder2: RightFolder.Aux[T, (HNil.type, stateType), getResults.type, P]) = {
    val (tasks, newState) = step.parents.foldRight((Nil.asInstanceOf[List[Task[stateType]]], state))(visitParentsAsync)
    Task.gatherUnordered(tasks).run
    step.parents.foldRight((HNil, newState))(getResults)
  }

  def applySafe[I, O](mapper: I => StepIO[O], e: I) = {
    try {
      StepResult(mapper(e))
    } catch {
      case e: Exception => StepResult(e.failure[O])
    }
  }

  override def visit[I, O](zipStep: Apply1Step[I, O], state: stateType): stateType = {
    state.processStep(zipStep) {
      val (input, newState) = inputWithState(zipStep, state)
      newState += (zipStep, applySafe(zipStep.f, input))
    }
  }

  override def visit[I1, I2, O](zipStep: Apply2Step[I1, I2, O], state: stateType): stateType = {
    state.processStep(zipStep) {
      val (input, newState) = inputWithStateAsync(zipStep, state)
      newState += (zipStep, applySafe(zipStep.f, input))
    }
  }

  override def visit[I1, I2, I3, O](zipStep: Apply3Step[I1, I2, I3, O], state: stateType): stateType =  {
    state.processStep(zipStep) {
      val (input, newState) = inputWithStateAsync(zipStep, state)
      newState += (zipStep, applySafe(zipStep.f, input))
    }
  }

  def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): StepIO[O] = {
    val m = Map(input:_*) mapValues { x => StepResult(x.success) }
    val inputState = new State(new ConcurrentHMap[~?>[OptionStep, StepResult]#λ](m.asInstanceOf[Map[Any, Any]]))
    val state = step.accept(this, inputState)
    state.get(step).get.result
  }
}
