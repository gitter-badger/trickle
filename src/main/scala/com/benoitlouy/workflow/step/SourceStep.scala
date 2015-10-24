package com.benoitlouy.workflow.step

import com.benoitlouy.workflow.Visitor

class SourceStep[O] extends OptionStep[O] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

object SourceStep {
  def apply[O](): SourceStep[O] = new SourceStep[O]
}
