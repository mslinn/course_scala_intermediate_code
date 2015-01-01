package object multi {
  def factorial(number: BigInt): BigInt = {
    @annotation.tailrec
    def fact(total: BigInt, number: BigInt): BigInt = {
      if (number == BigInt(1))
        total
      else
        fact(total* number, number - 1)
    }
    fact(1, number)
  }
}
