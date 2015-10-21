package com.benoitlouy.flow.steps

object StepOperators {

  implicit class MapOperatorConverter[I](val parent: OptionStep[I]) extends AnyVal {
    def map[O](mapper: Option[I] => Option[O]): Zip1Step[I, O] = new Zip1Step(parent, mapper)
//    def map[O](mapper: => (Option[I] => Option[O])): Zip1Step[I, O] = map(mapper)
  }

  implicit class Zip2OperatorConverter[I1, I2](val parents: (OptionStep[I1], OptionStep[I2])) extends AnyVal {
    def zip[O](zipper: (Option[I1], Option[I2]) => Option[O]): Zip2Step[I1, I2, O] = new Zip2Step(parents, zipper)
    def zip[O](zipper: => ((Option[I1], Option[I2]) => Option[O])): Zip2Step[I1, I2, O] = zip(zipper)
  }

  implicit class Zip3OperatorConverter[I1, I2, I3](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3])) extends AnyVal {
    def zip[O](zipper: (Option[I1], Option[I2], Option[I3]) => Option[O]): Zip3Step[I1, I2, I3, O] = new Zip3Step(parents, zipper)
    def zip[O](zipper: => ((Option[I1], Option[I2], Option[I3]) => Option[O])): Zip3Step[I1, I2, I3, O] = zip(zipper)
  }
}
