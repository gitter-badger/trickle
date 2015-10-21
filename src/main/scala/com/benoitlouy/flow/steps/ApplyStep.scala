package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor
import com.benoitlouy.flow.visitors.execute._
import shapeless.{::, HNil, HList}
import shapeless.syntax.std.product._

abstract class ApplyStep[T <: HList, I, O](val parents: T, val zipper: I => ValidationNelException[O]) extends InputOutputStep[I, O]{
  type parentType = T
  override val mapper: I => ValidationNelException[O] = zipper
}

class Zip1Step[I, O](parent: OptionStep[I], zipper: ValidationNelException[I] => ValidationNelException[O]) extends ApplyStep(parent :: HNil, zipper) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Zip2Step[I1, I2, O](parents: (OptionStep[I1], OptionStep[I2]), zipper: (ValidationNelException[I1], ValidationNelException[I2]) => ValidationNelException[O])
  extends ApplyStep(parents.productElements, zipper.tupled) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Zip3Step[I1, I2, I3, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3]), zipper: (ValidationNelException[I1], ValidationNelException[I2], ValidationNelException[I3]) => ValidationNelException[O])
  extends ApplyStep(parents.productElements, zipper.tupled) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}
