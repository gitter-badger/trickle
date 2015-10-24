package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step.OptionStep

object ExecutorOperators {

  implicit class ExecuteFlowOps[O](val workflow: OptionStep[O]) {
    def execute(input: (OptionStep[_], Any)*) = Executor().execute(workflow, input:_*)
    def executeParallel(input: (OptionStep[_], Any)*) = Executor.parallel().execute(workflow, input:_*)
  }
}
