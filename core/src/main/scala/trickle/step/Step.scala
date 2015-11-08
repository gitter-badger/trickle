package trickle.step

import trickle.Visitor

trait OutputStep[+O] {
  def d: O
  def accept[T](v: Visitor[T], state: T): T
}

trait Step[+O] extends OutputStep[StepIO[O]] {
  override val d = null // scalastyle:ignore
}
