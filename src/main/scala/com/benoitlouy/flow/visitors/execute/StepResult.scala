package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.steps.StepIO

case class StepResult[+T](result: StepIO[T]) {

}
