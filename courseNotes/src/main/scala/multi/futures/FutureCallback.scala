package multi.futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.language.postfixOps
import scala.util.control.NoStackTrace
import scala.util.{Failure, Success}

object FutureCallback extends App {
  val promises: Vector[Promise[String]] =
    Promise.successful("Hi there") +: (1 to 4).map { _ => Promise[String]() }.toVector

  def promiseStatus(i: Int): Unit =
    if (promises(i).isCompleted) {
      promises(i).future.value.get match {
        case Success(value) => println(s"promises($i).future.value='$value'.")
        case Failure(exception) => println(s"promises($i).future exception message: '${exception.getMessage}'.")
      }
    } else println(s"promises($i) is pending.")

  def promiseStatuses(msg: String): Unit = {
    println(s"\n== $msg ==")
    0 until promises.length foreach { promiseStatus }
    println()
  }

  promiseStatuses("Five Promises Started")
  promises(0).future onSuccess {
    case value => println(s"promises(0) completed successfully with value='$value'.")
  }

  promises(1).future onComplete {
    case Success(value) => println(s"promises(1) onComplete success: value='$value'.")
    case Failure(throwable) => println(s"promises(1) onComplete exception; message: '${throwable.getMessage}'.")
  }
  promises(1).future onSuccess {
    case value => println(s"promises(1) onSuccess; value='$value'.")
  }
  promises(1).future onFailure {
    case throwable => println(s"promises(1) onFailure; exception message: '${throwable.getMessage}'.")
  }
  promises(1).future andThen {
    case Success(value) => println(s"promises(1) andThen success: value='$value'.")
    case Failure(throwable) => println(s"promises(1) andThen exception; message: '${throwable.getMessage}'.")
  }
  promises(1).success("The tao that is knowable is not the true tao")
  //promises(1).complete(Success("The tao that is knowable is not the true tao")) // does same thing
  promiseStatuses("After Promises(1) Completed")

  promises(2).future andThen {
    case Success(value) => println(s"promises(2) andThen success: promise3 value='$value'.")
    case Failure(throwable) => println(s"promises(2) andThen exception message: '${throwable.getMessage}'.")
  } andThen {
    case _ => println("promises(2).andThen.andThen - Will that be all, master?")
  }
  promises(2).complete(Success("Great achievement looks incomplete, yet it works perfectly"))
  promiseStatuses("After Promises(2) Completed")

  promises(3).future onFailure {
    case exception => println(s"promises(3) completed with exception message: '${exception.getMessage}'.")
  }
  class ExceptTrace(msg: String) extends Exception(msg) with NoStackTrace
  promises(3).failure(new ExceptTrace("Kaboom"))
  promiseStatuses("After Promises(3) Completed")

  object TheExceptTrace extends Exception("Kablam") with NoStackTrace
  promises(4).complete(Failure(TheExceptTrace))
  promises(4).future onComplete { case _ => promiseStatuses("Five Promises Concluded") }

  val f1 = Future(multi.factorial(12345))
  f1 onComplete { // might not generate output before System.exit (run program several times to see)
    case Success(value) => println(s"factorial success #1: factorial(12345)=$value")
    case Failure(exception) => println(s"factorial onComplete completed with exception message: '${exception.getMessage}'.")
  }

  val f2 = Future(multi.factorial(12345)) andThen { // always works
    case Success(value) => println(s"factorial success #2: factorial(12345)=$value")
    case Failure(exception) => println(s"factorial andThen completed with exception message: '${exception.getMessage}'.")
  }

  Future.sequence(List(f1, f2) ::: promises.map(_.future).toList) andThen { case _ => System.exit(0) }

  synchronized { wait() }
}
