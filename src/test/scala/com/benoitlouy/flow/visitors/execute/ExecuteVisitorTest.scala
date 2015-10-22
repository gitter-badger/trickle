package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.steps._
import com.benoitlouy.flow.steps.StepIOOps._
import com.benoitlouy.test.UnitSpec
import StepOperators._
import scalaz._
import Scalaz._

class ExecuteVisitorTest extends UnitSpec {

  "An ExecuteVisitor" should "return input when executing SourceStep" in {
    val source = SourceStep[Int]()

    val result = ExecuteVisitor(source, source -> 1)

    val res: StepIO[Int] = result.result
    result.result shouldBe Success(Some(1))
  }

  it should "return a failure when executing SourceStep and input is missing" in {
    val source = SourceStep[Int]()

    val result = ExecuteVisitor(source)

    result.result shouldBe failure
  }

  it should "execute mapping step" in {
    val source = SourceStep[Int]()

    val flow = source map { _ mMap { _.toString } }

    val result = ExecuteVisitor(flow, source -> 1)

    result.result shouldBe Success(Some("1"))
  }

  it should "fail when executing mapping step and input is missing" in {
    val source = SourceStep[Int]()

    val f: StepIO[Int] => StepIO[String] = _ mMap { _.toString }

    val flow = source map { f }

    val result = ExecuteVisitor(flow)

    result.result shouldBe failure

    val StepResult(Failure(NonEmptyList(InputMissingException(message)))) = result

    message shouldBe "missing input"
  }

  it should "fail when mapping step throws" in {
    val source = SourceStep[Int]()

    val flow: Zip1Step[Int, String] = source map { x => throw new RuntimeException("error") }

    val result = ExecuteVisitor(flow, source -> 1)

    result.result shouldBe failure

    val StepResult(Failure(NonEmptyList(e))) = result

    e.getMessage shouldBe "error"
  }

  it should "fail when mapping step fails" in {
    val source = SourceStep[Int]()

    val flow = source map { x => new RuntimeException("error").failure }

    val result = ExecuteVisitor(flow, source -> 1)

    result.result shouldBe failure

    val StepResult(Failure(NonEmptyList(e))) = result

    e.getMessage shouldBe "error"
  }

  it should "execute chained mapping steps" in {
    val source = SourceStep[Int]()

    val inc = (_: StepIO[Int]) mMap { _ + 1 }

    val flow = source map { inc } map { inc } map { inc }

    val result = ExecuteVisitor(flow, source -> 1)

    result.result shouldBe Success(Some(4))
  }

  it should "execute zip step" in {
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[String]()

    val flow = (source1, source2) zip { (i,  s) =>  (i |@| s) { case (a, b) => Some(b.get * a.get) }  }

    val result = ExecuteVisitor(flow,
      source1 -> 3,
      source2 -> "foo")

    result.result shouldBe Success(Some("foo" * 3))
  }

  it should "execute complex flow" in {
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[String]()

    val flow = (source1 map { _ mMap { _ * 2 } },
      source2 map { _ mMap { s => s + s } }, source1) zip {
      (i, s, j) => (i |@| s |@| j) { case (a, b, c) => Some(b.get * (a.get + c.get))}
    }

    val result = ExecuteVisitor(flow,
      source1 -> 3,
      source2 -> "foo")

    result.result shouldBe Success(Some("foo" * 18))
  }

  it should "execute each step only once and reuse the output" in {
    val source = SourceStep[Int]()

    var count = 0
    val branch = source map { _ mMap { x => count += 1; count } }
    val flow = (branch, branch) zip { (a, b) => (a |@| b) { case (x, y) => Some((x.get, y.get) )}}

    val result = ExecuteVisitor(flow, source -> 1)

    result.result shouldBe Success(Some((1, 1)))
  }
}
