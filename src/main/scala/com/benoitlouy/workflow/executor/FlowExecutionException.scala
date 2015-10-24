package com.benoitlouy.workflow.executor

class FlowExecutionException(message: String, cause: Option[Throwable] = None) extends Exception(message, cause.orNull)

class InputMissingException(message: String) extends FlowExecutionException(message)
object InputMissingException {
  def apply(message: String) = new InputMissingException(message)
  def unapply(e: InputMissingException) = Some(e.getMessage)
}