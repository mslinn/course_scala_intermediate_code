object PartialFunReview extends App {
  val f1 = (a: Int) => a.toString
  println(s"f1(3 * 10)=${ f1(3 * 10) }")

  val f2 = (_: Int).toString
  println(s"f2(3 * 10)=${ f2(3 * 10) }")
}

object PartialFun1 extends App {
  println(s"System.getProperties.keySet=${ System.getProperties.keySet }")
  println(s"System.getenv.keySet=${ System.getenv.keySet }")

  val home = Option(System.getenv("JAVA_HOME"))

  val env: PartialFunction[String, String] = new PartialFunction[String, String] {
    private def value(name: String) = Option(System.getenv(name))

    def apply(name: String): String = value(name).get

    def isDefinedAt(name: String): Boolean = value(name).isDefined
  }

  println(s"""env.isDefinedAt("JAVA_HOME")=${ env.isDefinedAt("JAVA_HOME") }""")
  println(s"""env("JAVA_HOME")=${ env("JAVA_HOME") }""")
  println(s"""env.isDefinedAt("x")=${ env.isDefinedAt("x") }""")
  try {
    println(s"""env("x")=${ env("x") }""")
  } catch {
    case e: Exception => println(s"""env("x") threw ${ e.getMessage }""")
  }
}

trait Env {
  val env: PartialFunction[String, String] = {
    case name: String if Option(System.getenv(name)).isDefined  => System.getenv(name)
  }

  /*
  <console>:8: error: missing parameter type for expanded function
     The argument types of an anonymous function must be fully known. (SLS 8.5)
     Expected type was: ?
   val env = { case name: String => getenv } */
//  val env2 = {
//    case name: String if Option(System.getenv(name) ).isDefined => System.getenv(name)
//  }
}

object PartialFunShorthand extends App with Env

object PartialFunMultiIn extends App {
  val checkStringLength: PartialFunction[(String, Int), Boolean] = {
   case (string: String, length: Int) if string.toLowerCase==string => string.length==length
  }

  println(s"""checkStringLength.isDefinedAt(("asdf", 4))=${ checkStringLength.isDefinedAt(("asdf", 4)) }""")
  println(s"""checkStringLength(("asdf", 4))=${ checkStringLength(("asdf", 4)) }""")
  println(s"""checkStringLength("asdf", 4)=${ checkStringLength("asdf", 4) }""")
  println(s"""checkStringLength("asdf", 43)=${ checkStringLength("asdf", 43) }""")
  println(s"""checkStringLength.isDefinedAt(("ASDF", 4))=${ checkStringLength.isDefinedAt(("ASDF", 4)) }""")
  println(s"""checkStringLength("ASDF", 4)=${ checkStringLength("ASDF", 4) }""")
}

object PartialFunCompose extends App {
  type PfAnyToUnit = PartialFunction[Any, Unit]
  val int: PfAnyToUnit    = { case _: Int    => println("Int found") }
  val double: PfAnyToUnit = { case _: Double => println("Double found") }
  val any: PfAnyToUnit    = { case x         => println(s"Something else found ($x)") }
  val chainedPF: PartialFunction[Any, Unit] = int orElse double orElse any

  println(s"""chainedPF(1)=${ chainedPF(1) }""")

  val chainedPF2: PfAnyToUnit = {
    case _: Int    => println("Int found")
    case _: Double => println("Double found")
    case x         => println(s"Something else found ($x)")
  }
  println(s"""chainedPF2(1)=${ chainedPF2(1) }""")
  println(s"""(int orElse double orElse any)(1.0)=${ (int orElse double orElse any)(1.0) }""")
  println(s"""(int orElse double orElse any)(true)=${ (int orElse double orElse any)(true) }""")
}

object PartialFunCollect extends App {
  import PartialFunShorthand.env

  val javaHomes = List("JAVA_HOME_8", "JAVA_HOME_7", "JAVA_HOME_6", "JAVA_HOME_5", "JAVA_HOME")
  println(s"""javaHomes.collect(env)=${ javaHomes.collect(env) }""")
  println(s"""javaHomes collect env=${ javaHomes collect env }""")
}

