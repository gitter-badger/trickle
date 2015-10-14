package com.benoitlouy.flow

import scalaz._
import Scalaz._

class StepResult[T](val result: ValidationNel[Exception, T]) {
  type resultType = T
}

class State(val inputs: Map[OutputStep[_], Any]) {
  val results: Map[OutputStep[_], StepResult[Any]] = inputs.mapValues((x: Any)  => new StepResult[Any](x.successNel))
}

class Executor(val inputs: Map[OutputStep[_], Any]) {
  val state: State = new State(inputs)

  def execute[O](step: OutputStep[O]): ValidationNel[Exception, O] = {
    step match {
      case r: SourceStep[O] => processResult[O](state.results.get(step).get)
    }
  }

  def processResult[O](stepResult: StepResult[_]): ValidationNel[Exception, O] = {
    stepResult match {
      case r: StepResult[O] => r.result
      case _ => new ClassCastException().failureNel[O]
    }
  }
}

