package com.benoitlouy.workflow

import com.benoitlouy.workflow.step._

trait Visitor[T] {
  type stateType = T
  def visit[O](step: SourceStep[O], state: stateType): stateType
  def visit[I, O](step: Apply1Step[I, O], state: stateType): stateType
  def visit[I1, I2, O](step: Apply2Step[I1, I2, O], state: stateType): stateType
  def visit[I1, I2, I3, O](step: Apply3Step[I1, I2, I3, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, O](step: Apply4Step[I1, I2, I3, I4, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, O](step: Apply5Step[I1, I2, I3, I4, I5, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, O](step: Apply6Step[I1, I2, I3, I4, I5, I6, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, O](step: Apply7Step[I1, I2, I3, I4, I5, I6, I7, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, O](step: Apply8Step[I1, I2, I3, I4, I5, I6, I7, I8, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, O](step: Apply9Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O](step: Apply10Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O](step: Apply11Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O](step: Apply12Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O](step: Apply13Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O](step: Apply14Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O](step: Apply15Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O](step: Apply16Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O](step: Apply17Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O](step: Apply18Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O](step: Apply19Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O](step: Apply20Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O](step: Apply21Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O], state: stateType): stateType
  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O](step: Apply22Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O], state: stateType): stateType
  def visit[I, O](step: JunctionStep[I, O], state: stateType): stateType
}
