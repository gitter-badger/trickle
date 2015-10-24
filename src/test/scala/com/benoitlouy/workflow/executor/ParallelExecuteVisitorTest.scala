package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step._
import com.benoitlouy.test.UnitSpec
import StepOperators._
import StepIOOps._
import scalaz._
import Scalaz._

class ParallelExecuteVisitorTest extends UnitSpec {

  "An ExecuteVisitor" should "return input when executing SourceStep" in {
    val source = SourceStep[Int]()

    val result = ParallelExecuteVisitor(source, source -> 1)

    result.result shouldBe Success(Some(1))
  }

  it should "return a failure when executing SourceStep and input is missing" in {
    val source = SourceStep[Int]()

    val result = ParallelExecuteVisitor(source)

    result.result shouldBe failure
  }

  it should "execute mapping step" in {
    val source = SourceStep[Int]()

    val flow = source |> { _ mMap { _.toString } }

    val result = ParallelExecuteVisitor(flow, source -> 1)

    result.result shouldBe Success(Some("1"))
  }

  it should "fail when executing mapping step and input is missing" in {
    val source = SourceStep[Int]()

    val f: StepIO[Int] => StepIO[String] = _ mMap { _.toString }

    val flow = source |> { f }

    val result = ParallelExecuteVisitor(flow)

    result.result shouldBe failure

    val StepResult(Failure(NonEmptyList(InputMissingException(message)))) = result

    message shouldBe "missing input"
  }

  it should "fail when mapping step throws" in {
    val source = SourceStep[Int]()

    val flow: Zip1Step[Int, String] = source |> { x => throw new RuntimeException("error") }

    val result = ParallelExecuteVisitor(flow, source -> 1)

    result.result shouldBe failure

    val StepResult(Failure(NonEmptyList(e))) = result

    e.getMessage shouldBe "error"
  }

  it should "fail when mapping step fails" in {
    val source = SourceStep[Int]()

    val flow = source |> { x => new RuntimeException("error").failure }

    val result = ParallelExecuteVisitor(flow, source -> 1)

    result.result shouldBe failure

    val StepResult(Failure(NonEmptyList(e))) = result

    e.getMessage shouldBe "error"
  }

  it should "execute chained mapping steps" in {
    val source = SourceStep[Int]()

    val inc = (_: StepIO[Int]) mMap { _ + 1 }

    val flow = source |> { inc } |> { inc } |> { inc }

    val result = ParallelExecuteVisitor(flow, source -> 1)

    result.result shouldBe Success(Some(4))
  }

  it should "execute zip step" in {
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[String]()

    val flow = (source1, source2) |> { (i, s) =>  (i |@| s) { case (a, b) => Some(b.get * a.get) }  }

    val result = ParallelExecuteVisitor(flow,
      source1 -> 3,
      source2 -> "foo")

    result.result shouldBe Success(Some("foo" * 3))
  }


  it should "execute complex flow" in {
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[String]()

    val flow = (source1 |> { _ mMap { _ * 2 } },
      source2 |> { _ mMap { s => s + s } }, source1) |> {
      (i, s, j) => (i |@| s |@| j) { case (a, b, c) => Some(b.get * (a.get + c.get))}
    }

    val result = ParallelExecuteVisitor(flow,
      source1 -> 3,
      source2 -> "foo")

    result.result shouldBe Success(Some("foo" * 18))
  }

  it should "execute each step only once and reuse the output" in {
    val source = SourceStep[Int]()

    var count = 0
    val branch = source |> { _ mMap { x => count += 1; count } }
    val flow = (branch, branch) |> { (a, b) => (a |@| b) { case (x, y) => Some((x.get, y.get) )}}

    val result = ParallelExecuteVisitor(flow, source -> 1)

    result.result shouldBe Success(Some((1, 1)))
  }
}