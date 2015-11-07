package trickle.executor

import trickle.syntax.executor._
import trickle.step._

class ExecuteVisitorTest extends ExecutorSpec[State] {

  override def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): (StepIO[O], State) = step.execute(input:_*)
}
