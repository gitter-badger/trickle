package com.benoitlouy.flow

import com.benoitlouy.flow.visitors.Visitor

import scalaz.ValidationNel

trait OutputStep[O] {
  type output = O
  def accept[T](v: Visitor[T], state: T): T
}

trait InputOutputStep[I, O] extends OutputStep[O] {
  type input = I
  def mapper: (I => O)
}

case class SourceStep[O]() extends OutputStep[O] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class MapStep[I, O](val parent: OutputStep[I], val mapper: I => O) extends InputOutputStep[I, O] {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

object MapStep {
  def unapply[I, O](mapStep: MapStep[I, O]) = Some((mapStep.parent, mapStep.mapper))
}

class Zip2Step[I1, I2, O]( parent1: => OutputStep[I1], parent2: => OutputStep[I2], zipper: (I1, I2) => O) extends InputOutputStep[(I1, I2), O] {
  override def mapper: ((I1, I2)) => O = zipper.tupled

  override def accept[T](v: Visitor[T], state: T): T = state
}

object Steps {
  def map[I, O](parent: OutputStep[I])(mapper: => (I => O)): MapStep[I, O] = new MapStep[I, O](parent, mapper)

  def zip[I1, I2, O](parent1: => OutputStep[I1], parent2: => OutputStep[I2])(zipper: => ((I1, I2) => O)): Zip2Step[I1, I2, O] =
    new Zip2Step[I1, I2, O](parent1, parent2, zipper)

  implicit class MapOperatorConverter[I](val parent: OutputStep[I]) extends AnyVal {
    def map[O](mapper: I => O): MapStep[I, O] = new MapStep[I, O](parent, mapper)
    def map[O](mapper: => (I => O)): MapStep[I, O] = map(mapper)
  }



}
