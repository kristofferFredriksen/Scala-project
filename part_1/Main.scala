object Main {

  // Function to create an array of integers from 1 to 50
  def task1a(): Array[Int] = {
    val nums = new Array[Int](50)
    for (i <- 0 to 49){
      nums(i) = i + 1
    }
    return nums
  }

  // Iterative sum function
  def task1b(nums: Array[Int]): Int = {
    var sum = 0
    for (i <- nums){
      sum += i
    }
    return sum
  }

  // Recursive sum function
  def task1c(nums: Array[Int]): Int = {
    def rec(i: Int): Int = if (i == nums.length) 0 else nums(i) + rec(i + 1)
    rec(0)
  }

  // Fibonacci function
  def task1d(n: BigInt): BigInt = {
    if (n <= 1) return n
    return task1d(n - 1) + task1d(n - 2)
  }

  // quadratic function
  def task2a_1(a: Int, b: Int, c: Int): Array[Double] = {
    val d = b * b - 4 * a * c
    def realSolution(d: Int): Boolean = {
      return d >= 0
    }
    val coeffs = new Array[Double](2)
    if (!realSolution(d)) {
      println("No real roots")
    } else if (d == 0) {
      coeffs(0) = -b / (2 * a)
      coeffs(1) = coeffs(0)
    } else {
      coeffs(0) = ((-b + Math.sqrt(d)) / (2 * a))
      coeffs(1) = ((-b - Math.sqrt(d)) / (2 * a))
    }
    return coeffs
  }

  def task2a_4(a: Double, b: Double, c: Double): Double => Double = {
    (x: Double) => a * x * x + b * x + c
  }

  // Task 3a
  def thread(body: =>Unit): Thread = {
    val t = new Thread {
      override def run() = body
    }
    t
  }

  def main(args: Array[String]): Unit = {
    val nums = task1a()
    println(nums.mkString(", "))
    val sum = task1b(nums)
    println(s"Sum: $sum")
    val recSum = task1c(nums)
    println(s"Recursive Sum: $recSum")
    val n = 10
    val fib = task1d(n)
    println(s"n th Fibonacci number: $fib")
    var coeffs = task2a_1(2, 1, -1)
    println(s"Roots: ${coeffs(0)}, ${coeffs(1)}")
    coeffs = task2a_1(2, 1, 2)
    println(s"Roots: ${coeffs(0)}, ${coeffs(1)}")
    val (a, b, c, x) = (3, 2, 1, 2)
    println(s"Quadratic function: f($x) = $a * x^2 + $b * x + $c = ${task2a_4(a, b, c)(x)}")
  }

}
