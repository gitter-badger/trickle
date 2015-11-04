package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step._
import ExecutorOperators._
import StepOperators._
import StepIOOperators._
import shapeless.syntax.std.tuple._

import scalaz.{Failure, NonEmptyList, Success}

class ParallelExecuteVisitorTest extends  ExecutorSpec[ParallelState] {

  override def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): (StepIO[O], ParallelState) = step.executeParallel(input:_*)

  it should "not starve for thread" in {
    val source1 = SourceStep[Int]()

    val b1 = source1 |> { _ mMap { _ + 1 }}
    val b2 = source1 |> { _ mMap { _ + 1 }}
    val b3 = source1 |> { _ mMap { _ + 1 }}
    val b4 = source1 |> { _ mMap { _ + 1 }}
    val b5 = source1 |> { _ mMap { _ + 1 }}
    val b6 = source1 |> { _ mMap { _ + 1 }}

    val m1 = (b1, b2) |> { (b1,b2) => (b1,b2).foldLeft(0.successIO)(sum) }
    val m2 = (m1, b1, b2) |> { (m1,b1,b2) => (m1,b1,b2).foldLeft(0.successIO)(sum) }
    val m3 = (m1, b1, b2) |> { (m1,b1,b2) => (m1,b1,b2).foldLeft(0.successIO)(sum) }
    val m4 = (m2, m1, m3, b1, b2) |> { (m2,m1,m3,b1,b2) => (m2,m1,m3,b1,b2).foldLeft(0.successIO)(sum) }

    m4.executeParallel(source1 -> 1)
  }


}
