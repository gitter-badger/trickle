package trickle

import trickle.step.Step
import trickle.syntax.executor._
import trickle.syntax.step._

object convertStringToIntegerAndIncrement extends App {
  // Input of the flow is a String.
  val input = source[String]

  // Convert the String to an Int.
  // The input (and output) of a step is a StepIO[X] which is nothing more than a type alias for
  // Validation[NonEmptyList[Exception], Option[X]]. This mean than the input of step can be:
  //  - a failure in which case we will have an list of at least one exception
  //  - a success with a value (None)
  //  - a success without a value (Some)
  // For convenience, trickle provide the ioMap operator which unwrap the Validation and the Option
  val integer = input |> { _.ioMap(_.toInt) }

  // Increment the Int.
  val flow = integer |> { _.ioMap(_ + 1) }

  // Execute the flow defined above with input "41".
  // Executing a flow returns the result of the flow execution and the final execution state which contain the result
  // of all the steps the flow is made of.
  val (result, state) = flow.execute(input -> "41")

  // The result of the flow execution is Success(Some(43)) which is of type StepIO[Int]
  println(s"Flow result: ${result}")

  // You can retrieve the result of an intermediary step by looking it up in the state returned by the execution.
  println(s"Result of the integer step: ${state.get(integer)}")


  // A same flow can be executed many times with different input.
  // You can even execute the same flow concurrently as the flow in itself doesn't keep any information about the execution.
  // In the execution below the input String cannot be converted to an Int
  val (errorResult, errorState) = flow.execute(input -> "foo")

  // The integer step failed, the failure is propagated all the way to the result of the final step.
  println(s"Flow error: ${errorResult}")
}

object parallelFlow extends App {
  import scalaz.syntax.apply._

  val inputString = source[String]
  val inputIntAsString = source[String]

  val branch1 = inputString |> { _.ioMap(_ + " rocks ") }
  val branch2 = inputIntAsString |> { _.ioMap(_.toInt) } |> { _.ioMap(_ * 2) }

  // We want to combine the results produced by the two branches.
  val flow = (branch1, branch2) |> { (s, i) => (s |@| i) { case (os, oi) => Some(os.get * oi.get) } }

  // Using the executeParallel method will execute branch1 and branch2 at the same time.
  // If we used execute, branch1 would have been executed first and then branch2 would have been executed.
  val (result, state) = flow.executeParallel(inputString -> "trickle", inputIntAsString -> "2")

  println(s"Flow result: ${result}")
}

object conditionalBranching extends App {
  val inputString1 = source[String]
  val inputString2 = source[String]
  val inputCondition = source[Int]

  val flow = inputCondition |< { _.ioMap[Step[String]] { i => if (i == 0) inputString1 else inputString2 } }

  val (result, state) = flow.execute(inputString1 -> "foo", inputString2 -> "bar", inputCondition -> 0)

  println(s"Flow result: ${result}")
}

object traversable extends App {
  val input = source[Seq[String]]

  val flow = input |> { _ ioMap { _ map(_.toInt) }}

  val (result, state) = flow.execute(input -> List("1", "2", "3", "foo"))

  // Foo cannot be converted to an Int and the entire flow fails and we will lose the result
  // of the conversion of 1, 2 and 3.
  println(s"Flow result: ${result}")

  // If we lift the content of the input to a StepIO and then use the operator ||> , the flow execution won't
  // fail entirely, only the conversion of foo will result in a failure.
  val flow2 = input |> { _ ioMap { _ map toIO[String] }} ||> { _ ioMap(_.toInt) }

  val (result2, state2) = flow2.execute(input -> List("1", "2", "3", "foo"))

  println(s"Flow result: ${result2}")

}