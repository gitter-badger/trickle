package com.benoitlouy.flow.visitors

import com.benoitlouy.flow.steps.{Zip2Step, MapStep, SourceStep}

trait Visitor[T] {
  def visit[O](sourceStep: SourceStep[O], state: T): T
  def visit[I, O](mapStep: MapStep[I, O], state: T): T
  def visit[I1, I2, O](zipStep: Zip2Step[I1, I2, O], state: T): T
}
