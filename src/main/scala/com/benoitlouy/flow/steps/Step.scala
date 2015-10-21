package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor

trait OutputStep[O] {
  def accept[T](v: Visitor[T], state: T): T
}

trait OptionStep[O] extends OutputStep[Option[O]]

trait InputOutputStep[I, O] extends OptionStep[O] {
  def mapper: (I => Option[O])
}
