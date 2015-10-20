package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.steps._
import com.benoitlouy.test.UnitSpec
import StepOperators._
import scalaz._

class ExecuteVisitorTest extends UnitSpec {

  "An ExecuteVisitor" should "return input when executing SourceStep" in {
    val source = SourceStep[Int]()

    val result = ExecuteVisitor(source, source -> 1)

    result.result should beSuccess(1)
  }

  it should "return a failure when executing SourceStep and input is missing" in {
    val source = SourceStep[Int]()

    val result = ExecuteVisitor(source)

    result.result shouldBe failure
  }

  it should "execute mapping step" in {
    val source = SourceStep[Int]()

    val flow = source map { (_: Option[Int]).get.toString }

    val result = ExecuteVisitor(flow, source -> 1)

    result.result should beSuccess("1")
  }

  it should "fail when executing mapping step and input is missing" in {
    val source = SourceStep[Int]()

    val flow = source map { (_: Option[Int]).get.toString }

    val result = ExecuteVisitor(flow)

    result.result shouldBe failure

    val StepResult(Failure(NonEmptyList(InputMissingException(message)))) = result

    message shouldBe "missing input"
  }

  it should "fail when mapping step throws" in {
    val source = SourceStep[Int]()

    val flow: Zip1Step[Int, String] = source map { (i: Option[Int]) => throw new RuntimeException("error") }

    val result = ExecuteVisitor(flow, source -> 1)

    result.result shouldBe failure

    val StepResult(Failure(NonEmptyList(e))) = result

    e.getMessage shouldBe "error"
  }

  it should "execute chained mapping steps" in {
    val source = SourceStep[Int]()

    val inc = (i: Option[Int]) => i.get + 1

    val flow = source map { inc } map { inc } map { inc }

    val result = ExecuteVisitor(flow, source -> 1)

    result.result should beSuccess(4)
  }

  it should "execute zip step" in {
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[String]()

    val flow = (source1, source2) zip { (i: Option[Int], s: Option[String]) =>  s.get * i.get }

    val result = ExecuteVisitor(flow,
      source1 -> 3,
      source2 -> "foo")

    result.result should beSuccess("foo" * 3)
  }

  it should "execute complex flow" in {
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[String]()

    val flow = (source1 map { i: Option[Int] => i.get * 2 },
      source2 map { s: Option[String] => s.get + s.get}, source1) zip {
      (i: Option[Int], s: Option[String], j: Option[Int]) => s.get * (i.get + j.get)
    }

    val result = ExecuteVisitor(flow,
      source1 -> 3,
      source2 -> "foo")

    result.result should beSuccess("foo" * 18)
  }
}
