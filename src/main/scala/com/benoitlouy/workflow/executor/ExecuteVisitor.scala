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

  override def visit[I, O](step: Apply1Step[I, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, O](step: Apply2Step[I1, I2, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, O](step: Apply3Step[I1, I2, I3, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, O](step: Apply4Step[I1, I2, I3, I4, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, O](step: Apply5Step[I1, I2, I3, I4, I5, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, O](step: Apply6Step[I1, I2, I3, I4, I5, I6, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, O](step: Apply7Step[I1, I2, I3, I4, I5, I6, I7, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, O](step: Apply8Step[I1, I2, I3, I4, I5, I6, I7, I8, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, O](step: Apply9Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O](step: Apply10Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O](step: Apply11Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O](step: Apply12Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O](step: Apply13Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O](step: Apply14Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O](step: Apply15Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O](step: Apply16Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O](step: Apply17Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O](step: Apply18Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O](step: Apply19Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O](step: Apply20Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O](step: Apply21Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O](step: Apply22Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O], state: stateType): stateType = process(step, state)

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
