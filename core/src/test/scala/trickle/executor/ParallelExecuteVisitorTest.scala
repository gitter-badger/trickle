package trickle.executor

import trickle.syntax.executor._
import trickle.syntax.step._
import trickle.step._
import shapeless.syntax.std.tuple._

class ParallelExecuteVisitorTest extends  ExecutorSpec[ParallelState] {

  override def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): (StepIO[O], ParallelState) = step.executeParallel(input:_*)

  it should "not starve for thread" in {
    val integer = source[Int]

    val b1 = integer |> { _ mMap { _ + 1 }}
    val b2 = integer |> { _ mMap { _ + 1 }}
    val b3 = integer |> { _ mMap { _ + 1 }}
    val b4 = integer |> { _ mMap { _ + 1 }}
    val b5 = integer |> { _ mMap { _ + 1 }}
    val b6 = integer |> { _ mMap { _ + 1 }}

    val m1 = (b1, b2) |> { (b1,b2) => (b1,b2).foldLeft(0.successIO)(sum) }
    val m2 = (m1, b1, b2) |> { (m1,b1,b2) => (m1,b1,b2).foldLeft(0.successIO)(sum) }
    val m3 = (m1, b1, b2) |> { (m1,b1,b2) => (m1,b1,b2).foldLeft(0.successIO)(sum) }
    val m4 = (m2, m1, m3, b1, b2) |> { (m2,m1,m3,b1,b2) => (m2,m1,m3,b1,b2).foldLeft(0.successIO)(sum) }

    val result = m4.executeParallel(integer -> 1)
  }


}
