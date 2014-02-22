package solutions

import scala.annotation.tailrec

object PromptLoop1 extends App {
  var total = 0
  do {
    val line = Console.readLine(s"Total: $total; Input a number to add, Enter to stop> ").trim
    if (line.isEmpty) sys.exit()
    val number: Int = try {
      line.toInt
    } catch {
      case nfe: NumberFormatException =>
        println("Invalid number ignored. Please try again.")
        0

      case throwable: Throwable =>
        sys.error(throwable.getMessage)
    }
    total = total + number
  } while (true)
}

object PromptLoop2 extends App {
  @tailrec
  def getValue(increment: Int, subtotal: Int): Int = {
    val total = subtotal + increment
    val line = Console.readLine(s"Total: $total; Input a number to add, Enter to stop> ").trim
    if (line.isEmpty) 0 else {
      val userValue = try {
        line.toInt
      } catch {
        case nfe: NumberFormatException =>
          println("Invalid number ignored. Please try again.")
          0

        case throwable: Throwable =>
          sys.error(throwable.getMessage)
      }
      getValue(userValue, total)
    }
  }

  getValue(0, 0)
}
