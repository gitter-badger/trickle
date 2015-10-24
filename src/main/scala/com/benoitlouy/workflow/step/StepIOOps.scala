package com.benoitlouy.workflow.step

import scalaz.Validation

class StepIOFailureOps(ex: Exception) {
  def failure[X]: StepIO[X] = Validation.failureNel[Exception, Option[X]](ex)
}

class StepIOSuccessOps[A](self: A) {
  def success: StepIO[A] = Validation.success[Exception, Option[A]](Option(self)).toValidationNel
}

class StepIOOps[A](self: StepIO[A]) {
  def mMap[B](f: A => B): StepIO[B] = self map { _ map f }
}

object StepIOOps {
  implicit def ToStepIOFailureOps(ex: Exception): StepIOFailureOps = new StepIOFailureOps(ex)
  implicit def ToStepIOSuccessOps[A](a: A): StepIOSuccessOps[A] = new StepIOSuccessOps(a)
  implicit def ToStepIOOps[A](self: StepIO[A]): StepIOOps[A] = new StepIOOps(self)
}