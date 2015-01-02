package multi.futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.io.Source
import scala.util.control.NoStackTrace
import scala.util.{Failure, Success}

object FutureCallback extends App {
  val promise1: Promise[String] = Promise.successful("Hi there")
  val promise2, promise3, promise4, promise5 = Promise[String]()

  def promiseStatus(promise: Promise[_], id: Int): Unit =
    if (promise.isCompleted) {
      promise.future.value.get match {
        case Success(value) => println(s"promise$id.future.value='$value'.")
        case Failure(exception) => println(s"promise$id.future exception message: '${exception.getMessage}'.")
      }
    } else println(s"promise$id is pending.")

  def promiseStatuses(msg: String): Unit = {
    println(s"\n== $msg ==")
    List(promise1, promise2, promise3, promise4, promise5).zipWithIndex.foreach { case (promise, i) =>
      promiseStatus(promise, i + 1)
    }
    println()
  }

  promiseStatuses("Five Promises Started")

  promise1.future onSuccess {
    case value => println(s"promise1 completed successfully with value='$value'.")
  }

  promise2.future onSuccess {
    case value => println(s"promise2 completed successfully with value='$value'.")
  }
  promise2.success("The tao that is knowable is not the true tao") // happens right away
  //promise2.complete(Success("The tao that is knowable is not the true tao")) // does same thing

  promise3.future onComplete {
    case Success(value) => println(s"promise3 onComplete success: promise3 value='$value'.")
    case Failure(throwable) => println(s"promise3 onComplete exception message: '${throwable.getMessage}'.")
  }
  promise3.future andThen {
    case Success(value) => println(s"promise3 andThen success: promise3 value='$value'.")
    case Failure(throwable) => println(s"promise3 andThen exception message: '${throwable.getMessage}'.")
  } andThen {
    case _ => println("promise3.andThen.andThen - Will that be all, master?")
  }
  promise3.complete(Success("Great achievement looks incomplete, yet it works perfectly"))

  promise4.future.onFailure {
    case exception => println(s"promise4 completed with exception message: '${exception.getMessage}'.")
  }
  promise4.failure(new ExceptTrace("Kaboom"))

  promise5.complete(Failure(TheExceptTrace))
  promise5.future.onComplete {
    case _ =>
      promiseStatuses("Five Promises Concluded")
      System.exit(0)
  }

  synchronized { wait() }

  class ExceptTrace(msg: String) extends Exception(msg) with NoStackTrace

  object TheExceptTrace extends Exception("Kablam") with NoStackTrace
}
