package trickle.step

import trickle.Visitor

trait OutputStep[O] {
  def accept[T](v: Visitor[T], state: T): T
}

trait Step[O] extends OutputStep[StepIO[O]]
