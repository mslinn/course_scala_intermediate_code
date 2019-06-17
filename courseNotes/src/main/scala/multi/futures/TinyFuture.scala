package multi.futures

import scala.util.{Failure, Success, Try}

object TinyFuture {
  class WriteOnce[T] {
    private var value: Option[Try[T]] = None

    def isCompleted: Boolean = value.isDefined

    def isFailure: Boolean = value.exists(_.isFailure)

    def isSuccess: Boolean = value.exists(_.isSuccess)

    /** Create an instance with a failure value (a Throwable instance) */
    def fail(throwable: Throwable): WriteOnce[T] = {
      if (value.isEmpty) value = Some(Failure(throwable)) else
        throw new Exception(WriteOnce.alreadySetMSg)
      this
    }

    /** Create an instance with a success value */
    def set(newValue: T): WriteOnce[T] = {
      if (value.isEmpty) value = Some(Success(newValue)) else
        throw new Exception(WriteOnce.alreadySetMSg)
      this
    }

    /** Get success value */
    def success: T = value.map {
      case v if v.isSuccess => v.get
      case v => throw new Exception("WriteOnce is a failure, there is no value stored.")
    }.getOrElse(throw new Exception(WriteOnce.stillPendingMsg))

    /** Get failure value (a Throwable instance) */
    def failed: Throwable = value.map {
      case v if v.isFailure => v.failed.get
      case v => throw new Exception("WriteOnce is a success, there is no Throwable stored.")
    }.getOrElse(throw new Exception(WriteOnce.stillPendingMsg))
  }

  object WriteOnce {
    val stillPendingMsg = "WriteOnce is still pending, there is no value yet."
    val alreadySetMSg   = "Value of WriteOnce is already set"
  }

  class Future[T] {
    val value = new WriteOnce[T]

    def isCompleted: Boolean = value.isCompleted
  }

  object Future {
    def apply[T](value: => T): Future[T] = {
      val future = new Future[T]()
      try {
        future.value.set(value)
      } catch {
        case e: Exception => future.value.fail(e)
      }
      future
    }

    def failed[T](throwable: Throwable): Future[T] = {
      val future = new Future[T]
      future.value.fail(throwable)
      future
    }

    def successful[T](value: T): Future[T] = Future(value)
  }


  class Promise[T] {
    val future = new Future[T]

    def complete(result: Try[T]): Promise[T] = {
      if (result.isSuccess)
        future.value.set(result.get)
      else
        future.value.fail(result.failed.get)
      this
    }

    def failure(cause: Throwable): Promise[T] = {
      future.value.fail(cause)
      this
    }

    def success(value: T): Promise[T] = {
      future.value.set(value)
      this
    }

    final def tryCompleteWith(other: Future[T]): Promise[T] = {
      if (!future.isCompleted) {
        if (future.value.isSuccess)
          future.value.set(other.value.success)
        else
          future.value.fail(other.value.failed)
      }
      this
    }

    def tryFailure(cause: Throwable): Boolean = {
      if (future.isCompleted) {
        false
      } else {
        future.value.fail(cause)
        true
      }
    }

    def trySuccess(value: T): Boolean = {
      if (future.isCompleted) {
        false
      } else {
        future.value.set(value)
        true
      }
    }
  }

  object Promise {
    def apply[T](): Promise[T] = new Promise[T]

    def failed[T](throwable: Throwable): Promise[T] = {
      val promise = new Promise[T]
      promise.future.value.fail(throwable)
      promise
    }

    def successful[T](value: T): Promise[T] = {
      val promise = new Promise[T]
      promise.future.value.set(value)
      promise
    }
  }
}

object WriteOnceDemo extends App {
  import multi.futures.TinyFuture.WriteOnce

  val writeOnce1 = new WriteOnce[Int]()
  writeOnce1.set(42)
  println(s"writeOnce1.isCompleted=${writeOnce1.isCompleted}")
  println(s"writeOnce1.isFailure=${writeOnce1.isFailure}")
  println(s"writeOnce1.isSuccess=${writeOnce1.isSuccess}")
  println(s"writeOnce1.success=${writeOnce1.success}")
  try { writeOnce1.failed } catch { case exception: Throwable => println(s"writeOnce1.failed: ${exception.getMessage}") }
  println()

  val writeOnce2 = new WriteOnce().set(420)
  println(s"writeOnce2.isCompleted=${writeOnce2.isCompleted}")
  println(s"writeOnce2.isFailure=${writeOnce2.isFailure}")
  println(s"writeOnce2.isSuccess=${writeOnce2.isSuccess}")
  println(s"writeOnce2.success=${writeOnce2.success}")
  try { writeOnce2.failed } catch { case exception: Throwable => println(s"writeOnce2.failed: ${exception.getMessage}") }
  println()

  val writeOnce3 = new WriteOnce().fail(new Exception("You reached the end of the Internet"))
  println(s"writeOnce3.isCompleted=${writeOnce3.isCompleted}")
  println(s"writeOnce3.isFailure=${writeOnce3.isFailure}")
  println(s"writeOnce3.isSuccess=${writeOnce3.isSuccess}")
  try { writeOnce3.success } catch { case exception: Throwable => println(s"writeOnce3.success: ${exception.getMessage}") }
  println(s"writeOnce3.failed=${writeOnce3.failed}")
}

object TinyFutureDemo extends App {
  import multi.futures.TinyFuture._

  val promise1 = Promise.successful(1)
  println(s"Value of promise1 = ${promise1.future.value.success}")

  val promise2 = Promise.failed(new Exception("The knurblefritz has been badly gurbled"))
  println(s"Throwable of promise2 = ${promise2.future.value.failed}")

  val future1 = Future.successful(3+5)
  println(s"value of future1 = ${future1.value.success}")

  val future2 = Future.failed(new Exception("The omdedomdom has been hoomified"))
  println(s"Throwable of future2 = ${future2.value.failed.getMessage}")
}
