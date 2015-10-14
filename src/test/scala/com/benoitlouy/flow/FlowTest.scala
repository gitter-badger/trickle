package com.benoitlouy.flow

import com.benoitlouy.flow
import com.benoitlouy.test.UnitSpec
import com.benoitlouy.flow.Steps._

import scalaz.ValidationNel

class FlowTest extends UnitSpec {

  "Executing a SourceStep" should "return the input" in {
    val input = 1
    val source = SourceStep[Int]()
    val executor = new Executor(Map(source -> input))
    val result = executor.execute(source)
    result should beSuccess(input)
//    source to {
//      _.toString
//    }
  }

  "Executing a MapStep" should "return the mapped result" in {
    val input = 1
    val source = SourceStep[Int]()
    val toString = source to {
      _.toString
    }
    val result = new Executor(Map(source -> input)).execute(toString)
    result should beSuccess(input.toString)
  }

}
