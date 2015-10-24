package com.benoitlouy.flow.steps

object StepOperators {

  implicit class MapOperatorConverter[I](val parent: OptionStep[I]) extends AnyVal {
    def |>[O](mapper: StepIO[I] => StepIO[O]): Zip1Step[I, O] = new Zip1Step(parent, mapper)
  }

  implicit class Zip2OperatorConverter[I1, I2](val parents: (OptionStep[I1], OptionStep[I2])) extends AnyVal {
    def |>[O](zipper: (StepIO[I1], StepIO[I2]) => StepIO[O]): Zip2Step[I1, I2, O] = new Zip2Step(parents, zipper)
  }

  implicit class Zip3OperatorConverter[I1, I2, I3](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3])) extends AnyVal {
    def |>[O](zipper: (StepIO[I1], StepIO[I2], StepIO[I3]) => StepIO[O]): Zip3Step[I1, I2, I3, O] = new Zip3Step(parents, zipper)
  }
}
