
class Account(val code : String, val balance: Double) {
    private val lock = new Object
    private val transactionPool = new TransactionPool()

    // Implement functions. Account should be immutable.
    // Change return type to the appropriate one
    def withdraw (amount: Double) : Either[String, Account] = 
        if (amount > balance) Left("Insufficient funds")
        else if (amount < 0) Left("Withdrawal amount must be positive")
        else Right(new Account(code, balance - amount))

    def deposit (amount: Double) : Either[String, Account] = 
        if (amount < 0) Left("Deposit amount must be positive")
        else Right(new Account(code, balance + amount))
}
