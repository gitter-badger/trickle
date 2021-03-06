package trickle

import shapeless.poly._
import shapeless.Poly1

class HMap[R[_, _]](val underlying: collection.Map[Any, Any] = Map.empty) extends Poly1 {
  def get[K, V](k: K)(implicit ev: R[K, V]): Option[V] = underlying.get(k).asInstanceOf[Option[V]]

  def +[K, V](kv: (K, V))(implicit ev : R[K, V]) : HMap[R] = new HMap[R](underlying+kv)
  def -[K](k: K): HMap[R] = new HMap[R](underlying-k)

  def ++(other: HMap[R]): HMap[R] = new HMap[R](underlying ++ other.underlying)

  implicit def caseRel[K, V](implicit ev: R[K, V]) = Case1[this.type, K, V](get(_).get)
}

object HMap {
  def empty[R[_, _]]: HMap[R] = new HMap[R]
  def empty[R[_, _]](underlying: Map[Any, Any]): HMap[R] = new HMap[R](underlying)
}


