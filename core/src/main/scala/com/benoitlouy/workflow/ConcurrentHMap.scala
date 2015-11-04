package com.benoitlouy.workflow

import scala.collection.concurrent.TrieMap

class ConcurrentHMap[R[_, _]](init: Map[Any, Any] = Map.empty) extends HMap[R](underlying = TrieMap.empty ++= init) {
  override val underlying = TrieMap.empty ++= init
  def +=[K, V](kv: (K, V))(implicit ev: R[K, V]): ConcurrentHMap[R] = {
    underlying += kv
    this
  }

  def -=[K](k: K): ConcurrentHMap[R] = {
    underlying -= k
    this
  }

  def ++=(other: HMap[R]): ConcurrentHMap[R] = {
    underlying ++= other.underlying
    this
  }
}

object ConcurrentHMap {
  def empty[R[_, _]]: ConcurrentHMap[R] = new ConcurrentHMap[R]
  def empty[R[_, _]](underlying: Map[Any, Any]): ConcurrentHMap[R] = new ConcurrentHMap[R](underlying)
}
