package trickle

import scalaz.ValidationNel

package object step {
  type StepIO[T] = ValidationNel[Exception, Option[T]]
}
