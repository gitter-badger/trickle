package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.{Visitor, ConcurrentHMap}
import com.benoitlouy.workflow.step._
import com.benoitlouy.workflow.step.StepIOOperators._
import org.apache.commons.pool2.{PooledObject, BaseKeyedPooledObjectFactory}
import org.apache.commons.pool2.impl.{GenericKeyedObjectPoolConfig, DefaultPooledObject, GenericKeyedObjectPool}
import shapeless.PolyDefns.{~>>, ~>}
import shapeless.{Poly, ~?>}
import shapeless.ops.hlist._
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

  class FutureParents(state: stateType) extends (OptionStep ~>> Task[stateType]) {
    override def apply[T](f: OptionStep[T]): Task[stateType] = Task {
      blocking {
        f.accept(self, state)
      }
    }
  }

  object waitForParents extends Poly {
    implicit def caseFuture = use((state: stateType, future: Future[stateType]) => state ++= Await.result(future, 1 minute))
  }

  class GetResults(state: stateType) extends (OptionStep ~> StepIO) {
    override def apply[T](f: OptionStep[T]): StepIO[T] = state.get(f).get.result
  }

  def applySafe[I, O](mapper: I => StepIO[O], e: I) = {
    try {
      mapper(e)
    } catch {
      case e: Exception => e.failure[O]
    }
  }

  override def visit[I, O](zipStep: Apply1Step[I, O], state: stateType): stateType = {
    state
//    state.processStep(zipStep) {
//      val newState = zipStep.parents.foldLeft(state)(visitParents)
//      object getResult extends GetResults(newState)
//      val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).head)
//      state += (zipStep, StepResult(result))
//    }
  }

  override def visit[I1, I2, O](zipStep: Apply2Step[I1, I2, O], state: stateType): stateType = {
    state
//    state.processStep(zipStep) {
//      object futureParents extends FutureParents(state)
//      val futures = zipStep.parents map { futureParents }
//      Task.gatherUnordered(futures.toList[Task[stateType]]).run
//      object getResult extends GetResults(state)
//      val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).tupled)
//      state += (zipStep, StepResult(result))
//    }
  }

  override def visit[I1, I2, I3, O](zipStep: Apply3Step[I1, I2, I3, O], state: stateType): stateType =  {
    state
//    state.processStep(zipStep) {
//      object futureParents extends FutureParents(state)
//      val futures = zipStep.parents map { futureParents }
//      Task.gatherUnordered(futures.toList[Task[stateType]]).run
//      object getResult extends GetResults(state)
//      val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).tupled)
//      state += (zipStep, StepResult(result))
//    }
  }

  def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): StepIO[O] = {
    val m = Map(input:_*) mapValues { x => StepResult(x.success) }
    val inputState = new State(new ConcurrentHMap[~?>[OptionStep, StepResult]#λ](m.asInstanceOf[Map[Any, Any]]))
    val state = step.accept(this, inputState)
    state.get(step).get.result
  }
}

object
ParallelExecuteVisitor {
  def apply[O](step: OptionStep[O], input: (OptionStep[_], Any)*) = {
    new ParallelExecuteVisitor().execute(step, input:_*)
  }
}