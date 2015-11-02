package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step.OptionStep

trait ExecutorState[S <: ExecutorState[S]] {
  def get[O](step: OptionStep[O]): Option[StepResult[O]]
  def processStep[O](step: OptionStep[O])(f: => S): S
}
