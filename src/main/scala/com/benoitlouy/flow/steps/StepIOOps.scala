package com.benoitlouy.flow.steps

import scalaz.Validation

class StepIOFailureOps(ex: Exception) {
  def failureNelEx[X]: StepIO[X] = Validation.failureNel[Exception, Option[X]](ex)
}

class StepIOSuccessOps[A](self: A) {
  def successEx: Validation[Exception, Option[A]] = Validation.success[Exception, Option[A]](Option(self))
  def successNelEx: StepIO[A] = successEx.toValidationNel
}

class StepIOOps[A](self: StepIO[A]) {
  def oMap[B](f: A => B): StepIO[B] = self map { _ map f }
}

object StepIOOps {
  implicit def ToStepIOFailureOps(ex: Exception): StepIOFailureOps = new StepIOFailureOps(ex)
  implicit def ToStepIOSuccessOps[A](a: A): StepIOSuccessOps[A] = new StepIOSuccessOps(a)
  implicit def ToStepIOOps[A](self: StepIO[A]): StepIOOps[A] = new StepIOOps(self)
}
