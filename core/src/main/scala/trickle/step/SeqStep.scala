package trickle.step

import trickle.Visitor

import scala.collection.GenSeq

class SeqStep[I, O, S[X] <: GenSeq[X]](val parent: OptionStep[S[StepIO[I]]], val f: StepIO[I] => StepIO[O]) extends OptionStep[S[StepIO[O]]] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}
