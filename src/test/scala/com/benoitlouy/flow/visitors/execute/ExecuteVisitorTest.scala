package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.{MapStep, SourceStep}
import com.benoitlouy.test.UnitSpec
import com.benoitlouy.flow.Steps._
import scalaz._
import Scalaz._

class ExecuteVisitorTest extends UnitSpec {

  "An ExecuteVisitor" should "return input when executing SourceStep" in {
    val source = SourceStep[Int]()

    val result = ExecuteVisitor(source, Map(source -> StepResult[Int](1.successNel)))

    result.result should beSuccess(1)
  }

  it should "return a failure when executing SourceStep and input is missing" in {
    val source = SourceStep[Int]()

    val result = ExecuteVisitor(source, Map())

    result.result shouldBe failure
  }

  it should "execute mapping step" in {
    val source = SourceStep[Int]()

    val flow = source map { (_: Int).toString }

    val result = ExecuteVisitor(flow, Map(source -> StepResult[Int](1.successNel)))

    result.result should beSuccess("1")
  }

  it should "fail when executing mapping step and input is missing" in {
    val source = SourceStep[Int]()

    val flow = source map { (_: Int).toString }

    val result = ExecuteVisitor(flow, Map())

    result.result shouldBe failure

    val StepResult(Failure(NonEmptyList(InputMissingException(message)))) = result

    message shouldBe "missing input"
  }

  it should "fail when mapping step throws" in {
    val source = SourceStep[Int]()

    val flow: MapStep[Int, String] = source map { (i: Int) => throw new RuntimeException("error") }

    val result = ExecuteVisitor(flow, Map(source -> StepResult[Int](1.successNel)))

    result.result shouldBe failure

    val StepResult(Failure(NonEmptyList(e))) = result

    e.getMessage shouldBe "error"
  }

  it should "execute chained mapping steps" in {
    val source = SourceStep[Int]()

    val inc = (i: Int) => i + 1

    val flow = source map { inc } map { inc } map { inc }

    val result = ExecuteVisitor(flow, Map(source -> StepResult[Int](1.successNel)))

    result.result should beSuccess(4)
  }
}
