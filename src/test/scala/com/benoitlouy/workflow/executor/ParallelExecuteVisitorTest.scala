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

  it should "execute conditional branching" in {
    val switch = SourceStep[Int]()
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[Int]()

    val cond: StepIO[Int] => StepIO[OptionStep[Int]] = { x: StepIO[Int] =>
      x mMap { y =>
        if (y < 0) throw new RuntimeException("cannot process negative input")
        if (y == 0) source1 else source2
      }
    }

    val flow = new JunctionStep(switch, cond)

    flow.executeParallel(switch -> None, source1 -> 1, source2 -> 2)._1 shouldBe Success(None)
    flow.executeParallel(switch -> 0, source1 -> 1, source2 -> 2)._1 shouldBe Success(Some(1))
    flow.executeParallel(switch -> 42, source1 -> 1, source2 -> 2)._1 shouldBe Success(Some(2))
    val (Failure(NonEmptyList(e)), state) = flow.executeParallel(switch -> -1, source1 -> 1, source2 -> 2)
    e.getMessage shouldBe "cannot process negative input"
    flow.executeParallel(source1 ->1, source2 -> 2)._1 shouldBe Failure(NonEmptyList(InputMissingException("missing input")))
  }
}
