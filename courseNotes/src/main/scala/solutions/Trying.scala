package solutions

object Trying extends App {
  import scala.util.{Failure, Success, Try}

  /** @return collection of all failures from the given collection of Try */
  def failures[A](tries: Seq[Try[A]]): Seq[Throwable] = tries.collect { case Failure(t) => t }

  /** @return collection of all successful values from the given collection of Try */
  def successes[A](tries: Seq[Try[A]]): Seq[A] = tries.collect { case Success(t) => t }

  /** @return a Try of a collection from a collection of Try. If many Exceptions are encountered, only one Exception needs to be captured. */
  def sequence[A](tries: Seq[Try[A]]): Try[Seq[A]] = Try(tries.map(_.get))

  /** @return a Tuple2 containing a collection of all failures and all the successes */
  def sequenceWithFailures[A](tries: Seq[Try[A]]): (Seq[Throwable], Seq[A]) =
    (failures(tries), successes(tries))


  val listOfTry = List(Try(6/0), Try("Happiness " * 3), Failure(new Exception("Drat!")), Try(99))

  println(s"failures=${failures(listOfTry).map(_.getMessage).mkString("; ")}")
  println(s"successes=${successes(listOfTry)}")
  println(s"sequence=${sequence(listOfTry)}")
  val (successes, failures) = sequenceWithFailures(listOfTry)
  println(s"""sequenceWithFailures:
    successes=$successes
    failures=$failures""")
}
