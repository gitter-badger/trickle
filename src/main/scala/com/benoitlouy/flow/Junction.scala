package com.benoitlouy.flow

trait Junction[C, O] {
  def switchOn(c: C): OutputStep[O]
}
