package com.benoitlouy.flow

import shapeless.poly._
import shapeless.{HMapBuilder, Poly1}

import scala.collection
import scala.collection.concurrent


class HMap[R[_, _]](protected[HMap] val underlying : collection.Map[Any, Any] = Map.empty) extends Poly1 {
  def get[K, V](k : K)(implicit ev : R[K, V]) : Option[V] = underlying.get(k).asInstanceOf[Option[V]]

  def +[K, V](kv : (K, V))(implicit ev : R[K, V]) : HMap[R] = new HMap[R](underlying+kv)
  def -[K](k : K) : HMap[R] = new HMap[R](underlying-k)

  def ++(other: HMap[R]): HMap[R] = new HMap[R](underlying ++ other.underlying)

  implicit def caseRel[K, V](implicit ev : R[K, V]) = Case1[this.type, K, V](get(_).get)
}

object HMap {
  def apply[R[_, _]] = new HMapBuilder[R]

  def empty[R[_, _]] = new HMap[R]
  def empty[R[_, _]](underlying : Map[Any, Any]) = new HMap[R](underlying)
}

class MutableHMap[R[_, _]](override val underlying: concurrent.Map[Any, Any] = concurrent.TrieMap.empty) extends HMap[R](underlying) {
  def +=[K, V](kv: (K, V))(implicit ev: R[K, V]): MutableHMap[R] = {
    underlying += kv
    this
  }

  def -=[K](k: K): MutableHMap[R] = {
    underlying -= k
    this
  }

  def ++=(other: HMap[R]): MutableHMap[R] = {
    underlying ++= other.underlying
    this
  }
}
