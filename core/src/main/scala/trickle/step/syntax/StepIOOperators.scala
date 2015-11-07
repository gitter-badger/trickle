package trickle.step.syntax

import trickle.step._

import scalaz.Validation



trait StepIOOperators {
  implicit class StepIOFailureOps(ex: Exception) {
    def failureIO[X]: StepIO[X] = Validation.failureNel[Exception, Option[X]](ex)
  }

  implicit class StepIOSuccessOps[A](self: A) {
    def successIO: StepIO[A] = Validation.success[Exception, Option[A]](Option(self)).toValidationNel
  }

  implicit class StepIOSuccessOptionOps[A](self: Option[A]) {
    def successIO: StepIO[A] = Validation.success[Exception, Option[A]](self).toValidationNel
  }

  implicit class StepIOOperators[A](self: StepIO[A]) {
    def mMap[B](f: A => B): StepIO[B] = self map { inner: Option[A] => inner map f }
  }


//  implicit def ToStepIOFailureOps(ex: Exception): StepIOFailureOps = new StepIOFailureOps(ex)
//  implicit def ToStepIOSuccessOps[A](a: A): StepIOSuccessOps[A] = new StepIOSuccessOps(a)
//  implicit def ToStepIOSuccessOptionOps[A](a: Option[A]): StepIOSuccessOptionOps[A] = new StepIOSuccessOptionOps(a)
//  implicit def ToStepIOOps[A](self: StepIO[A]): StepIOOperators[A] = new StepIOOperators(self)

  def toIO[B](a: Any): StepIO[B] = a match {
    case None => None.successIO.asInstanceOf[StepIO[B]]
    case Some(e) => e.successIO.asInstanceOf[StepIO[B]]
    case e: Exception => e.failureIO[B]
    case e => e.successIO.asInstanceOf[StepIO[B]]
  }
}
