package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step.{StepIO, OptionStep}

trait Executor[S <: ExecutorState[S]] {
  def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): (StepIO[O], S)
}

object Executor {
  def parallel() = new ParallelExecuteVisitor
  def apply() = new ExecuteVisitor
}


