package com.benoitlouy.flow.visitors.execute

import scalaz.ValidationNel

case class StepResult[T](result: ValidationNel[Exception, T]) {

}
