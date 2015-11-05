package trickle.executor

import trickle.step._
import trickle.executor.ExecutorOperators._

class ExecuteVisitorTest extends ExecutorSpec[State] {

  override def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): (StepIO[O], State) = step.execute(input:_*)
}
