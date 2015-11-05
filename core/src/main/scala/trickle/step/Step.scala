package trickle.step

import trickle.Visitor

trait OutputStep[O] {
  def accept[T](v: Visitor[T], state: T): T
}

trait OptionStep[O] extends OutputStep[StepIO[O]]

trait InputOutputStep[I, O] extends OptionStep[O] {
  def mapper: (I => StepIO[O])
}
