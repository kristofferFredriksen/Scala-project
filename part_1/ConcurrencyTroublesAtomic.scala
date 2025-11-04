package example

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.LockSupport
import scala.annotation.tailrec

object ConcurrencyTroublesLockFreeOnce {
  final case class State(value1: Int, value2: Int) { def sum: Int = value1 + value2 }
  final case class Versioned(state: State, ver: Long)

  private val ref = new AtomicReference(Versioned(State(1000, 0), 0L))

  @tailrec
  private def moveOneUnit(): Unit = {
    val cur = ref.get()
    val s   = cur.state
    val nextState =
      if (s.value1 == 1) State(1000, 0) else State(s.value1 - 1, s.value2 + 1)
    val next = Versioned(nextState, cur.ver + 1)
    if (!ref.compareAndSet(cur, next)) moveOneUnit()
  }

  private def execute(): Unit = {
    while (true) {
      moveOneUnit()
      Thread.sleep(50)
    }
  }

  def main(args: Array[String]): Unit = {
    for (_ <- 1 to 2) new Thread(() => execute()).start()

    var seen = -1L
    while (true) {
      val v = ref.get()
      if (v.ver != seen) {
        val s = v.state
        println(s"${s.sum} [${s.value1} ${s.value2}]")
        seen = v.ver
      } else {
        LockSupport.parkNanos(1_000_000) // ~1 ms backoff
      }
    }
  }
}
