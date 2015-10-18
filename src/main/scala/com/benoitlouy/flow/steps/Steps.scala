package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor

trait OutputStep[O] {
  def accept[T](v: Visitor[T], state: T): T
}

trait InputOutputStep[I, O] extends OutputStep[O] {
  def mapper: (I => O)
}

object Steps {

  implicit class MapOperatorConverter[I](val parent: OutputStep[I]) extends AnyVal {
    def map[O](mapper: I => O): MapStep[I, O] = MapStep(parent, mapper)
    def map[O](mapper: => (I => O)): MapStep[I, O] = map(mapper)
  }

  implicit class Zip2OperatorConverter[I1, I2](val parents: (OutputStep[I1], OutputStep[I2])) extends AnyVal {
    def zip[O](zipper: (Option[I1], Option[I2]) => O): Zip2Step[I1, I2, O] = new Zip2Step(parents, zipper)
    def zip[O](zipper: => ((Option[I1], Option[I2]) => O)): Zip2Step[I1, I2, O] = zip(zipper)
  }

  implicit class Zip3OperatorConverter[I1, I2, I3](val parents: (OutputStep[I1], OutputStep[I2], OutputStep[I3])) extends AnyVal {
    def zip[O](zipper: (Option[I1], Option[I2], Option[I3]) => O): Zip3Step[I1, I2, I3, O] = new Zip3Step(parents, zipper)
    def zip[O](zipper: => ((Option[I1], Option[I2], Option[I3]) => O)): Zip3Step[I1, I2, I3, O] = zip(zipper)
  }
}
