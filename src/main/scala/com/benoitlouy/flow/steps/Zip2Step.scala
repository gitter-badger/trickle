package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor

trait ZipStep[T, O] {
  def parents: T
}

class Zip2Step[I1, I2, O](parent1: OutputStep[I1], parent2: OutputStep[I2], zipper: (I1, I2) => O)
  extends InputOutputStep[(I1, I2), O] with ZipStep[(OutputStep[I1], OutputStep[I2]), O] {

  val parents: (OutputStep[I1], OutputStep[I2]) = (parent1, parent2)

  override val mapper: ((I1, I2)) => O = zipper.tupled

  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}
