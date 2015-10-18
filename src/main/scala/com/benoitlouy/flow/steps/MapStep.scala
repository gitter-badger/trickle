package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor
import com.benoitlouy.flow.visitors.execute.ValidationNelException

class MapStep[I, O](val parent: OutputStep[I], val mapper: I => O) extends InputOutputStep[I, O] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

object MapStep {
  def apply[I, O](parent: OutputStep[I], mapper: I => O): MapStep[I, O] = new MapStep(parent, mapper)
  def unapply[I, O](mapStep: MapStep[I, O]) = Some((mapStep.parent, mapStep.mapper))
}
