package com.benoitlouy.flow

trait OutputStep[O] {
  type output = O
}

trait InputOutputStep[I, O] extends OutputStep[O] {
  type input = I
  def mapper: (I => O)
}

case class SourceStep[O]() extends OutputStep[O]

class MapStep[I, O](val parent: Any => OutputStep[I], val mapper: I => O) extends InputOutputStep[I, O]

object MapStep {
  def unapply[I, O](mapStep: MapStep[I, O]) = Some((mapStep.parent, mapStep.mapper))
}

class Zip2Step[I1, I2, O]( parent1: => OutputStep[I1], parent2: => OutputStep[I2], zipper: (I1, I2) => O) extends InputOutputStep[(I1, I2), O] {
  override def mapper: ((I1, I2)) => O = zipper.tupled
}

object Steps {
  def map[I, O](parent: OutputStep[I])(mapper: => (I => O)): MapStep[I, O] = map(_ => parent)(mapper)
  def map[I, O](parent: Any => OutputStep[I])(mapper: => (I => O)): MapStep[I, O] =
    new MapStep[I, O](parent, mapper)

  def zip[I1, I2, O](parent1: => OutputStep[I1], parent2: => OutputStep[I2])(zipper: => ((I1, I2) => O)): Zip2Step[I1, I2, O] =
    new Zip2Step[I1, I2, O](parent1, parent2, zipper)

  implicit class MapOperatorConverter[I](val parent: Any => OutputStep[I]) extends AnyVal {
    def to[O](mapper: I => O): MapStep[I, O] = new MapStep[I, O](parent, mapper)
    def to[O](mapper: => (I => O)): MapStep[I, O] = to(mapper)
  }

  implicit class MapOperatorConverter2[I](val parent: OutputStep[I]) extends AnyVal {
    def to[O](mapper: I => O): MapStep[I, O] = new MapStep[I, O](_ => parent, mapper)
    def to[O](mapper: => (I => O)): MapStep[I, O] = to(mapper)
  }


}
