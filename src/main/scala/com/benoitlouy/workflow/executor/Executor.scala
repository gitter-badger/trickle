package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step.{StepIO, OptionStep}

trait Executor {
  def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): StepIO[O]
}

object Executor {
  def parallel() = new ParallelExecuteVisitor
  def apply() = new ExecuteVisitor
}


