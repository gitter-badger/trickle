package trickle.executor.syntax

import trickle.executor.{Executor, ParallelState, State}
import trickle.step.{Step, StepIO}

trait ExecutorOperators {

  implicit class ExecuteFlowOps[O](val workflow: Step[O]) {
    def execute(input: (Step[_], Any)*): (StepIO[O], State) = Executor().execute(workflow, input:_*)
    def executeParallel(input: (Step[_], Any)*): (StepIO[O], ParallelState) = Executor.parallel().execute(workflow, input:_*)
  }
}
