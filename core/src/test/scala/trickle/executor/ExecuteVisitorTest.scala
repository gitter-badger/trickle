package trickle.executor

import trickle.syntax.executor._
import trickle.step._

class ExecuteVisitorTest extends ExecutorSpec[State] {

  override def execute[O](step: Step[O], input: (Step[_], Any)*): (StepIO[O], State) = step.execute(input:_*)
}
