package com.benoitlouy.workflow.step

import com.benoitlouy.workflow.Visitor

trait OutputStep[O] {
  def accept[T](v: Visitor[T], state: T): T
}

trait OptionStep[O] extends OutputStep[StepIO[O]]

trait InputOutputStep[I, O] extends OptionStep[O] {
  def mapper: (I => StepIO[O])
}
