package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step.StepIO

case class StepResult[+T](result: StepIO[T]) {

}
