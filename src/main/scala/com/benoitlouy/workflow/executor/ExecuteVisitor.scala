package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.{Visitor, HMap}
import com.benoitlouy.workflow.step._
import com.benoitlouy.workflow.step.StepIOOperators._
import shapeless.ops.hlist._
import shapeless.ops.tuple.IsComposite
import shapeless._

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

  object getResults extends Poly2 {
    implicit def case1[O, T <: HList] = at[OptionStep[O], (T, stateType)] {
      case (step, (acc, state)) => (get(state, step).result :: acc, state)
    }
  }

  case class InputAndState[I <: HList](input: I, state: stateType)
  object getResults2 extends Poly2 {
    implicit def case1[O, I <: HList] = at[OptionStep[O], InputAndState[I]] {
      case (step, accAndState)=> InputAndState(get(accAndState.state, step).result :: accAndState.input, accAndState.state)
    }
  }

  def inputWithState[T <: HList, I <: HList, P](step: ApplyStep[T, I, _], state: stateType)
                                               (implicit leftFolder: LeftFolder.Aux[T, stateType, visitParents.type, stateType],
                                                rightFolder: RightFolder.Aux[T, (HNil.type, stateType), getResults.type, P]): P = {
    val newState = step.parents.foldLeft(state)(visitParents)
    step.parents.foldRight((HNil, newState))(getResults)
  }



  def ifNotInState[O](state: stateType, step: OptionStep[O])(f: => stateType): stateType = {
    getOption(state, step) match {
      case None => f
      case _ => state
    }
  }

  override def visit[I, O](zipStep: Apply1Step[I, O], state: stateType): stateType = {
    ifNotInState(state, zipStep) {
      val (input, newState) = inputWithState(zipStep, state)
      put(newState, zipStep, StepResult(applySafe(zipStep.f, input)))
    }
  }

  override def visit[I1, I2, O](zipStep: Apply2Step[I1, I2, O], state: stateType): stateType = {
    ifNotInState(state, zipStep) {
      val (input, newState) = inputWithState(zipStep, state)
      put(newState, zipStep, StepResult(applySafe(zipStep.f, input)))
    }
  }


  override def visit[I1, I2, I3, O](zipStep: Apply3Step[I1, I2, I3, O], state: stateType): stateType = {
    ifNotInState(state, zipStep) {
      val (input, newState) = inputWithState(zipStep, state)
      put(newState, zipStep, StepResult(applySafe(zipStep.f, input)))
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