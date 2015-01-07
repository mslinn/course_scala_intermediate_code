package multi.futures

import multi._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object ForComp0 extends App {
  for {
    value <- readUrlFuture("http://scalacourses.com", 200)
  } println(s"For loop: value=$value")

  val futureResult = for {
    value <- readUrlFuture("http://scalacourses.com", 200)
  } yield value
  println(s"For comprehension: result=$futureResult")

  synchronized { wait() }
}

object ForCompSerial1 extends App {
  val list: List[BigInt] = for {
    x <- List(factorial(123))
    y <- List(factorial(234)) if y / 10 > x
    z <- List(factorial(345)) if x % 2 == BigInt(0)
  } yield x + y + z
  println(s"List=$list")

  val option: Option[BigInt] = for {
    x <- Option(factorial(123))
    y <- Option(factorial(234)) if y / 10 > x
    z <- Option(factorial(345)) if x % 2 == BigInt(0)
  } yield x + y + z
  println(s"Option=$option")

  val futureResult: Future[BigInt] = for {
    x <- Future(factorial(123))
    y <- Future(factorial(234)) if y / 10 > x
    z <- Future(factorial(345)) if x % 2 == BigInt(0)
  } yield x + y + z
  futureResult andThen {
    case Success(value) ⇒
      println(s"Future value=$value")
      System.exit(0)

    case Failure(ex) ⇒
      println(s"Failure: ${ex.getMessage}")
      System.exit(0)
  }
  synchronized { wait() }
}

object ForCompSerial2 extends App {
  (for {
    x <- Future(factorial(123))
    y <- Future(factorial(234)) if y/10>x
    z <- Future(factorial(345)) if x%2==BigInt(0)
  } yield x + y + z) andThen {
    case Success(value) =>
      println(s"Success: $value")
      System.exit(0)

    case Failure(ex) =>
      println(s"Failure: ${ex.getMessage}")
      System.exit(1)
  }
  synchronized { wait() }
}

object ForCompSerial3 extends App {
  Future(factorial(123)).flatMap { x =>
    Future(factorial(234)).flatMap { y =>
      Future(factorial(345)).map { z => x + y + z }
    }
  } andThen {
    case Success(value) =>
      println(s"Success: $value")
      System.exit(0)

    case Failure(ex) =>
      println(s"Failure: ${ex.getMessage}")
      System.exit(1)
  }
  synchronized { wait() }
}

object ForCompParallel extends App {
  val futureX: Future[BigInt] = Future(factorial(123))
  val futureY: Future[BigInt] = Future(factorial(345))
  val futureZ: Future[BigInt] = Future(factorial(345))

  val futureResult: Future[BigInt] = for {
    x <- futureX
    y <- futureY if y/10>x
    z <- futureZ if x%2==BigInt(0)
  } yield x + y + z

  futureResult andThen {
    case Success(value) =>
      println(s"Success: $value")
      System.exit(0)

    case Failure(ex) =>
      println(s"Failure: ${ex.getMessage}")
      System.exit(1)
  }
  synchronized { wait() }
}
