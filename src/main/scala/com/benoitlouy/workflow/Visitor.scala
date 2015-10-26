package com.benoitlouy.workflow

import com.benoitlouy.workflow.step._

trait Visitor[T] {
  type stateType = T
  def visit[O](sourceStep: SourceStep[O], state: stateType): stateType
  def visit[I, O](zipStep: Apply1Step[I, O], state: stateType): stateType
  def visit[I1, I2, O](zipStep: Apply2Step[I1, I2, O], state: stateType): stateType
  def visit[I1, I2, I3, O](zipStep: Apply3Step[I1, I2, I3, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, O](zipStep: Apply4Step[I1, I2, I3, I4, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, O](zipStep: Apply5Step[I1, I2, I3, I4, I5, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, O](zipStep: Apply6Step[I1, I2, I3, I4, I5, I6, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, O](zipStep: Apply7Step[I1, I2, I3, I4, I5, I6, I7, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, O](zipStep: Apply8Step[I1, I2, I3, I4, I5, I6, I7, I8, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, O](zipStep: Apply9Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O](zipStep: Apply10Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O](zipStep: Apply11Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O](zipStep: Apply12Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O](zipStep: Apply13Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O](zipStep: Apply14Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O](zipStep: Apply15Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O](zipStep: Apply16Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O](zipStep: Apply17Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O](zipStep: Apply18Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O](zipStep: Apply19Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O](zipStep: Apply20Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O](zipStep: Apply21Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O], state: stateType): stateType
//  def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O](zipStep: Apply22Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O], state: stateType): stateType
}
