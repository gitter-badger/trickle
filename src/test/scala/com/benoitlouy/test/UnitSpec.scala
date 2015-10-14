package com.benoitlouy.test

import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, FlatSpec}
import org.typelevel.scalatest.ValidationMatchers

abstract class UnitSpec extends FlatSpec with Matchers with PropertyChecks with ValidationMatchers
