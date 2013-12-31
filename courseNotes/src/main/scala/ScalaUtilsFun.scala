class Super
class Sub extends Super

object TripleEqualsFun extends App {
  import org.scalautils.TripleEquals._

  println(s"Some(1)===1 => ${Some(1)===1}")
  println(s"1===1L => ${1 === 1L}")
  println(s"List(1, 2, 3)===Vector(1, 2, 3) => ${List(1, 2, 3) === Vector(1, 2, 3)}")

  println(s"new Sub===new Super => ${new Sub === new Super}")
}


object OrUnapply extends App {
  import org.scalautils._

  case class Person(firstName: String, lastName: String, age: Int)

  object Person {
    private def parseAge(input: String): Int Or ErrorMessage = {
      try {
        val age = input.toInt
        if (age >= 0 && age <=150) Good(age) else Bad(s""""${age}" is not a valid age""")
      } catch {
        case _: NumberFormatException => Bad(s""""${input}" is not a valid integer""")
      }
    }

    def parsePerson(input: String): Person Or ErrorMessage = {
      input.trim.split(" ").toList match {
        case first :: last :: inputAge :: Nil =>
          for {
            age <- parseAge(inputAge)
          } yield Person(first, last, age)

        case otherwise =>
          Bad(s"'$input' could not be parsed")
      }
    }

    implicit def unapply(input: String): Option[Person] = parsePerson(input).toOption
  }

  val maybePerson: Option[Person] = "Fred Flintstone 36"
}
