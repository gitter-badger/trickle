package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor

trait OutputStep[O] {
  def accept[T](v: Visitor[T], state: T): T
}

trait InputOutputStep[I, O] extends OutputStep[O] {
  def mapper: (I => O)
}
