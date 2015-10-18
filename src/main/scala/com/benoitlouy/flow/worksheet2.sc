import shapeless._
import poly._
import ops.hlist.{ Mapped, Mapper }
import shapeless.ops.hlist.Mapper.Aux
import syntax.std.function._

object option extends (Id ~> Option) {
  def apply[T](t : T) = Option(t)
}

object id extends (Option ~> Id) {
  override def apply[T](f: Option[T]): Id[T] = f.get
}

object State extends (Id ~>> Map[String, Int]) {
  override def apply[T](f: Id[T]): Map[String, Int] = Map(f.toString -> f.toString.length)
}
object isDefined extends (Option ~>> Boolean) {
  def apply[T](o : Option[T]) = o.isDefined
}

val l5 = List(1,2,3)
val l6 = l5 map option

l6 map isDefined

//val s = new State(Map("hello" -> 5))

object combine extends Poly {
  implicit def caseFoo = use((f1: Map[String, Int], f2: Map[String, Int]) => f1 ++ f2)
}

(23 :: "foo" :: HNil) map option map id map State reduceLeft(combine)

//val m = Map(1 -> "foo", 2 -> "bar")
//m + (3 -> "bar")