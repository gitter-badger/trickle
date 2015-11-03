package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step._

trait ExecutorState[S <: ExecutorState[S]] {
  def get[O](step: OptionStep[O]): Option[StepResult[O]]
  def put[O](step: OptionStep[O], stepResult: StepResult[O]): S
  def putAll(other: S): S
  def processStep[O](step: OptionStep[O])(f: => S): S
}
