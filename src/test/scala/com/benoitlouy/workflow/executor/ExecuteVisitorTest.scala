package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step._
import com.benoitlouy.workflow.executor.ExecutorOperators._

class ExecuteVisitorTest extends ExecutorSpec {
  override def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): StepIO[O] = step.execute(input:_*)
}
