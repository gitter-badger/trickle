package trickle.executor

import java.util.concurrent.Executors

import trickle.{Visitor, ConcurrentHMap}
import trickle.step._
import trickle.step.StepIOOperators._
import org.apache.commons.pool2.{PooledObject, BaseKeyedPooledObjectFactory}
import org.apache.commons.pool2.impl.{GenericKeyedObjectPoolConfig, DefaultPooledObject, GenericKeyedObjectPool}
import shapeless._

class ParallelState(val content: ConcurrentHMap[(OptionStep ~?> StepResult)#λ]) extends ExecutorState[ParallelState] {

  private object lockPoolFactory extends BaseKeyedPooledObjectFactory[OutputStep[_], Any] {
    override def wrap(value: Any): PooledObject[Any] = new DefaultPooledObject[Any](value)
    override def create(key: OutputStep[_]): AnyRef = AnyRef
  }
  private object lockPoolConfig extends GenericKeyedObjectPoolConfig {
    setMaxTotalPerKey(1)
  }
  private object lockPool extends GenericKeyedObjectPool(lockPoolFactory, lockPoolConfig)

  implicit object constraint extends (OptionStep ~?> StepResult)

  def get[O](k: OptionStep[O]): Option[StepResult[O]] = content.get(k)



  override def put[O](step: OptionStep[O], stepResult: StepResult[O]): ParallelState = {
    content += (step, stepResult)
    this
  }

  override def putAll(other: ParallelState): ParallelState = {
    content ++= other.content
    this
  }

  def processStep[O](step: OptionStep[O])(f: => ParallelState): ParallelState = {
    val lock = lockPool.borrowObject(step)
    try {
      get(step) match {
        case None => f
        case _ => this
      }
    } finally {
      lockPool.returnObject(step, lock)
    }
  }
}

class ParallelExecuteVisitor extends ParallelExecutionUtils[ParallelState] with Visitor[ParallelState] with Executor[ParallelState] { self =>

  implicit val executor = Executors.newCachedThreadPool()

  override def visit[I, O](step: Apply1Step[I, O], state: stateType): stateType = process(step, state)

  override def visit[I1, I2, O](step: Apply2Step[I1, I2, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, O](step: Apply3Step[I1, I2, I3, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, O](step: Apply4Step[I1, I2, I3, I4, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, O](step: Apply5Step[I1, I2, I3, I4, I5, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, O](step: Apply6Step[I1, I2, I3, I4, I5, I6, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, O](step: Apply7Step[I1, I2, I3, I4, I5, I6, I7, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, O](step: Apply8Step[I1, I2, I3, I4, I5, I6, I7, I8, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, O](step: Apply9Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O](step: Apply10Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O](step: Apply11Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O](step: Apply12Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O](step: Apply13Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O](step: Apply14Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O](step: Apply15Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O](step: Apply16Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O](step: Apply17Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O](step: Apply18Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O](step: Apply19Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O](step: Apply20Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O](step: Apply21Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O](step: Apply22Step[I1, I2, I3, I4, I5, I6, I7, I8, I9, I10, I11, I12, I13, I14, I15, I16, I17, I18, I19, I20, I21, I22, O], state: stateType): stateType = processParallel(step, state)

  override def visit[I, O](step: SeqStep[I, O], state: stateType): stateType = state

  def execute[O](step: OptionStep[O], input: (OptionStep[_], Any)*): (StepIO[O], ParallelState) = {
    val m = Map(input:_*) mapValues {
      case None => StepResult(None.successIO)
      case Some(e) => StepResult(e.successIO)
      case e => StepResult(e.successIO)
    }
    val inputState = new ParallelState(new ConcurrentHMap[~?>[OptionStep, StepResult]#λ](m.asInstanceOf[Map[Any, Any]]))
    val state = step.accept(this, inputState)
    (state.get(step).get.result, state)
  }
}
