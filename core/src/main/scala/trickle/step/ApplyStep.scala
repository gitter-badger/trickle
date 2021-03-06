package trickle.step

import shapeless.{HNil, HList, ::}
import shapeless.syntax.std.product._
import shapeless.syntax.std.function._
import trickle.Visitor


abstract class ApplyStep[T <: HList, I, +O](val parents: T, val f: I => StepIO[O]) extends Step[O] {
  type parentsType = T
  type inputType = I
}

class Apply1Step[I, +O](parent: Step[I],
                       f: StepIO[I] => StepIO[O])
  extends ApplyStep(parent :: HNil, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply2Step[I1, I2, +O](parents: (Step[I1], Step[I2]),
                            f: (StepIO[I1], StepIO[I2]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply3Step[I1, I2, I3, +O](parents: (Step[I1], Step[I2], Step[I3]),
                                f: (StepIO[I1], StepIO[I2], StepIO[I3]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply4Step[I1, I2, I3, I4, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4]),
                                    f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply5Step[I1, I2, I3, I4, I5, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5]),
                                        f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply6Step[I1, I2, I3, I4, I5, I6, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6]),
                                            f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply7Step[I1, I2, I3, I4, I5, I6, I7, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7]),
                                                f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply8Step[I1, I2, I3, I4, I5, I6, I7, I8, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8]),
                                                    f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply9Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9]),
                                                        f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply10Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10]),
                                                              f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply11Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11]),
                                                                   f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply12Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12]),
                                                                        f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply13Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12], Step[I13]),
                                                                             f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply14Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12], Step[I13], Step[I14]),
                                                                                  f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply15Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12], Step[I13], Step[I14], Step[I15]),
                                                                                       f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply16Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12], Step[I13], Step[I14], Step[I15], Step[I16]),
                                                                                            f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply17Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12], Step[I13], Step[I14], Step[I15], Step[I16], Step[I17]),
                                                                                                 f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply18Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12], Step[I13], Step[I14], Step[I15], Step[I16], Step[I17], Step[I18]),
                                                                                                      f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply19Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12], Step[I13], Step[I14], Step[I15], Step[I16], Step[I17], Step[I18], Step[I19]),
                                                                                                           f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply20Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12], Step[I13], Step[I14], Step[I15], Step[I16], Step[I17], Step[I18], Step[I19], Step[I20]),
                                                                                                                f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19], StepIO[I20]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply21Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12], Step[I13], Step[I14], Step[I15], Step[I16], Step[I17], Step[I18], Step[I19], Step[I20], Step[I21]),
                                                                                                                     f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19], StepIO[I20], StepIO[I21]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply22Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, +O](parents: (Step[I1], Step[I2], Step[I3], Step[I4], Step[I5], Step[I6], Step[I7], Step[I8], Step[I9], Step[I10], Step[I11], Step[I12], Step[I13], Step[I14], Step[I15], Step[I16], Step[I17], Step[I18], Step[I19], Step[I20], Step[I21], Step[I22]),
                                                                                                                          f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19], StepIO[I20], StepIO[I21], StepIO[I22]) => StepIO[O])
  extends ApplyStep(parents.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}
