package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.{Visitor, HMap}
import com.benoitlouy.workflow.step._
import com.benoitlouy.workflow.step.StepIOOperators._
import shapeless.ops.hlist._
import shapeless._

class ExecuteVisitor extends Visitor[HMap[(OptionStep ~?> StepResult)#λ]] with Executor{ self =>

  implicit object constraint extends (OptionStep ~?> StepResult)

  override def visit[O](step: SourceStep[O], state: stateType): stateType = {
    get(state, step) match {
      case None => put(state, step, StepResult(InputMissingException("missing input").failure[O]))
      case _ => state
    }
  }

  object visitParents extends Poly {
    implicit def caseStep[O] = use((state: stateType, step: OptionStep[O]) => state ++ step.accept(self, state))
  }

  object getResults extends Poly2 {
    implicit def case1[O, T <: HList] = at[OptionStep[O], (T, stateType)] {
      case (step, (acc, state)) => (get(state, step).get.result :: acc, state)
    }
  }

  def process[T <: HList, I <: HList, P <: I, O](step: ApplyStep[T, I, O], state: stateType)
                                                (implicit leftFolder: LeftFolder.Aux[T, stateType, visitParents.type, stateType],
                                                rightFolder: RightFolder.Aux[T, (HNil.type, stateType), getResults.type, (P, stateType)]) = {
    ifNotInState(state, step) {
      val newState = step.parents.foldLeft(state)(visitParents)
      val (input, newState2) = step.parents.foldRight((HNil, newState))(getResults)
      put(newState2, step, applySafe(step.f, input))
    }
  }

  def ifNotInState[O](state: stateType, step: OptionStep[O])(f: => stateType): stateType = {
    get(state, step) match {
      case None => f
      case _ => state
    }
  }

  override def visit[I, O](step: Apply1Step[I, O], state: stateType): stateType = {
    process(step, state)
  }

  override def visit[I1, I2, O](step: Apply2Step[I1, I2, O], state: stateType): stateType = {
    process(step, state)
  }

  override def visit[I1, I2, I3, O](step: Apply3Step[I1, I2, I3, O], state: stateType): stateType = {
    process(step, state)
  }

  def applySafe[I, O](mapper: I => StepIO[O], e: I) = {
    try {
      StepResult(mapper(e))
    } catch {
      case e: Exception => StepResult(e.failure[O])
    }
  }

  def put[O](state: HMap[~?>[OptionStep, StepResult]#λ], step: OptionStep[O], stepResult: StepResult[O]) = {
    state + (step, stepResult)
  }

  def get[O](state: stateType, step: OptionStep[O]): Option[StepResult[O]] = state.get(step)

  override def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): StepIO[O] = {
    val m = Map(input:_*) mapValues { x => StepResult(x.success) }
    val state = step.accept(this, new HMap[(OptionStep ~?> StepResult)#λ](m.asInstanceOf[Map[Any, Any]]))
    get(state, step).get.result
  }

}
