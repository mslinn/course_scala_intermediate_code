package multi.futures

import java.util.concurrent.{CompletableFuture, ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.jdk.javaapi.FutureConverters
import scala.util.{Failure, Success}

object FutureConverterFun extends App {
  val executorService: ExecutorService = Executors.newFixedThreadPool(10)
  implicit val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(executorService)

  val future = new CompletableFuture[String]
  val scalaFuture: Future[String] = FutureConverters.asScala(future)

  scalaFuture.andThen {
    case Success(result) => println(s"Success: $result")
    case Failure(exception) => println(s"Failure: ${ exception.getMessage }")
  }.andThen {
    case _ => System.exit(0)
  }
  future.complete("All done!")
  synchronized { wait() }
}
