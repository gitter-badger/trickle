package trickle

import trickle.executor.syntax.ExecutorOperators
import trickle.step.syntax.{StepOperators, StepIOOperators}

package object syntax {

  object step extends StepIOOperators with StepOperators

  object executor extends ExecutorOperators

}
