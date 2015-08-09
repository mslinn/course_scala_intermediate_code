package solutions

object AndThenTry extends App {
  import scala.util.{Failure, Success, Try}

  implicit class RichTry[A](theTry: Try[A]) {
    def andThen(pf: PartialFunction[Try[A], Unit]): Try[A] = {
      if (pf.isDefinedAt(theTry)) pf(theTry)
      theTry
    }
  }

  val x: Try[Int] = Try {
    2 / 0
  } andThen {
    case Failure(ex) => println(s"Logging ${ex.getMessage}")
  } andThen {
    case Success(value) => println(s"Success: got $value")
    case Failure(ex) => println(s"This just shows that any failure is provided to each chained andThen clause ${ex.getMessage}")
  }
  println(s"x=$x")
}
