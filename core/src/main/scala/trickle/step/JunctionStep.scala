package trickle.step

import trickle.Visitor

class JunctionStep[I, O](val parent: OptionStep[I], val f: StepIO[I] => StepIO[OptionStep[O]]) extends OptionStep[O] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}