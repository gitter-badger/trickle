package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.steps._
import com.benoitlouy.test.UnitSpec
import com.benoitlouy.flow.visitors.execute.ToValidationNelExOps._
import StepOperators._
import scalaz._
import Scalaz._

class ExecuteVisitorTest extends UnitSpec {

  "An ExecuteVisitor" should "return input when executing SourceStep" in {
    val source = SourceStep[Int]()

    val result = ExecuteVisitor(source, source -> 1)

    val res: ValidationNelException[Int] = result.result
    result.result shouldBe Success(Some(1))
  }

  it should "return a failure when executing SourceStep and input is missing" in {
    val source = SourceStep[Int]()

    val result = ExecuteVisitor(source)

    result.result shouldBe failure
  }

  it should "execute mapping step" in {
    val source = SourceStep[Int]()

//    val flow = source map { _.map { _.map { _.toString } } }
    val flow = source map { _ oMap { _.toString } }

    val result = ExecuteVisitor(flow, source -> 1)

    result.result shouldBe Success(Some("1"))
  }

  it should "fail when executing mapping step and input is missing" in {
    val source = SourceStep[Int]()

    val f: ValidationNelException[Int] => ValidationNelException[String] = _ oMap { _.toString }

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

  it should "execute chained mapping steps" in {
    val source = SourceStep[Int]()

    val inc = (_: ValidationNelException[Int]) oMap { _ + 1 }

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

    val flow = (source1 map { _ oMap { _ * 2 } },
      source2 map { _ oMap { s => s + s } }, source1) zip {
      (i, s, j) => (i |@| s |@| j) { case (a, b, c) => Some(b.get * (a.get + c.get))}
    }

    val result = ExecuteVisitor(flow,
      source1 -> 3,
      source2 -> "foo")

    result.result shouldBe Success(Some("foo" * 18))
  }
}
