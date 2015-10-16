package com.benoitlouy.flow

import com.benoitlouy.flow.steps.OutputStep

trait Junction[C, O] {
  def switchOn(c: C): OutputStep[O]
}
