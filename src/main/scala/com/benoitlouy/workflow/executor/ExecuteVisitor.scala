package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.{Visitor, HMap}
import com.benoitlouy.workflow.step._
import com.benoitlouy.workflow.step.StepIOOperators._
import shapeless.ops.hlist.{RightFolder, Mapper, LeftFolder, ZipConst}
import shapeless.ops.tuple.IsComposite
import shapeless.poly._
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

  case class Cont[T <: HList](state: stateType, result: T)

  object newGetResult extends Poly {
    implicit def case1[O, T <: HList] = use((acc: Cont[T], step: OptionStep[O]) => Cont[StepIO[O] :: T](acc.state, get(acc.state, step).result :: acc.result))
  }

  object newGetResult2 extends Poly2 {
    implicit def case1[O, T <: HList] = at[OptionStep[O], (T, stateType)] {
      case (step, (acc, state)) => (get(state, step).result :: acc, state)
    }
  }

  def visit1[T <: HList, I <: HList, O, P](zipStep: ApplyStep[T, I, O], state: stateType)
                                       (implicit leftFolder: LeftFolder.Aux[T, stateType, visitParents.type, stateType],
                                        rightFolder: RightFolder.Aux[T, (HNil.type, stateType), newGetResult2.type, (I, stateType)],
                                        ic: IsComposite.Aux[(I, stateType), I, _]): stateType = {
    ifNotInState(state, zipStep) {
      val newState: stateType = zipStep.parents.foldLeft(state)(visitParents)
      val input: (I, stateType) = zipStep.parents.foldRight((HNil, state))(newGetResult2)
      val input2: I = ic.head(input)
      val result: StepIO[O] = applySafe(zipStep.f, input2)
      put(newState, zipStep, StepResult(result))
    }
  }


  type StepState[O] = (OptionStep[O], stateType)

  object getResults2 extends (StepState ~> StepIO) {
    override def apply[T](f: StepState[T]): StepIO[T] = get(f._2, f._1).result
  }

  class GetResults(val state: stateType) extends (OptionStep ~> StepIO) {
    override def apply[T](f: OptionStep[T]): StepIO[T] = get(state, f).result
  }

  def ifNotInState[O](state: stateType, step: OptionStep[O])(f: => stateType): stateType = {
    getOption(state, step) match {
      case None => f
      case _ => state
    }
  }

  override def visit[I, O](zipStep: Apply1Step[I, O], state: stateType): stateType = {
    visit1(zipStep, state)
  }

  override def visit[I1, I2, O](zipStep: Apply2Step[I1, I2, O], state: stateType): stateType = {
    ifNotInState(state, zipStep) {
      val newState: stateType = zipStep.parents.foldLeft(state)(visitParents)
      object getResults extends GetResults(newState)
      val input = zipStep.parents map getResults
      val result = applySafe(zipStep.f, input)
      put(getResults.state, zipStep, StepResult(result))
    }
  }


  override def visit[I1, I2, I3, O](zipStep: Apply3Step[I1, I2, I3, O], state: stateType): stateType = {
    ifNotInState(state, zipStep) {
      val newState: stateType = zipStep.parents.foldLeft(state)(visitParents)
      object getResults extends GetResults(newState)
      val input = zipStep.parents map getResults
      val result = applySafe(zipStep.f, input)
      put(getResults.state, zipStep, StepResult(result))
    }
  }

  def generateNewState[T <: HList, I <: HList, O](zipStep: ApplyStep[T, I, O], state: stateType)
                                                 (implicit folder: LeftFolder.Aux[T, stateType, visitParents.type, stateType]): GetResults = {
    new GetResults(zipStep.parents.foldLeft(state)(visitParents))
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