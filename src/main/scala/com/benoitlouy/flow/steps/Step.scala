package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor
import com.benoitlouy.flow.visitors.execute.ValidationNelException

trait OutputStep[O] {
  def accept[T](v: Visitor[T], state: T): T
}

trait OptionStep[O] extends OutputStep[ValidationNelException[O]]

trait InputOutputStep[I, O] extends OptionStep[O] {
  def mapper: (I => ValidationNelException[O])
}
