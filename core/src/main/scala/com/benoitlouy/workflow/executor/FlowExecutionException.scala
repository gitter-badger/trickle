package com.benoitlouy.workflow.executor

class FlowExecutionException(message: String, cause: Option[Throwable] = None) extends Exception(message, cause.orNull)

case class InputMissingException(message: String) extends FlowExecutionException(message)
