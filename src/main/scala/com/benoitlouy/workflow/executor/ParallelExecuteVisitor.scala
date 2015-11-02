package com.benoitlouy.workflow.executor

import java.util.concurrent.Executors

import com.benoitlouy.workflow.{Visitor, ConcurrentHMap}
import com.benoitlouy.workflow.step._
import com.benoitlouy.workflow.step.StepIOOperators._
import org.apache.commons.pool2.{PooledObject, BaseKeyedPooledObjectFactory}
import org.apache.commons.pool2.impl.{GenericKeyedObjectPoolConfig, DefaultPooledObject, GenericKeyedObjectPool}
import shapeless._
import shapeless.ops.hlist._
import scalaz.{Failure, Success}
import scalaz.concurrent.Task

import scala.concurrent.blocking

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

  implicit val executor = Executors.newCachedThreadPool()

  override def visit[O](step: SourceStep[O], state: stateType): stateType = {
    state.processStep(step) {
      state.get(step) match  {
        case None => state += (step, StepResult(InputMissingException("missing input").failureIO[O]))
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

  def process[T <: HList, I <: HList, P <: I, O](step: ApplyStep[T, I, O], state: stateType)
                                                (implicit leftFolder: LeftFolder.Aux[T, stateType, visitParents.type, stateType],
                                                rightFolder: RightFolder.Aux[T, (HNil.type, stateType), getResults.type, (P, stateType)]) = {
    state.processStep(step) {
      val newState = step.parents.foldLeft(state)(visitParents)
      val (input, newState2) = step.parents.foldRight((HNil, newState))(getResults)
      newState2 += (step, StepResult(applySafe(step.f, input)))
    }
  }

  object visitParentsParallel extends Poly2 {
    implicit def case1[O] = at[OptionStep[O], (List[Task[stateType]], stateType)] {
      case (step, (tasks, state)) => ( tasks :+ Task { blocking { state ++= step.accept(self, state) }}, state)
    }
  }

  def processParallel[T <: HList, I <: HList, P <: I, O](step: ApplyStep[T, I, O], state: stateType)
                                                        (implicit rightFolder: RightFolder.Aux[T, (List[Task[stateType]], stateType), visitParentsParallel.type, (List[Task[stateType]], stateType)],
                                                         rightFolder2: RightFolder.Aux[T, (HNil.type, stateType), getResults.type, (P, stateType)]) = {
    state.processStep(step) {
      val (tasks, newState) = step.parents.foldRight((Nil.asInstanceOf[List[Task[stateType]]], state))(visitParentsParallel)
      Task.gatherUnordered(tasks).run
      val (input, newState2) = step.parents.foldRight((HNil, newState))(getResults)
      newState2 += (step, StepResult(applySafe(step.f, input)))
    }
  }

  def applySafe[I, O](mapper: I => StepIO[O], e: I) = {
    try {
      mapper(e)
    } catch {
      case e: Exception => e.failureIO[O]
    }
  }

  override def visit[I, O](step: Apply1Step[I, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, O](step: Apply2Step[I1, I2, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, O](step: Apply3Step[I1, I2, I3, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, O](step: Apply4Step[I1, I2, I3, I4, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, O](step: Apply5Step[I1, I2, I3, I4, I5, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, O](step: Apply6Step[I1, I2, I3, I4, I5, I6, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, O](step: Apply7Step[I1, I2, I3, I4, I5, I6, I7, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, O](step: Apply8Step[I1, I2, I3, I4, I5, I6, I7, I8, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, O](step: Apply9Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O](step: Apply10Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O](step: Apply11Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O](step: Apply12Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O](step: Apply13Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O](step: Apply14Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O](step: Apply15Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O](step: Apply16Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O](step: Apply17Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O](step: Apply18Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O](step: Apply19Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O](step: Apply20Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O](step: Apply21Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O](step: Apply22Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O], state: stateType): stateType = processParallel(step, state)


  override def visit[I, O](step: JunctionStep[I, O], state: stateType): stateType = {
    state.processStep(step) {
      val newState = step.parent.accept(this, state)
      val input = newState.get(step.parent).get.result
      val branch = applySafe(step.f, input)
      branch match {
        case Success(Some(s)) => {
          val newNewState = s.accept(this, newState)
          newNewState += (step, newNewState.get(s).get)
        }
        case Success(None) => newState += (step, StepResult(None.successIO))
        case Failure(nel) => newState += (step, StepResult(Failure(nel)))
      }
    }
  }

  def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): StepIO[O] = {
    val m = Map(input:_*) mapValues { x => x match {
      case None => StepResult(None.successIO)
      case Some(e) => StepResult(e.successIO)
      case e => StepResult(e.successIO)
    }}
    val inputState = new State(new ConcurrentHMap[~?>[OptionStep, StepResult]#λ](m.asInstanceOf[Map[Any, Any]]))
    val state = step.accept(this, inputState)
    state.get(step).get.result
  }
}
