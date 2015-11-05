package trickle.executor

import trickle.step.StepIO

case class StepResult[+T](result: StepIO[T]) {

}
