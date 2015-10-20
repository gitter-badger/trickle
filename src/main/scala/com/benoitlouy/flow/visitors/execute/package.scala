package com.benoitlouy.flow.visitors


import scalaz.{Validation, ValidationNel}



package object execute {
  type ValidationNelException[T] = ValidationNel[Exception, Option[T]]

  class ValidationNelExceptionOps(ex: Exception) {
    def failureNelEx[X]: ValidationNelException[X] = Validation.failureNel[Exception, Option[X]](ex)
  }

  class ValidationNelExceptionOps2[A](self: Option[A]) {
    def successEx: Validation[Exception, Option[A]] = Validation.success[Exception, Option[A]](self)
    def successNelEx: ValidationNelException[A] = successEx.toValidationNel
  }

  object ToValidationNelExOps {
    implicit def ToValidationNelExceptionFailureOps(ex: Exception): ValidationNelExceptionOps = new ValidationNelExceptionOps(ex)
    implicit def ToValidationNelExceptionSuccessOps[A](a: Option[A]): ValidationNelExceptionOps2[A] = new ValidationNelExceptionOps2(a)
  }
}
