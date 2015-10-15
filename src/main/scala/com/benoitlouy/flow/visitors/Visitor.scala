package com.benoitlouy.flow.visitors

import com.benoitlouy.flow.{MapStep, SourceStep}

trait Visitor[T] {
  def visit[O](sourceStep: SourceStep[O], state: T): T
  def visit[I, O](mapStep: MapStep[I, O], state: T): T
}
