package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.HMap
import com.benoitlouy.flow.steps._
import com.benoitlouy.flow.visitors.Visitor
import shapeless._
import scalaz.Failure
import scalaz.Success
import scalaz.NonEmptyList
import scalaz.syntax.validation._
import shapeless.syntax.std.tuple._
import shapeless.syntax.std.tuple.productTupleOps
import shapeless.poly._

class ExecuteVisitor extends Visitor[HMap[(OutputStep ~?> StepResult)#λ]] {

  implicit val SO1 = ~?>.rel[OutputStep, StepResult]

  override def visit[O](sourceStep: SourceStep[O], state: stateType): stateType = {
    getOption(state, sourceStep) match {
      case None => put(state, sourceStep, StepResult(InputMissingException("missing input").failureNel[O]))
      case _ => state
    }
  }

  override def visit[I, O](mapStep: MapStep[I, O], state: stateType): stateType = {
    val parentState = mapStep.parent.accept(this, state)
    val parentResult: StepResult[I] = get(parentState, mapStep.parent)
    val result: ValidationNelException[O] = parentResult.result match {
      case Failure(NonEmptyList(e)) => e.failureNel[O]
      case Success(e) => mapSafe(mapStep.mapper, e)
    }
    put(parentState, mapStep, StepResult(result))
  }

//  def applyProduct[P <: Product, F, L <: HList, R](p: P)(f: F)(implicit gen: Generic.Aux[P, L], fp: FnToProduct.Aux[F, L => R]) =
//    f.toProduct(gen.to(p))

  override def visit[I1, I2, O](zipStep: Zip2Step[I1, I2, O], state: stateType): stateType = {
    val self = this
    object visitParents extends (OutputStep ~>> stateType) {
      override def apply[T](f: OutputStep[T]): stateType = f.accept(self, state)
    }
    object combineStates extends Poly {
      implicit def caseState = use((s1: stateType, s2: stateType) => s1 ++ s2)
    }

    val parentStates = zipStep.parents map visitParents
    val newState = parentStates.foldLeft(state)(combineStates)
    object getResult extends (OutputStep ~> Option) {
      override def apply[T](f: OutputStep[T]) = get(newState, f).result match {
        case Failure(NonEmptyList(e)) => None
        case Success(e) => Some(e)
      }
    }
    val parentResults = zipStep.parents map getResult

    put(state, zipStep, StepResult(new RuntimeException("not executed").failureNel[O]))
  }

//  override def visit[T <: HList, O](zipStep: ZipStep[T, O], state: stateType): stateType = {
//    val self = this
//    object visitParents extends (OutputStep ~>> stateType) {
//      override def apply[S](f: OutputStep[S]): stateType = f.accept(self, state)
//    }
//    object combineStates extends Poly {
//      implicit def caseState = use((s1: stateType, s2: stateType) => s1 ++ s2)
//    }
//
//    val newState = zipStep.parents map visitParents reduceLeft combineStates
//
//    object getResult extends (OutputStep ~> ValidationNelException) {
//      override def apply[T](f: OutputStep[T]): ValidationNelException[T] = get(newState, f).result
//    }
//
//    val parentResult = zipStep.parents map getResult
//
//  }

//  override def visit[I1, I2, I3, O](zipStep: Zip3Step[I1, I2, I3, O], state: stateType): stateType = state

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
    val state = step.accept(this, input)
    get(state, step)
  }

}

object ExecuteVisitor {
  def apply[O](step: OutputStep[O], input: Map[OutputStep[_], _]) =
    new ExecuteVisitor().execute(step, new HMap[~?>[OutputStep, StepResult]#λ](input.asInstanceOf[Map[Any, Any]]))
}