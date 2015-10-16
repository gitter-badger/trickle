package com.benoitlouy.flow.visitors.execute

case class StepResult[+T](result: ValidationNelException[T]) {

}
