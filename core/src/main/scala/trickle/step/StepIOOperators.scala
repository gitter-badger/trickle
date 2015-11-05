package trickle.step

import scalaz.Validation

class StepIOFailureOps(ex: Exception) {
  def failureIO[X]: StepIO[X] = Validation.failureNel[Exception, Option[X]](ex)
}

class StepIOSuccessOps[A](self: A) {
  def successIO: StepIO[A] = Validation.success[Exception, Option[A]](Option(self)).toValidationNel
}

class StepIOSuccessOptionOps[A](self: Option[A]) {
  def successIO: StepIO[A] = Validation.success[Exception, Option[A]](self).toValidationNel
}

class StepIOOperators[A](self: StepIO[A]) {
  def mMap[B](f: A => B): StepIO[B] = self map { inner: Option[A] => inner map f }
}

object StepIOOperators {
  implicit def ToStepIOFailureOps(ex: Exception): StepIOFailureOps = new StepIOFailureOps(ex)
  implicit def ToStepIOSuccessOps[A](a: A): StepIOSuccessOps[A] = new StepIOSuccessOps(a)
  implicit def ToStepIOSuccessOptionOps[A](a: Option[A]): StepIOSuccessOptionOps[A] = new StepIOSuccessOptionOps(a)
  implicit def ToStepIOOps[A](self: StepIO[A]): StepIOOperators[A] = new StepIOOperators(self)
}
