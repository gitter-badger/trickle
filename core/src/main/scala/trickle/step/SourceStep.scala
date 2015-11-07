package trickle.step

import trickle.Visitor

class SourceStep[O] extends Step[O] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

object SourceStep {
  def apply[O](): SourceStep[O] = new SourceStep[O]
}
