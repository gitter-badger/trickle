package com.benoitlouy.workflow.step

import com.benoitlouy.workflow.Visitor
import shapeless.{HNil, HList, ::}
import shapeless.syntax.std.product._
import shapeless.syntax.std.function._


abstract class ApplyStep[T <: HList, I, O](val parents: T, val f: I => StepIO[O]) extends OptionStep[O] { //InputOutputStep[I, O]{
  type parentsType = T
  type inputType = I
}

class Apply1Step[I, O](parentI: OptionStep[I],
                       fI: StepIO[I] => StepIO[O])
  extends ApplyStep(parentI :: HNil, fI.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply2Step[I1, I2, O](parentsI: (OptionStep[I1], OptionStep[I2]),
                            f: (StepIO[I1], StepIO[I2]) => StepIO[O])
  extends ApplyStep(parentsI.productElements, f.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

class Apply3Step[I1, I2, I3, O](parentsI: (OptionStep[I1], OptionStep[I2], OptionStep[I3]),
                                fI: (StepIO[I1], StepIO[I2], StepIO[I3]) => StepIO[O])
  extends ApplyStep(parentsI.productElements, fI.toProduct) {
  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
}

//class Apply4Step[I1, I2, I3, I4, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4]),
//                                    zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply5Step[I1, I2, I3, I4, I5, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5]),
//                                        zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply6Step[I1, I2, I3, I4, I5, I6, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6]),
//                                            zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply7Step[I1, I2, I3, I4, I5, I6, I7, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7]),
//                                                zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply8Step[I1, I2, I3, I4, I5, I6, I7, I8, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8]),
//                                                    zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply9Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9]),
//                                                        zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply10Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10]),
//                                                              zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply11Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11]),
//                                                                   zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply12Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12]),
//                                                                        zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply13Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13]),
//                                                                             zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply14Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14]),
//                                                                             zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply15Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15]),
//                                                                                       zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply16Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16]),
//                                                                                            zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply17Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17]),
//                                                                                                 zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply18Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17], OptionStep[I18]),
//                                                                                                      zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply19Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17], OptionStep[I18], OptionStep[I19]),
//                                                                                                           zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply20Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17], OptionStep[I18], OptionStep[I19], OptionStep[I20]),
//                                                                                                                zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19], StepIO[I20]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply21Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17], OptionStep[I18], OptionStep[I19], OptionStep[I20], OptionStep[I21]),
//                                                                                                                     zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19], StepIO[I20], StepIO[I21]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
//
//class Apply22Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O](parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17], OptionStep[I18], OptionStep[I19], OptionStep[I20], OptionStep[I21], OptionStep[I22]),
//                                                                                                                          zipper: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19], StepIO[I20], StepIO[I21], StepIO[I22]) => StepIO[O])
//  extends ApplyStep(parents.productElements, zipper.tupled) {
//  override def accept[T](v: Visitor[T], state: T): T = v.visit(this, state)
//}