object PartialFunCompColl extends App {
  val sample = 1 to 10

  val isEven: PartialFunction[Int, String] = { case x if x % 2 == 0 => s"$x is even" }
  val evenNumbers = sample collect isEven
  println(s"""evenNumbers=$evenNumbers""")

  val isOdd: PartialFunction[Int, String] = { case x if x % 2 == 1 => s"$x is odd" }
  val oddNumbers = sample collect isOdd
  println(s"""oddNumbers=$oddNumbers""")

  val numbers = sample collect (isEven orElse isOdd)
  println(s"""numbers=$numbers""")

  val numbers2 = sample map (isEven orElse isOdd)
  println(s"""numbers2=$numbers2""")
}

object PartialFunCaseSeq extends App {
  val list = List(Some(1), None, Some(3))
//  val result1 = list collect { item => // IntelliJ IDEA flags this as a syntax error but it is legal and will run
//    item match {
//      case Some(x) => x
//    }
//  }
//  println(s"result1=$result1")

  // This is what IntelliJ wants:
  val result2 = list collect { case Some(x) => x }
  println(s"result2=$result2")

  def doSomething[T](data: T)
                    (operation: T => T): Any = try {
     operation(data)
  } catch {
    case ioe: java.io.IOException => println(ioe.getMessage)
    case   e: Exception => println(e)
  }
}

object PartialFunWith extends App {
  def withT[T](t: T)
              (operation: T => Unit): Unit = { operation(t) }

  case class Blarg(i: Int, s: String)

  def handle(implicit blarg: Blarg): Unit = withT(blarg) {
    case Blarg(0, _) => println("i is 0")

    case Blarg(_, "triple") => println("s is triple")

    case whatever => println(whatever)
  }

  println(s"""handle(Blarg(1, "blarg"))=${ handle(Blarg(1, "blarg")) }""")
  println(s"""handle(Blarg(1, "triple"))=${ handle(Blarg(1, "triple")) }""")
  println(s"""handle(Blarg(0, "triple"))=${ handle(Blarg(0, "triple")) }""")

  def showBlarg(msg: String)
               (implicit blarg: Blarg): Unit =
    println(s"$msg\nblarg.i=${ blarg.i }; blarg.s=${ blarg.s }")

  withT(Blarg(1, "blarg ")) { implicit blarg =>
    blarg match {
      case Blarg(0, s) => showBlarg("Matched blarg on i==0")
      case Blarg(i, "triple") => showBlarg("""Matched blarg on s=triple""")
      case _ => showBlarg("Catchall case")
    }
  }
}

object PartialFun2 extends App {
  case class Attendee(name: String, id: Long, knowledge: Option[String])

  val attendees = List(
    Attendee("Fred", 1, None),
    Attendee("Lisa", 2, Some("Akka")),
    Attendee("You",  3, Some("Scala"))
  )

  println("Using partial function")
  for {
    you <- attendees.collect { case attendee if attendee.name=="You" => attendee }
    yourKnowledge <- you.knowledge orElse Some("Nothing")
  } yield {
    println(s"You know $yourKnowledge")
  }

  println("\nUsing guard")
  for {
    attendee <- attendees if attendee.name=="You"
    yourKnowledge <- attendee.knowledge orElse Some("Nothing")
  } yield {
    println(s"You know $yourKnowledge")
  }
}

object PartialFun3 extends App {
  import scala.util.{Failure, Success, Try}

  def handleTry(theTry: Try[String]): Unit = theTry match {
    case Success(word) if word.toLowerCase.contains("secret") =>
      println(s"You typed the secret word!")

    case Success(value) if value.trim.nonEmpty =>
      println(s"Found an unexpected word: '$value'")

    case Success(_) =>
      println(s"Got an empty string")

    case Failure(err) =>
      println(s"Error: ${ err.getMessage }")
  }

  handleTry(Success("blah"))
  handleTry(Failure(new Exception("blah")))
  handleTry(Try("I know your secret"))
  handleTry(Try("  "))
}
