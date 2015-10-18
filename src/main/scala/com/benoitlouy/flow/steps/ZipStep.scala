package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor
import shapeless.{HList, HNil}
import shapeless.::
import shapeless.syntax.std.product._

abstract class ZipStep[T <: HList, I, O](zipper: I => O) extends InputOutputStep[I, O]{
  type parentType = T
  def parents: T

  override val mapper = zipper
}

class Zip2Step[I1, I2, O](parentTple: (OutputStep[I1], OutputStep[I2]), zipper: (Option[I1], Option[I2]) => O)
  extends ZipStep[OutputStep[I1] :: OutputStep[I2] :: HNil, (Option[I1], Option[I2]), O](zipper.tupled) {

  override val parents = parentTple.productElements

  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Zip3Step[I1, I2, I3, O](parentTuple: (OutputStep[I1], OutputStep[I2], OutputStep[I3]), zipper: (Option[I1], Option[I2], Option[I3]) => O)
  extends ZipStep[OutputStep[I1] :: OutputStep[I2] :: OutputStep[I3] :: HNil, (Option[I1], Option[I2], Option[I3]), O](zipper.tupled) {

  override val parents = parentTuple.productElements

  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}
