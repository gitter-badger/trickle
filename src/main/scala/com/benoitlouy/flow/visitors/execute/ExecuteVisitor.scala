package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.HMap
import com.benoitlouy.flow.steps._
import com.benoitlouy.flow.visitors.Visitor
import com.benoitlouy.flow.visitors.execute.ToValidationNelExOps._
import shapeless.poly._
import shapeless.{~?>, Poly}

class ExecuteVisitor extends Visitor[HMap[(OptionStep ~?> StepResult)#λ]] { self =>


  implicit object constraint extends (OptionStep ~?> StepResult)
//  implicit val SO1 = ~?>.rel[OutputStep, StepResult]

  override def visit[O](sourceStep: SourceStep[O], state: stateType): stateType = {
    val e = getOption(state, sourceStep)
    e match {
      case None => put(state, sourceStep, StepResult(InputMissingException("missing input").failureNelEx[O]))
      case _ => state
    }
  }

  object visitParents extends Poly {
    implicit def caseStep[O] = use((state: stateType, step: OptionStep[O]) => state ++ step.accept(self, state))
  }

  object combineStates extends Poly {
    implicit def caseState = use((s1: stateType, s2: stateType) => s1 ++ s2)
  }

  class GetResults(state: stateType) extends (OptionStep ~> ValidationNelException) {
    override def apply[T](f: OptionStep[T]): ValidationNelException[T] = get(state, f).result
  }


  override def visit[I, O](zipStep: Zip1Step[I, O], state: stateType): stateType = {
    val newState = zipStep.parents.foldLeft(state)(visitParents)
    object getResult extends GetResults(newState)
    val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).head)
    put(newState, zipStep, StepResult(result))
  }

  override def visit[I1, I2, O](zipStep: Zip2Step[I1, I2, O], state: stateType): stateType = {
    val newState = zipStep.parents.foldLeft(state)(visitParents)
    object getResult extends GetResults(newState)
    val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).tupled)
    put(newState, zipStep, StepResult(result))
  }


  override def visit[I1, I2, I3, O](zipStep: Zip3Step[I1, I2, I3, O], state: stateType): stateType = {
    val newState = zipStep.parents.foldLeft(state)(visitParents)
    object getResult extends GetResults(newState)
    val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).tupled)
    put(newState, zipStep, StepResult(result))
  }

  def applySafe[I, O](mapper: I => ValidationNelException[O], e: I) = {
    try {
      mapper(e)
    } catch {
      case e: Exception => e.failureNelEx[O]
    }
  }

  def put[O](state: HMap[~?>[OptionStep, StepResult]#λ], step: OptionStep[O], stepResult: StepResult[O]) = {
    state + (step, stepResult)
  }

  def getOption[O](state: stateType, step: OptionStep[O]): Option[StepResult[O]] = state.get(step)
  def get[O](state: stateType, step: OptionStep[O]): StepResult[O] = getOption(state, step).get

  def execute[O](step: OptionStep[O], input: stateType): StepResult[O] = {
    val state = step.accept(this, input)
    get(state, step)
  }

}

object ExecuteVisitor {
  def apply[O](step: OptionStep[O], input: (OutputStep[_], Any)*) = {
    val m = Map(input:_*) mapValues { x =>
      StepResult(x.successNelEx)
    }
    new ExecuteVisitor().execute(step, new HMap[~?>[OptionStep, StepResult]#λ](m.asInstanceOf[Map[Any, Any]]))
  }
}