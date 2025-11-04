import collection.mutable.Map

class Bank(val allowedAttempts: Integer = 3) {

    private val accountsRegistry : Map[String,Account] = Map()

    val transactionsPool: TransactionPool = new TransactionPool()
    val completedTransactions: TransactionPool = new TransactionPool()


    def processing : Boolean = !transactionsPool.isEmpty

    // TODO
    // Adds a new transaction for the transfer to the transaction pool
    def transfer(from: String, to: String, amount: Double): Unit = ???

    // TODO
    // Process the transactions in the transaction pool
    // The implementation needs to be completed and possibly fixed
    def processTransactions: Unit = {

        // val workers : List[Thread] = transactionsPool.iterator.toList
        //                                        .filter(/* select only pending transactions */)
        //                                        .map(processSingleTransaction)

        // workers.map( element => element.start() )
        // workers.map( element => element.join() )

        /* TODO: change to select only transactions that succeeded */
        // val succeded : List[Transaction] = transactionsPool

        /* TODO: change to select only transactions that failed */
        // val failed : List[Transaction] = transactionsPool

        // succeded.map(/* remove transactions from the transaction pool */)
        // succeded.map(/* add transactions to the completed transactions queue */)

        //failed.map(t => { 
            /*  transactions that failed need to be set as pending again; 
                if the number of retry has exceeded they also need to be removed from
                the transaction pool and to be added to the queue of completed transactions */
        //})

        if(!transactionsPool.isEmpty) {
            processTransactions
        }
    }

    private def processSingleTransaction(t: Transaction): Thread = {
        val worker = new Runnable {
            override def run(): Unit = {
                var attemptsLeft = allowedAttempts
                var completed = false

                while (!completed && attemptsLeft > 0) {
                    val success = Bank.this.synchronized {
                        (accountsRegistry.get(t.from), accountsRegistry.get(t.to)) match {
                            case (Some(fromAccount), Some(toAccount)) if t.amount > 0 && fromAccount.balance >= t.amount =>
                                accountsRegistry.update(t.from, new Account(fromAccount.code, fromAccount.balance - t.amount))
                                accountsRegistry.update(t.to, new Account(toAccount.code, toAccount.balance + t.amount))
                                true
                            case _ => false
                        }
                    }

                    if (success) {
                        completed = true
                    } else {
                        attemptsLeft -= 1
                    }
                }
            }
        }

        new Thread(worker)
    }


    // TODO
    // Creates a new account and returns its code to the user.
    // The account is stored in the local registry of bank accounts.
    def createAccount(initialBalance: Double) : String = ???


    // TODO
    // Return information about a certain account based on its code.
    // Remember to handle the case in which the account does not exist
    def getAccount(code : String) : Option[Account] = ???
}
