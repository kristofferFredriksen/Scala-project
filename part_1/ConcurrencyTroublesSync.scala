package example

object ConcurrencyTroubles {
  private var value1: Int = 1000
  private var value2: Int = 0
  private var sum: Int = 0

  private val lock = new AnyRef

  private def moveOneUnit(): Unit = lock.synchronized {
    value1 -= 1
    value2 += 1
    if (value1 == 0) {
      value1 = 1000
      value2 = 0
    }
    sum = value1 + value2
  }

  private def updateSum(): Unit = lock.synchronized {
    sum = value1 + value2
  }

  private def snapshot(): (Int, Int, Int) = lock.synchronized {
    (sum, value1, value2)
  }

  private def execute(): Unit = {
    while (true) {
      moveOneUnit()
      Thread.sleep(50)
    }
  }

  def main(args: Array[String]): Unit = {
    for (_ <- 1 to 2) {
      val thread = new Thread {
        override def run(): Unit = execute()
      }
      thread.start()
    }

    while (true) {
      updateSum() 
      val (s, v1, v2) = snapshot()
      println(s"$s [$v1 $v2]")
      Thread.sleep(10)
    }
  }
}
