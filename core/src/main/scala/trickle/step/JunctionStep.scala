package trickle.step

import trickle.Visitor
import trickle.executor.InputMissingException
import trickle.syntax.step._

class JunctionStep[I, O](val parent: Step[I], val f: StepIO[I] => StepIO[Step[O]]) extends Step[O] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}
