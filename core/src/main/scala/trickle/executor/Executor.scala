package trickle.executor

import trickle.step.{StepIO, Step}

trait Executor[S <: ExecutorState[S]] {
  def execute[O](step: Step[O], input: (Step[_], Any)*): (StepIO[O], S)
}

object Executor {
  def parallel() = new ParallelExecuteVisitor
  def apply() = new ExecuteVisitor
}


