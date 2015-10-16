package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor

case class SourceStep[O]() extends OutputStep[O] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}
