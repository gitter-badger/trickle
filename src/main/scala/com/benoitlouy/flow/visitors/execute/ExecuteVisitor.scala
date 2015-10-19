package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.HMap
import com.benoitlouy.flow.steps._
import com.benoitlouy.flow.visitors.Visitor
import scalaz.Failure
import scalaz.Success
import scalaz.NonEmptyList
import scalaz.syntax.validation.ToValidationOps
import shapeless.poly._
import shapeless.{~?>, Poly}

class ExecuteVisitor extends Visitor[HMap[(OutputStep ~?> StepResult)#λ]] { self =>

  implicit val SO1 = ~?>.rel[OutputStep, StepResult]

  override def visit[O](sourceStep: SourceStep[O], state: stateType): stateType = {
    getOption(state, sourceStep) match {
      case None => put(state, sourceStep, StepResult(InputMissingException("missing input").failureNel[O]))
      case _ => state
    }
  }

  override def visit[I, O](mapStep: MapStep[I, O], state: stateType): stateType = {
    val parentState = mapStep.parent.accept(this, state)
    val parentResult: StepResult[I] = get(parentState, mapStep.parent)
    val result: ValidationNelException[O] = parentResult.result match {
      case Failure(NonEmptyList(e)) => e.failureNel[O]
      case Success(e) => applySafe(mapStep.mapper, e)
    }
    put(parentState, mapStep, StepResult(result))
  }

  object visitParents extends Poly {
    implicit def caseStep[O] = use((state: stateType, step: OutputStep[O]) => state ++ step.accept(self, state))
  }

  object combineStates extends Poly {
    implicit def caseState = use((s1: stateType, s2: stateType) => s1 ++ s2)
  }

  class GetResults(state: stateType) extends (OutputStep ~> Option) {
    override def apply[T](f: OutputStep[T]): Option[T] = get(state, f).result match {
      case Failure(NonEmptyList(e)) => None
      case Success(e) => Some(e)
    }
  }


  override def visit[I, O](zipStep: Zip1Step[I, O], state: stateType): stateType = {
    val newState = zipStep.parents.foldLeft(state)(visitParents)
    object getResult extends GetResults(newState)
    val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).head)
    put(state, zipStep, StepResult(result))
  }

  override def visit[I1, I2, O](zipStep: Zip2Step[I1, I2, O], state: stateType): stateType = {
    val newState = zipStep.parents.foldLeft(state)(visitParents)
    object getResult extends GetResults(newState)
    val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).tupled)
    put(state, zipStep, StepResult(result))
  }


  override def visit[I1, I2, I3, O](zipStep: Zip3Step[I1, I2, I3, O], state: stateType): stateType = {
    val newState = zipStep.parents.foldLeft(state)(visitParents)
    object getResult extends GetResults(newState)
    val result = applySafe(zipStep.zipper, (zipStep.parents map getResult).tupled)
    put(state, zipStep, StepResult(result))
  }

  def applySafe[I, O](mapper: I => O, e: I) = {
    try {
      mapper(e).successNel
    } catch {
      case e: Exception => e.failureNel[O]
    }
  }

  def put[O](state: HMap[~?>[OutputStep, StepResult]#λ], step: OutputStep[O], stepResult: StepResult[O]) = {
    state + (step, stepResult)
  }

  def getOption[O](state: HMap[~?>[OutputStep, StepResult]#λ], step: OutputStep[O]): Option[StepResult[O]] = state.get(step)
  def get[O](state: HMap[~?>[OutputStep, StepResult]#λ], step: OutputStep[O]): StepResult[O] = getOption(state, step).get

  def execute[O](step: OutputStep[O], input: HMap[~?>[OutputStep, StepResult]#λ]): StepResult[O] = {
    val state = step.accept(this, input)
    get(state, step)
  }

}

object ExecuteVisitor {
  def apply[O](step: OutputStep[O], input: (OutputStep[_], Any)*) = {
    val m = Map(input:_*) mapValues { x => StepResult(x.successNel) }
    new ExecuteVisitor().execute(step, new HMap[~?>[OutputStep, StepResult]#λ](m.asInstanceOf[Map[Any, Any]]))
  }
}