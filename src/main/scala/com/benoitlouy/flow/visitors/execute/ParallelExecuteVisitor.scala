package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.steps.{SourceStep, Zip1Step, Zip2Step, Zip3Step}
import com.benoitlouy.flow.visitors.Visitor

class State {


}

class ParallelExecuteVisitor extends Visitor[State] {
  override def visit[O](sourceStep: SourceStep[O], state: stateType): stateType = state

  override def visit[I, O](zipStep: Zip1Step[I, O], state: stateType): stateType = state

  override def visit[I1, I2, O](zipStep: Zip2Step[I1, I2, O], state: stateType): stateType = state

  override def visit[I1, I2, I3, O](zipStep: Zip3Step[I1, I2, I3, O], state: stateType): stateType = state
}
