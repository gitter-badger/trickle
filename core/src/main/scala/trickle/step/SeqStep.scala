package trickle.step

import trickle.Visitor

class SeqStep[I, O](val parent: OptionStep[Seq[StepIO[I]]], val f: StepIO[I] => StepIO[O]) extends OptionStep[Seq[StepIO[O]]] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}
