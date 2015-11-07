package trickle.step

import trickle.Visitor

import scala.collection.generic.CanBuildFrom
import scala.collection.{GenTraversableLike, GenSeq}

class SeqStep[I, O, S[X] <: GenTraversableLike[X, S[X]]](val parent: OptionStep[S[StepIO[I]]], val f: StepIO[I] => StepIO[O])
                                                        (implicit val bf: CanBuildFrom[S[StepIO[I]], StepIO[O], S[StepIO[O]]]) extends OptionStep[S[StepIO[O]]] {

  override def accept[T](v: Visitor[T], state: T): T = {
    v.visit(this, state)
  }
}
