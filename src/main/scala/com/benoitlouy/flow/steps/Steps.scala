package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.Visitor
import com.benoitlouy.flow.visitors.execute.ValidationNelException

trait OutputStep[O] {
  def accept[T](v: Visitor[T], state: T): T
}

trait InputOutputStep[I, O] extends OutputStep[O] {
  def mapper: (I => O)
}

object Steps {

  def zip[I1, I2, O](parent1: => OutputStep[I1], parent2: => OutputStep[I2])(zipper: => ((I1, I2) => O)): Zip2Step[I1, I2, O] =
    new Zip2Step[I1, I2, O](parent1, parent2, zipper)

  implicit class MapOperatorConverter[I](val parent: OutputStep[I]) extends AnyVal {
    def map[O](mapper: I => O): MapStep[I, O] = MapStep(parent, mapper)
    def map[O](mapper: => (I => O)): MapStep[I, O] = map(mapper)
  }

 implicit class Zip2OperatorConverter[I1, I2](val parents: (OutputStep[I1], OutputStep[I2])) extends AnyVal {
   def zip[O](zipper: (I1, I2) => O): Zip2Step[I1, I2, O] = new Zip2Step(parents._1, parents._2, zipper)
   def zip[O](zipper: => ((I1, I2) => O)): Zip2Step[I1, I2, O] = zip(zipper)
 }
}
