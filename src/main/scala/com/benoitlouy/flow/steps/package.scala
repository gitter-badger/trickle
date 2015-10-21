package com.benoitlouy.flow

import scalaz.ValidationNel

package object steps {
  type StepIO[T] = ValidationNel[Exception, Option[T]]
}
