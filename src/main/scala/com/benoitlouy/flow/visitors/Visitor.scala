package com.benoitlouy.flow.visitors

import com.benoitlouy.flow.steps._

trait Visitor[T] {
  type stateType = T
  def visit[O](sourceStep: SourceStep[O], state: stateType): stateType
  def visit[I, O](mapStep: MapStep[I, O], state: stateType): stateType
  def visit[I, O](zipStep: Zip1Step[I, O], state: stateType): stateType
  def visit[I1, I2, O](zipStep: Zip2Step[I1, I2, O], state: stateType): stateType
  def visit[I1, I2, I3, O](zipStep: Zip3Step[I1, I2, I3, O], state: stateType): stateType
}
