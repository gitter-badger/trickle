package com.benoitlouy.flow.steps

import com.benoitlouy.flow.visitors.execute._

object StepOperators {

  implicit class MapOperatorConverter[I](val parent: OptionStep[I]) extends AnyVal {
    def map[O](mapper: ValidationNelException[I] => ValidationNelException[O]): Zip1Step[I, O] = new Zip1Step(parent, mapper)
  }

  implicit class Zip2OperatorConverter[I1, I2](val parents: (OptionStep[I1], OptionStep[I2])) extends AnyVal {
    def zip[O](zipper: (ValidationNelException[I1], ValidationNelException[I2]) => ValidationNelException[O]): Zip2Step[I1, I2, O] = new Zip2Step(parents, zipper)
  }

  implicit class Zip3OperatorConverter[I1, I2, I3](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3])) extends AnyVal {
    def zip[O](zipper: (ValidationNelException[I1], ValidationNelException[I2], ValidationNelException[I3]) => ValidationNelException[O]): Zip3Step[I1, I2, I3, O] = new Zip3Step(parents, zipper)
  }
}
