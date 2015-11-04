package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step.{StepIO, OptionStep}

object ExecutorOperators {

  implicit class ExecuteFlowOps[O](val workflow: OptionStep[O]) {
    def execute(input: (OptionStep[_], Any)*): (StepIO[O], State) = Executor().execute(workflow, input:_*)
    def executeParallel(input: (OptionStep[_], Any)*): (StepIO[O], ParallelState) = Executor.parallel().execute(workflow, input:_*)
  }
}
