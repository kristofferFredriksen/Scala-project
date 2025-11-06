object TransactionStatus extends Enumeration {
  val SUCCESS, PENDING, FAILED = Value
}

class TransactionPool {

  private val pool = scala.collection.mutable.Queue[Transaction]()
  private val lock = new Object

     // Remove and the transaction from the pool
    def remove(t: Transaction): Boolean = lock.synchronized {
      pool.dequeueFirst(x => x == t).isDefined
    }

    // Return whether the queue is empty
    def isEmpty: Boolean = pool.isEmpty

    // Return the size of the pool
    def size: Integer = pool.size

    // Add new element to the back of the queue
    def add(t: Transaction): Boolean = lock.synchronized {
      pool.enqueue(t)
      true
    }

    // Return an iterator to allow you to iterate over the queue
    def iterator : Iterator[Transaction] = lock.synchronized {
      pool.iterator
    }

}

class Transaction(val from: String,
                  val to: String,
                  val amount: Double,
                  val retries: Int = 3) {

  private var status: TransactionStatus.Value = TransactionStatus.PENDING
  private var attempts = 0

  def getStatus(): TransactionStatus.Value = status
  def getAttempts(): Int = attempts
  def canRetry(): Boolean = attempts < retries
  
  def markSuccess(): Unit = {
      status = TransactionStatus.SUCCESS
  }

  def markFailed(): Unit = {
      status = TransactionStatus.FAILED
  }
  
  def markPending(): Unit = {
      status = TransactionStatus.PENDING
  }

  def incrementAttempts(): Unit = {
      attempts += 1
  }
}
