package trickle.step.syntax

import trickle.step._

import scala.collection.GenTraversableLike
import scala.collection.generic.CanBuildFrom

trait StepOperators {

  def source[T](): SourceStep[T] = new SourceStep[T]()

  implicit class Apply1OperatorConverter[I](val parent: OptionStep[I]) {
    def |>[O](f: StepIO[I] => StepIO[O]) = new Apply1Step(parent, f)
  }

  implicit class Apply2OperatorConverter[I1, I2](val parents: (OptionStep[I1], OptionStep[I2])) {
    def |>[O](f: (StepIO[I1], StepIO[I2]) => StepIO[O]) = new Apply2Step(parents, f)
  }

  implicit class Apply3OperatorConverter[I1, I2, I3](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3]) => StepIO[O]) = new Apply3Step(parents, f)
  }

  implicit class Apply4OperatorConverter[I1, I2, I3, I4](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4]) => StepIO[O]) = new Apply4Step(parents, f)
  }

  implicit class Apply5OperatorConverter[I1, I2, I3, I4, I5](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5]) => StepIO[O]) = new Apply5Step(parents, f)
  }

  implicit class Apply6OperatorConverter[I1, I2, I3, I4, I5, I6](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6]) => StepIO[O]) = new Apply6Step(parents, f)
  }

  implicit class Apply7OperatorConverter[I1, I2, I3, I4, I5, I6, I7](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7]) => StepIO[O]) = new Apply7Step(parents, f)
  }

  implicit class Apply8OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8]) => StepIO[O]) = new Apply8Step(parents, f)
  }

  implicit class Apply9OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9]) => StepIO[O]) = new Apply9Step(parents, f)
  }

  implicit class Apply10OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10]) => StepIO[O]) = new Apply10Step(parents, f)
  }

  implicit class Apply11OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11]) => StepIO[O]) = new Apply11Step(parents, f)
  }

  implicit class Apply12OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12]) => StepIO[O]) = new Apply12Step(parents, f)
  }

  implicit class Apply13OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13]) => StepIO[O]) = new Apply13Step(parents, f)
  }

  implicit class Apply14OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14]) => StepIO[O]) = new Apply14Step(parents, f)
  }

  implicit class Apply15OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15]) => StepIO[O]) = new Apply15Step(parents, f)
  }

  implicit class Apply16OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16]) => StepIO[O]) = new Apply16Step(parents, f)
  }

  implicit class Apply17OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17]) => StepIO[O]) = new Apply17Step(parents, f)
  }

  implicit class Apply18OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17], OptionStep[I18])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18]) => StepIO[O]) = new Apply18Step(parents, f)
  }

  implicit class Apply19OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17], OptionStep[I18], OptionStep[I19])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19]) => StepIO[O]) = new Apply19Step(parents, f)
  }

  implicit class Apply20OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17], OptionStep[I18], OptionStep[I19], OptionStep[I20])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19], StepIO[I20]) => StepIO[O]) = new Apply20Step(parents, f)
  }

  implicit class Apply21OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17], OptionStep[I18], OptionStep[I19], OptionStep[I20], OptionStep[I21])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19], StepIO[I20], StepIO[I21]) => StepIO[O]) = new Apply21Step(parents, f)
  }

  implicit class Apply22OperatorConverter[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22](val parents: (OptionStep[I1], OptionStep[I2], OptionStep[I3], OptionStep[I4], OptionStep[I5], OptionStep[I6], OptionStep[I7], OptionStep[I8], OptionStep[I9], OptionStep[I10], OptionStep[I11], OptionStep[I12], OptionStep[I13], OptionStep[I14], OptionStep[I15], OptionStep[I16], OptionStep[I17], OptionStep[I18], OptionStep[I19], OptionStep[I20], OptionStep[I21], OptionStep[I22])) {
    def |>[O](f: (StepIO[I1], StepIO[I2], StepIO[I3], StepIO[I4], StepIO[I5], StepIO[I6], StepIO[I7], StepIO[I8], StepIO[I9], StepIO[I10], StepIO[I11], StepIO[I12], StepIO[I13], StepIO[I14], StepIO[I15], StepIO[I16], StepIO[I17], StepIO[I18], StepIO[I19], StepIO[I20], StepIO[I21], StepIO[I22]) => StepIO[O]) = new Apply22Step(parents, f)
  }

  implicit class JunctionOperatorConverter[I](val parent: OptionStep[I]) {
    def |<[O](f: StepIO[I] => StepIO[OptionStep[O]]) = new JunctionStep(parent, f)
  }

  implicit class SeqOperatorConverter[I, S[X] <: GenTraversableLike[X, S[X]]](val parent: OptionStep[S[StepIO[I]]]) {
    def ||>[O](f: StepIO[I] => StepIO[O])(implicit bf: CanBuildFrom[S[StepIO[I]], StepIO[O], S[StepIO[O]]]) = new SeqStep(parent, f)
  }
}
