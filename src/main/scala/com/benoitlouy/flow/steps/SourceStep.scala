package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor

class SourceStep[O] extends OutputStep[O] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

object SourceStep {
  def apply[O](): SourceStep[O] = new SourceStep[O]
}
