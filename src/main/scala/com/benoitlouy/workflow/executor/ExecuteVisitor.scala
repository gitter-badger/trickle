package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.{Visitor, HMap}
import com.benoitlouy.workflow.step._
import com.benoitlouy.workflow.step.StepIOOperators._
import shapeless.poly._
import shapeless.{~?>, Poly}

class ExecuteVisitor extends Visitor[HMap[(OptionStep ~?> StepResult)#λ]] with Executor{ self =>

  implicit object constraint extends (OptionStep ~?> StepResult)

  override def visit[O](sourceStep: SourceStep[O], state: stateType): stateType = {
    getOption(state, sourceStep) match {
      case None => put(state, sourceStep, StepResult(InputMissingException("missing input").failure[O]))
      case _ => state
    }
  }

  object visitParents extends Poly {
    implicit def caseStep[O] = use((state: stateType, step: OptionStep[O]) => state ++ step.accept(self, state))
  }

  class GetResults(state: stateType) extends (OptionStep ~> StepIO) {
    override def apply[T](f: OptionStep[T]): StepIO[T] = get(state, f).result
  }

  def ifNotInState[O](state: stateType, step: OptionStep[O])(f: => stateType): stateType = {
    getOption(state, step) match {
      case None => f
      case _ => state
    }
  }

  override def visit[I, O](zipStep: Zip1Step[I, O], state: stateType): stateType = {
    ifNotInState(state, zipStep) {
      val newState = zipStep.parents.foldLeft(state)(visitParents)
      object getResult extends GetResults(newState)
      val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).head)
      put(newState, zipStep, StepResult(result))
    }
  }

  override def visit[I1, I2, O](zipStep: Zip2Step[I1, I2, O], state: stateType): stateType = {
    ifNotInState(state, zipStep) {
      val newState = zipStep.parents.foldLeft(state)(visitParents)
      object getResult extends GetResults(newState)
      val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).tupled)
      put(newState, zipStep, StepResult(result))
    }
  }


  override def visit[I1, I2, I3, O](zipStep: Zip3Step[I1, I2, I3, O], state: stateType): stateType = {
    ifNotInState(state, zipStep) {
      val newState = zipStep.parents.foldLeft(state)(visitParents)
      object getResult extends GetResults(newState)
      val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).tupled)
      put(newState, zipStep, StepResult(result))
    }
  }

  def applySafe[I, O](mapper: I => StepIO[O], e: I) = {
    try {
      mapper(e)
    } catch {
      case e: Exception => e.failure[O]
    }
  }

  def put[O](state: HMap[~?>[OptionStep, StepResult]#λ], step: OptionStep[O], stepResult: StepResult[O]) = {
    state + (step, stepResult)
  }

  def getOption[O](state: stateType, step: OptionStep[O]): Option[StepResult[O]] = state.get(step)
  def get[O](state: stateType, step: OptionStep[O]): StepResult[O] = getOption(state, step).get

  override def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): StepIO[O] = {
    val m = Map(input:_*) mapValues { x => StepResult(x.success) }
    val state = step.accept(this, new HMap[(OptionStep ~?> StepResult)#λ](m.asInstanceOf[Map[Any, Any]]))
    get(state, step).result
  }

}

object ExecuteVisitor {
  def apply[O](step: OptionStep[O], input: (OptionStep[_], Any)*) = {
    new ExecuteVisitor().execute(step, input:_*)
  }
}