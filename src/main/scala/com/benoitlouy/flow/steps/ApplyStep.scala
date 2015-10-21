package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor
import com.benoitlouy.flow.visitors.execute._
import shapeless.{::, HNil, HList}
import shapeless.syntax.std.product._

abstract class ApplyStep[T <: HList, I, O](val parents: T, val zipper: I => StepIO[O]) extends InputOutputStep[I, O]{
  type parentType = T
  override val mapper: I => StepIO[O] = zipper
}

class Zip1Step[I, O](parent: OptionStep[I], zipper: StepIO[I] => StepIO[O]) extends ApplyStep(parent :: HNil, zipper) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Zip2Step[I1, I2, O](parents: (OptionStep[I1], OptionStep[I2]), zipper: (StepIO[I1], StepIO[I2]) => StepIO[O])
  extends ApplyStep(parents.productElements, zipper.tupled) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Zip3Step[I1, I2, I3, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3]), zipper: (StepIO[I1], StepIO[I2], StepIO[I3]) => StepIO[O])
  extends ApplyStep(parents.productElements, zipper.tupled) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}
