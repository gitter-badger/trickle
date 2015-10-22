package com.benoitlouy.flow.visitors.execute

import com.benoitlouy.flow.steps._
import com.benoitlouy.flow.visitors.Visitor
import org.apache.commons.pool2.{PooledObject, BaseKeyedPooledObjectFactory}
import org.apache.commons.pool2.impl.{GenericKeyedObjectPoolConfig, DefaultPooledObject, GenericKeyedObjectPool}

class State {

  object StepLockPoolFactory extends BaseKeyedPooledObjectFactory[OutputStep[_], Any] {
    override def wrap(value: Any): PooledObject[Any] = new DefaultPooledObject[Any](value)

    override def create(key: OutputStep[_]): AnyRef = AnyRef
  }

  object StepLockPoolConfig extends GenericKeyedObjectPoolConfig {
    setMaxTotalPerKey(1)
  }

  object StepLockPool extends GenericKeyedObjectPool[OutputStep[_], Any](StepLockPoolFactory, StepLockPoolConfig)

  def processStep[O](step: OutputStep[O], f: => Unit) = {
    val lock = StepLockPool.borrowObject(step)
    try {
      f
    } finally {
      StepLockPool.returnObject(step, lock)
    }
  }
}

class ParallelExecuteVisitor extends Visitor[State] {
  override def visit[O](sourceStep: SourceStep[O], state: stateType): stateType = state

  override def visit[I, O](zipStep: Zip1Step[I, O], state: stateType): stateType = state

  override def visit[I1, I2, O](zipStep: Zip2Step[I1, I2, O], state: stateType): stateType = state

  override def visit[I1, I2, I3, O](zipStep: Zip3Step[I1, I2, I3, O], state: stateType): stateType = state
}
