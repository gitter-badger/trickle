package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step._
import com.benoitlouy.test.UnitSpec
import ExecutorOperators._
import StepOperators._
import StepIOOperators._
import scalaz._
import Scalaz._

class ParallelExecuteVisitorTest extends UnitSpec {

  "An ExecuteVisitor" should "return input when executing SourceStep" in {
    val source = SourceStep[Int]()

    val result = source.executeParallel(source -> 1)

    result shouldBe Success(Some(1))
  }

  it should "return a failure when executing SourceStep and input is missing" in {
    val source = SourceStep[Int]()

    val result = source.executeParallel()

    result shouldBe failure
  }

  it should "execute mapping step" in {
    val source = SourceStep[Int]()

    val flow = source |> { _ mMap { _.toString } }

    val result = flow.executeParallel(source -> 1)

    result shouldBe Success(Some("1"))
  }

  it should "fail when executing mapping step and input is missing" in {
    val source = SourceStep[Int]()

    val f: StepIO[Int] => StepIO[String] = _ mMap { _.toString }

    val flow = source |> { f }

    val result = flow.executeParallel()

    result shouldBe failure

    val Failure(NonEmptyList(InputMissingException(message))) = result

    message shouldBe "missing input"
  }

  it should "fail when mapping step throws" in {
    val source = SourceStep[Int]()

    val flow: Apply1Step[Int, String] = source |> { x => throw new RuntimeException("error") }

    val result = flow.executeParallel(source -> 1)

    result shouldBe failure

    val Failure(NonEmptyList(e)) = result

    e.getMessage shouldBe "error"
  }

  it should "fail when mapping step fails" in {
    val source = SourceStep[Int]()

    val flow = source |> { x => new RuntimeException("error").failure[String] }

    val result = flow.executeParallel(source -> 1)

    result shouldBe failure

    val Failure(NonEmptyList(e)) = result

    e.getMessage shouldBe "error"
  }

  it should "execute chained mapping steps" in {
    val source = SourceStep[Int]()

    val inc = (_: StepIO[Int]) mMap { _ + 1 }

    val flow = source |> { inc } |> { inc } |> { inc }

    val result = flow.executeParallel(source -> 1)

    result shouldBe Success(Some(4))
  }

  it should "execute zip step" in {
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[String]()

    val flow = (source1, source2) |> { (i, s) =>  (i |@| s) { case (a, b) => Some(b.get * a.get) }  }

    val result = flow.executeParallel(
      source1 -> 3,
      source2 -> "foo")

    result shouldBe Success(Some("foo" * 3))
  }


  it should "execute complex flow" in {
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[String]()

    val flow = (source1 |> { _ mMap { _ * 2 } },
      source2 |> { _ mMap { s => s + s } }, source1) |> {
      (i, s, j) => (i |@| s |@| j) { case (a, b, c) => Some(b.get * (a.get + c.get))}
    }

    val result = flow.executeParallel(
      source1 -> 3,
      source2 -> "foo")

    result shouldBe Success(Some("foo" * 18))
  }

  it should "execute each step only once and reuse the output" in {
    val source = SourceStep[Int]()

    var count = 0
    val branch = source |> { _ mMap { x => count += 1; count } }
    val flow = (branch, branch) |> { (a, b) => (a |@| b) { case (a, b) => Some((a.get, b.get) )}}

    val result = flow.executeParallel(source -> 1)

    result shouldBe Success(Some((1, 1)))
  }

  it should "not block on diamond topology" in {
    val source = SourceStep[Int]()

    val root = source |> { _ mMap { _ + 1 }}
    val b1 = root |> { _ mMap { _ * 2 }} |> { _ mMap { _.toString }}
    val b2 = root |> { _ mMap { _ * 3 }}

    val flow = (b1, b2) |> { (b1, b2) => (b1 |@| b2) { case (a, b) => Some((a.get, b.get))}}

    val result = flow.executeParallel(source -> 1)
    result shouldBe Success(Some(("4", 6)))
  }

  it should "return failure when parents steps fails" in {
    val source = SourceStep[Int]()

    val branch1 = source |> { _ mMap { _ + 1 }} |> { x => new RuntimeException("error1").failure[String] }
    val branch2: Apply1Step[Int, String] = source |> { x => throw new RuntimeException("error2")}
    val flow = (branch1, branch2) |> { (a, b) => (a |@| b) { case (a, b) => Some(a.get + b.get)}}

    val result = flow.executeParallel(source -> 42)

    val Failure(NonEmptyList(ex1, ex2)) = result
    ex1.getMessage shouldBe "error1"
    ex2.getMessage shouldBe "error2"
  }

  it should "execute apply step with up to 22 inputs" in {
    val source = SourceStep[Int]()
    
  }
}
