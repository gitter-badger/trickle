package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.steps.{Zip2Step, MapStep, SourceStep, OutputStep}
import com.benoitlouy.flow.visitors.Visitor
import shapeless.{HList, ~?>, HMap, Generic}
import scalaz._
import Scalaz._
import shapeless.syntax.std.function._
import shapeless.ops.function._

class ExecuteVisitor extends Visitor[HMap[(OutputStep ~?> StepResult)#λ]] {

  implicit val SO1 = ~?>.rel[OutputStep, StepResult]

  override def visit[O](sourceStep: SourceStep[O], state: HMap[~?>[OutputStep, StepResult]#λ]): HMap[~?>[OutputStep, StepResult]#λ] = {
    getOption(state, sourceStep) match {
      case None => put(state, sourceStep, StepResult(InputMissingException("missing input").failureNel[O]))
      case _ => state
    }
  }


  override def visit[I, O](mapStep: MapStep[I, O], state: HMap[~?>[OutputStep, StepResult]#λ]): HMap[~?>[OutputStep, StepResult]#λ] = {
    val parentState = mapStep.parent.accept(this, state)
    val parentResult: StepResult[I] = get(parentState, mapStep.parent)
    val result: ValidationNel[Exception, O] = parentResult.result match {
      case Failure(NonEmptyList(e)) => e.failureNel[O]
      case Success(e) => mapSafe(mapStep.mapper, e)
    }
    put(parentState, mapStep, StepResult(result))
  }

  def applyProduct[P <: Product, F, L <: HList, R](p: P)(f: F)(implicit gen: Generic.Aux[P, L], fp: FnToProduct.Aux[F, L => R]) =
    f.toProduct(gen.to(p))


  override def visit[I1, I2, O](zipStep: Zip2Step[I1, I2, O], state: HMap[~?>[OutputStep, StepResult]#λ]): HMap[~?>[OutputStep, StepResult]#λ] = {
    val parent1State = zipStep.parents._1.accept(this, state)
    val parent2State = zipStep.parents._2.accept(this, state)

    applyProduct(zipStep.parents)((_: OutputStep[I1]).accept(this, state) + (_: OutputStep[I2]).accept(this, state) )
    state
  }

  def mapSafe[I, O](mapper: I => O, e: I) = {
    try {
      mapper(e).successNel
    } catch {
      case e: Exception => e.failureNel[O]
    }
  }

  def put[O](state: HMap[~?>[OutputStep, StepResult]#λ], step: OutputStep[O], stepResult: StepResult[O]) = {
    state + (step, stepResult)
  }

  def getOption[O](state: HMap[~?>[OutputStep, StepResult]#λ], step: OutputStep[O]): Option[StepResult[O]] = state.get(step)
  def get[O](state: HMap[~?>[OutputStep, StepResult]#λ], step: OutputStep[O]): StepResult[O] = getOption(state, step).get

  def execute[O](step: OutputStep[O], input: HMap[~?>[OutputStep, StepResult]#λ]): StepResult[O] = {
    get(step.accept(this, input), step)
  }

}

object ExecuteVisitor {
  def apply[O](step: OutputStep[O], input: Map[OutputStep[_], _]) =
    new ExecuteVisitor().execute(step, new HMap[~?>[OutputStep, StepResult]#λ](input.asInstanceOf[Map[Any, Any]]))
}