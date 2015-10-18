//import shapeless.examples.StackOverflow3.Input
//import shapeless.{HNil, HList, Id}
//import shapeless.poly._
//


import shapeless._
import poly._
import ops.hlist.{ Mapped, Mapper }
import shapeless.ops.hlist.Mapper.Aux
import syntax.std.function._

case class Input[T](value: T)

trait Output[T] {
  def access: T
}

class OutputString(s: String) extends Output[String] {
  override def access: String = s
}

class OutputInt(i: Int) extends Output[Int] {
  override def access: Int = i * 3
}

object value extends (Input ~> Id) {
  def apply[T](i : Input[T]) = i.value
}

object MyGet extends (Output ~>> Int) {
  def apply[T](f: Output[T]) = 42
}

class Preprocessor[In <: HList, Out <: HList, R](ctor : Out => R)
                                                (implicit
                                                 mapped: Mapped.Aux[Out, Input, In],
                                                 mapper: Mapper.Aux[value.type, In, Out]
                                                ) {
  def apply(in : In) = ctor(in map value)
}

object combine extends Poly {
  implicit def caseFoo[A, B] = use((f1: Int, f2: Int) => f1 + f2)
}

class Preprocessor2[In <: HList, Out <: HList, R](f: Out => R)
                                                 (implicit
                                                  mapped: Mapped.Aux[Out, Output, In],
                                                  mapper: Mapper.Aux[MyGet.type, In, Out]
                                                 ) {
  def apply(in : In) = (in map MyGet).reduceLeft(combine)
}

case class Foo(input1 : Int, input2 : Int)
//object FooBuilder extends Preprocessor((Foo.apply _).toProduct)
object FooBuilder2 extends Preprocessor2((Foo.apply _).toProduct)
//val foo = FooBuilder(Input(23) :: Input("foo") :: HNil)
case class Bar(input1 : Int, input2 : String, input3 : Boolean)
object BarBuilder extends Preprocessor((Bar.apply _).toProduct)
val bar = BarBuilder(Input(23) :: Input("foo") :: Input(true) :: HNil)



val l = new OutputInt(41) :: new OutputInt(1) :: HNil

val foo2 = FooBuilder2(l)

//




//object MyBuilder extends Preprocessor2((Foo.apply _).toProduct)
//
//MyBuilder(l)