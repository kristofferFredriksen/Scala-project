import collection.mutable.Map
import java.util.UUID

class Bank(val allowedAttempts: Integer = 3) {

    private val accountsRegistry : Map[String,Account] = Map()

    val transactionsPool: TransactionPool = new TransactionPool()
    val completedTransactions: TransactionPool = new TransactionPool()


    def processing : Boolean = !transactionsPool.isEmpty

    // Adds a new transaction for the transfer to the transaction pool
    def transfer(from: String, to: String, amount: Double): Unit = {
        val transaction = new Transaction(from, to, amount)
        transactionsPool.add(transaction)
    }

    // Process the transactions in the transaction pool
    // The implementation needs to be completed and possibly fixed
    def processTransactions: Unit = {
        // Select pending transactions
        val pendingTransactions = transactionsPool.iterator.toList
           .filter(_.getStatus() == TransactionStatus.PENDING)

        // Create one worker thread per transaction
        val workers = pendingTransactions.map(processSingleTransaction)

        // Start and join all threads (so they all finish)
        workers.foreach(_.start())
        workers.foreach(_.join())

        // Separate results
        val succeeded = pendingTransactions.filter(_.getStatus() == TransactionStatus.SUCCESS)
        val failed = pendingTransactions.filter(_.getStatus() == TransactionStatus.FAILED)

        // Move successful transactions
        succeeded.foreach { t =>
            transactionsPool.remove(t)
            completedTransactions.add(t)
        }

        // Handle failed transactions
        failed.foreach { t =>
            t.incrementAttempts()
            if (t.canRetry()) {
                t.markPending()
            } else {
                transactionsPool.remove(t)
                completedTransactions.add(t)
            }
        }

        // Recursively process remaining pending ones
        // Recursively process remaining pending ones
        val stillPending = transactionsPool.iterator.exists(_.getStatus() == TransactionStatus.PENDING)
        if (stillPending) processTransactions
    }

    // The function creates a new thread ready to process
    // the transaction, and returns it as a return value
    private def processSingleTransaction(t: Transaction): Thread = {
        new Thread() {
            override def run(): Unit = {
            accountsRegistry.synchronized {
                val maybeFrom = accountsRegistry.get(t.from)
                val maybeTo   = accountsRegistry.get(t.to)

                (maybeFrom, maybeTo) match {
                case (Some(fromAcc), Some(toAcc)) =>
                    t.incrementAttempts()

                    val withdrawResult = fromAcc.withdraw(t.amount)
                    withdrawResult match {
                    case Right(updatedFrom) =>
                        val depositResult = toAcc.deposit(t.amount)
                        depositResult match {
                        case Right(updatedTo) =>
                            accountsRegistry += (updatedFrom.code -> updatedFrom)
                            accountsRegistry += (updatedTo.code   -> updatedTo)
                            t.markSuccess()
                        case Left(_) =>
                            t.markFailed()
                        }
                    case Left(_) =>
                        t.markFailed()
                    }
                case _ =>
                    t.markFailed()
                }
            }
            }
        }
    }

    // Creates a new account and returns its code to the user.
    // The account is stored in the local registry of bank accounts.
    def createAccount(initialBalance: Double) : String = {
        val code = UUID.randomUUID().toString
        val account = new Account(code, initialBalance)
        accountsRegistry += (code -> account)
        code
    }

    // Return information about a certain account based on its code.
    // Remember to handle the case in which the account does not exist
    def getAccount(code : String) : Option[Account] = {
        accountsRegistry.get(code)
    }
}
