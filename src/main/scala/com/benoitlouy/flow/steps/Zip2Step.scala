package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor
import shapeless.{HList, HNil, ::}

trait ZipStep[T <: HList, O] {
  def parents: T
}

class Zip2Step[I1, I2, O](parent1: OutputStep[I1], parent2: OutputStep[I2], zipper: (I1, I2) => O)
  extends InputOutputStep[(I1, I2), O] with ZipStep[(OutputStep[I1] :: OutputStep[I2] :: HNil), O] {

  override val parents = parent1 :: parent2 :: HNil

  override val mapper: ((I1, I2)) => O = zipper.tupled

  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

//class Zip3Step[I1, I2, I3, O](parent1: OutputStep[I1], parent2: OutputStep[I2], parent3: OutputStep[I3], zipper: (I1, I2, I3) => O)
//  extends InputOutputStep[(I1, I2, I3), O] with ZipStep[(OutputStep[I1] :: OutputStep[I2] :: OutputStep[I3] :: HNil), O] {
//
//  override val parents = parent1 :: parent2 :: parent3 :: HNil
//
//  override val mapper = zipper.tupled
//
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
