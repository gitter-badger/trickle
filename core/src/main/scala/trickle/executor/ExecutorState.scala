package trickle.executor

import trickle.step._

trait ExecutorState[S <: ExecutorState[S]] {
  def get[O](step: Step[O]): Option[StepResult[O]]
  def put[O](step: Step[O], stepResult: StepResult[O]): S
  def putAll(other: S): S
  def processStep[O](step: Step[O])(f: => S): S
}
