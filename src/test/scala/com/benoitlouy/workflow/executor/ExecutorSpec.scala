package com.benoitlouy.workflow.executor

import com.benoitlouy.workflow.step.StepIOOperators._
import com.benoitlouy.workflow.step.StepOperators._
import com.benoitlouy.test.UnitSpec
import com.benoitlouy.workflow.step._
import shapeless.Poly2
import shapeless.syntax.std.tuple._

import scalaz.{NonEmptyList, Failure, Success, Validation}
import scalaz.syntax.apply._

trait ExecutorSpec[S <: ExecutorState[S]] extends UnitSpec {

  def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): (StepIO[O], S)

  "An Executor" should "return input when executing SourceStep" in {
    val source = SourceStep[Int]()

    val (result, state) = execute(source, source -> 1)

    result shouldBe Success(Some(1))
  }

  it should "return a failure when executing SourceStep and input is missing" in {
    val source = SourceStep[Int]()

    val (result, state) = execute(source)

    result shouldBe failure
  }

  it should "execute mapping step" in {
    val source = SourceStep[Int]()

    val flow = source |> { _ mMap { _.toString } }

    val (result, state) = execute(flow, source -> 1)

    result shouldBe Success(Some("1"))
  }

  it should "fail when executing mapping step and input is missing" in {
    val source = SourceStep[Int]()

    val f: StepIO[Int] => StepIO[String] = _ mMap { _.toString }

    val flow = source |> { f }

    val (result, state) = execute(flow)

    result shouldBe failure

    val Failure(NonEmptyList(InputMissingException(message))) = result

    message shouldBe "missing input"
  }

  it should "fail when mapping step throws" in {
    val source = SourceStep[Int]()

    val flow: Apply1Step[Int, String] = source |> { x => throw new RuntimeException("error") }

    val (result, state) = execute(flow, source -> 1)

    result shouldBe failure

    val Failure(NonEmptyList(e)) = result

    e.getMessage shouldBe "error"
  }

  it should "fail when mapping step fails" in {
    val source = SourceStep[Int]()

    val flow = source |> { x => new RuntimeException("error").failureIO[String] }

    val (result, state) = execute(flow, source -> 1)

    result shouldBe failure

    val Failure(NonEmptyList(e)) = result

    e.getMessage shouldBe "error"
  }

  it should "execute chained mapping steps" in {
    val source = SourceStep[Int]()

    val inc = (_: StepIO[Int]) mMap { _ + 1 }

    val flow = source |> { inc } |> { inc } |> { inc }

    val (result, state) = execute(flow, source -> 1)

    result shouldBe Success(Some(4))
  }

  it should "execute zip step" in {
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[String]()

    val flow = (source1, source2) |> { (i, s) =>  (i |@| s) { case (a, b) => Some(b.get * a.get) }  }

    val (result, state) = execute(flow, source1 -> 3, source2 -> "foo")

    result shouldBe Success(Some("foo" * 3))
  }


  it should "execute complex flow" in {
    val source1 = SourceStep[Int]()
    val source2 = SourceStep[String]()

    val flow = (source1 |> { _ mMap { _ * 2 } },
      source2 |> { _ mMap { s => s + s } }, source1) |> {
      (i, s, j) => (i |@| s |@| j) { case (a, b, c) => Some(b.get * (a.get + c.get))}
    }

    val (result, state) = execute(flow, source1 -> 3, source2 -> "foo")

    result shouldBe Success(Some("foo" * 18))
  }

  it should "execute each step only once and reuse the output" in {
    val source = SourceStep[Int]()

    var count = 0
    val branch = source |> { _ mMap { x => count += 1; count } }
    val flow = (branch, branch) |> { (a, b) => (a |@| b) { case (a, b) => Some((a.get, b.get) )}}

    val (result, state) = execute(flow, source -> 1)

    result shouldBe Success(Some((1, 1)))
  }

  it should "not block on diamond topology" in {
    val source = SourceStep[Int]()

    val root = source |> { _ mMap { _ + 1 }}
    val b1 = root |> { _ mMap { _ * 2 }} |> { _ mMap { _.toString }}
    val b2 = root |> { _ mMap { _ * 3 }}

    val flow = (b1, b2) |> { (b1, b2) => (b1 |@| b2) { case (a, b) => Some((a.get, b.get))}}

    val (result, state) = execute(flow, source -> 1)

    result shouldBe Success(Some(("4", 6)))
  }

  it should "return failure when parents steps fails" in {
    val source = SourceStep[Int]()

    val branch1 = source |> { _ mMap { _ + 1 }} |> { x => new RuntimeException("error1").failureIO[String] }
    val branch2: Apply1Step[Int, String] = source |> { x => throw new RuntimeException("error2")}
    val flow = (branch1, branch2) |> { (a, b) =>
      (a |@| b) { case (a, b) => Some(a.get + b.get)}
    }

    val (result, state) = execute(flow, source -> 42)

    val Failure(NonEmptyList(ex1, ex2)) = result
    ex1.getMessage shouldBe "error1"
    ex2.getMessage shouldBe "error2"
  }

  object sum extends Poly2 {
    implicit def caseStepIOInt: Case.Aux[StepIO[Int], StepIO[Int], StepIO[Int]] = at[StepIO[Int], StepIO[Int]] {
      (a, b) => (a |@| b) { (i, j) => Some(i.get + j.get) }
    }
  }

  it should "execute apply step with up to 22 inputs" in {
    val source = SourceStep[Int]()

    def createSteps(count: Int): List[OptionStep[Int]] = {
      if (count > 0) {
        (source |> { x => x }) :: createSteps(count - 1)
      } else {
        Nil
      }
    }

    val steps = createSteps(22)

    val flows = List(
      steps(0) |> { x => x },
      (steps(0), steps(1)) |> { (a,b) => (a,b).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2)) |> { (a,b,c) => (a,b,c).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3)) |> { (a,b,c,d) => (a,b,c,d).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4)) |> { (a,b,c,d,e) => (a,b,c,d,e).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5)) |> { (a,b,c,d,e,f) => (a,b,c,d,e,f).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6)) |> { (a,b,c,d,e,f,g) => (a,b,c,d,e,f,g).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7)) |> { (a,b,c,d,e,f,g,h) => (a,b,c,d,e,f,g,h).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8)) |> { (a,b,c,d,e,f,g,h,i) => (a,b,c,d,e,f,g,h,i).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9)) |> { (a,b,c,d,e,f,g,h,i,j) => (a,b,c,d,e,f,g,h,i,j).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10)) |> { (a,b,c,d,e,f,g,h,i,j,k) => (a,b,c,d,e,f,g,h,i,j,k).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11)) |> { (a,b,c,d,e,f,g,h,i,j,k,l) => (a,b,c,d,e,f,g,h,i,j,k,l).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11), steps(12)) |> { (a,b,c,d,e,f,g,h,i,j,k,l,m) => (a,b,c,d,e,f,g,h,i,j,k,l,m).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11), steps(12), steps(13)) |> { (a,b,c,d,e,f,g,h,i,j,k,l,m,n) => (a,b,c,d,e,f,g,h,i,j,k,l,m,n).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11), steps(12), steps(13), steps(14)) |> { (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o) => (a,b,c,d,e,f,g,h,i,j,k,l,m,n, o).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11), steps(12), steps(13), steps(14), steps(15)) |> { (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p) => (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11), steps(12), steps(13), steps(14), steps(15), steps(16)) |> { (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q) => (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11), steps(12), steps(13), steps(14), steps(15), steps(16), steps(17)) |> { (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r) => (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11), steps(12), steps(13), steps(14), steps(15), steps(16), steps(17), steps(18)) |> { (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s) => (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11), steps(12), steps(13), steps(14), steps(15), steps(16), steps(17), steps(18), steps(19)) |> { (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t) => (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11), steps(12), steps(13), steps(14), steps(15), steps(16), steps(17), steps(18), steps(19), steps(20)) |> { (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u) => (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u).foldLeft(0.successIO)(sum) },
      (steps(0), steps(1), steps(2), steps(3), steps(4), steps(5), steps(6), steps(7), steps(8), steps(9), steps(10), steps(11), steps(12),steps(13), steps(14), steps(15), steps(16), steps(17), steps(18), steps(19), steps(20), steps(21)) |> { (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v) => (a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v).foldLeft(0.successIO)(sum) }
    )

    for ((flow, i) <- flows.zipWithIndex) {
      execute(flow, source -> 1)._1 shouldBe Success(Some(i + 1))
    }
  }

}
