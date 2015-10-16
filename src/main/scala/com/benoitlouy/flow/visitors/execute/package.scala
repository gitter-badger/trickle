package com.benoitlouy.flow.visitors

import scalaz.ValidationNel

package object execute {
  type ValidationNelException[T] = ValidationNel[Exception, T]
}
