package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.{Visitor, HMap}
import com.benoitlouy.workflow.step._
import com.benoitlouy.workflow.step.StepIOOperators._
import shapeless.ops.hlist._
import shapeless._

class State(val content: HMap[(OptionStep ~?> StepResult)#λ]) extends ExecutorState[State] {
  implicit object constraint extends (OptionStep ~?> StepResult)

  override def get[O](step: OptionStep[O]): Option[StepResult[O]] = content.get(step)

  override def processStep[O](step: OptionStep[O])(f: => State): State = content.get(step) match {
    case None => f
    case _ => this
  }

  def +[O](kv: (OptionStep[O], StepResult[O])): State = new State(content + kv)

  def ++(other: State): State = new State(content ++ other.content)
}

class ExecuteVisitor extends Visitor[State] with Executor[State] { self =>

  override def visit[O](step: SourceStep[O], state: stateType): stateType = {
    state.processStep(step) {
      state.get(step) match {
        case None => state +(step, StepResult(InputMissingException("missing input").failureIO[O]))
        case _ => state
      }
    }
  }

  object visitParents extends Poly {
    implicit def caseStep[O] = use((state: stateType, step: OptionStep[O]) => state ++ step.accept(self, state))
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
      newState2 + (step, applySafe(step.f, input))
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


  override def visit[I, O](step: JunctionStep[I, O], state: stateType): stateType = state

  def applySafe[I, O](mapper: I => StepIO[O], e: I) = {
    try {
      StepResult(mapper(e))
    } catch {
      case e: Exception => StepResult(e.failureIO[O])
    }
  }

  override def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): (StepIO[O], State) = {
    val m = Map(input:_*) mapValues { x => StepResult(x.successIO) }
    val state = step.accept(this, new State(new HMap[(OptionStep ~?> StepResult)#λ](m.asInstanceOf[Map[Any, Any]])))
    (state.get(step).get.result, state)
  }
}
