package com.benoitlouy.flow

import shapeless.poly._
import shapeless.{HMapBuilder, Poly1}


class HMap[R[_, _]](val underlying : Map[Any, Any] = Map.empty) extends Poly1 {
  def get[K, V](k : K)(implicit ev : R[K, V]) : Option[V] = underlying.get(k).asInstanceOf[Option[V]]

  def +[K, V](kv : (K, V))(implicit ev : R[K, V]) : HMap[R] = new HMap[R](underlying+kv)
  def -[K](k : K) : HMap[R] = new HMap[R](underlying-k)

  def +(o: HMap[R[_, _]]): new HMap[R](underlying + )

  implicit def caseRel[K, V](implicit ev : R[K, V]) = Case1[this.type, K, V](get(_).get)
}

object HMap {
  def apply[R[_, _]] = new HMapBuilder[R]

  def empty[R[_, _]] = new HMap[R]
  def empty[R[_, _]](underlying : Map[Any, Any]) = new HMap[R](underlying)
}
