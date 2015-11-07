package trickle

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
  // For convenience, trickle provide the mMap operator which unwrap the Validation and the Option
  val integer = input |> { _.mMap(_.toInt) }

  // Increment the Int.
  val flow = integer |> { _.mMap(_ + 1) }

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

  val flow = (
    inputString |> { _.mMap(_ + " rocks ") },
    inputIntAsString |> { _.mMap(_.toInt) } |> { _.mMap(_ * 2) }
    ) |> { (s, i) => (s |@| i) { case (os, oi) => Some(os.get * oi.get) } }

  val (result, state) = flow.executeParallel(inputString -> "trickle", inputIntAsString -> "2")

  println(result)
}

