package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor
import shapeless.{::, HNil, HList}
import shapeless.syntax.std.product._

abstract class ZipStep[T <: HList, I, O](val parents: T, val zipper: I => O) extends InputOutputStep[I, O]{
  type parentType = T
  override val mapper = zipper
}

class Zip1Step[I, O](parent: OutputStep[I], zipper: Option[I] => O) extends ZipStep(parent :: HNil, zipper) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Zip2Step[I1, I2, O](parents: (OutputStep[I1], OutputStep[I2]), zipper: (Option[I1], Option[I2]) => O)
  extends ZipStep(parents.productElements, zipper.tupled) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Zip3Step[I1, I2, I3, O](parents: (OutputStep[I1], OutputStep[I2], OutputStep[I3]), zipper: (Option[I1], Option[I2], Option[I3]) => O)
  extends ZipStep(parents.productElements, zipper.tupled) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}
