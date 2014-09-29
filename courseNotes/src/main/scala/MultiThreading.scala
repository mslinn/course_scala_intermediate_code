object MultiThreading {
  import scala.concurrent.ExecutionContext

  /** Returns an instance of the requested type of `ExecutionContext`.
    * @args can be `cached`, `fixed`, `forkjoin`, `scheduled`, `single`, `singleScheduled`, `akka*` or the empty String.
    *      "" is the default, and uses the default global execution context.
    *      For `akka`, all arguments are used to configure the dispatcher (requires no embedded spaces between arguments), for example:
    *      <pre>akka.daemonic=on
    * akka.actor.default-dispatcher.fork-join-executor.parallelism-min=20
    * akka.actor.default-dispatcher.fork-join-executor.parallelism-max=200</pre> */
  def executionContext(args: Array[String] = Array.empty[String]): ExecutionContext = {
    import java.util.concurrent.{Executors, ExecutorService}

    args.toList match {
      case a if a.contains("cached") =>
        val pool: ExecutorService = Executors.newCachedThreadPool()
        ExecutionContext.fromExecutor(pool)

      case a if a.contains("fixed") =>
        val pool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors)
        ExecutionContext.fromExecutor(pool)

      case a if a.contains("forkjoin") =>
        val pool: ExecutorService = new concurrent.forkjoin.ForkJoinPool()
        ExecutionContext.fromExecutor(pool)

      case a if a.contains("scheduled") =>
        val pool: ExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime.availableProcessors)
        ExecutionContext.fromExecutor(pool)

      case a if a.contains("single") =>
        val pool: ExecutorService = Executors.newSingleThreadExecutor()
        ExecutionContext.fromExecutor(pool)

      case a if a.contains("singleScheduled") =>
        val pool: ExecutorService = Executors.newSingleThreadScheduledExecutor()
        ExecutionContext.fromExecutor(pool)

      case a :: rest if a.contains("akka") =>
        val configString = args.mkString("\n")
        val config = com.typesafe.config.ConfigFactory.parseString(configString)
        val result = akka.actor.ActorSystem("actorSystem", config).dispatcher
        result

      case a if a.isEmpty =>
        concurrent.ExecutionContext.Implicits.global
    }
  }
}
