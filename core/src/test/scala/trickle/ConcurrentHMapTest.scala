package trickle

import shapeless.{~?>, Id}

class ConcurrentHMapTest extends UnitSpec {

  "A ConcurrentHMap" should "be mutable" in {
    val map = ConcurrentHMap.empty[(Option ~?> Id)#λ]

    map += (Option(1), 1)
    map += (Option("foo"), "foo")

    map.get(Option(1)) shouldBe Some(1)
    map.get(Option("foo")) shouldBe Some("foo")

    val other = ConcurrentHMap.empty[(Option ~?> Id)#λ]
    other += (Option("bar"), "baz")

    map ++= other

    map.get(Option("bar")) shouldBe Some("baz")
    map.get(Option("foo")) shouldBe Some("foo")
    other.get(Option(1)) shouldBe None
  }
}
