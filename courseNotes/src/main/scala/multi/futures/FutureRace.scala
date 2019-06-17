package multi.futures

object FutureRace extends App {
  import java.util.concurrent.{ExecutorService, Executors}
  import scala.concurrent.duration.Duration
  import scala.concurrent.{Await, ExecutionContext, Future}
  import scala.util.{Failure, Success}

  val threadCount: Int = try {
    if (args.length==1) math.max(1, args(0).toInt) else 1
  } catch {
    case e: Exception =>
      println("You can specify the number of threads in the threadpool on the command line; default value is 1")
      System.exit(-1)
      1
  }

  val pool: ExecutorService = Executors.newFixedThreadPool(threadCount)
  implicit val ec = ExecutionContext.fromExecutor(pool)

  var offset = 6 // @volatile does not affect race conditions
  def accessor: Int = offset

  val f1 = Future {
    2 + 3 + offset // will be executed asynchronously
  } andThen {
    case Success(result)   => println("Result 1: " + result)
    case Failure(exception) => println("Exception 1: " + exception.getMessage)
  }

  val f2 = Future {
    2 + 3 + accessor // will be executed asynchronously
  } andThen {
    case Success(result)   => println("Result 2: " + result)
    case Failure(exception) => println("Exception 2: " + exception.getMessage)
  }
  offset = 42
  println("End of mainline, offset = " + offset)

  Await.ready(Future.sequence(List(f1, f2)), Duration.Inf)
  pool.shutdown()
}
