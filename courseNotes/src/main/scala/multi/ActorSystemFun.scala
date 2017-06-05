package multi

import akka.actor._
import com.typesafe.config.ConfigFactory
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object ActorSystemFun1 extends App {
  implicit val system = ActorSystem("MySystem")
  implicit val executor = system.dispatcher
  futures.readUrlFuture(goodUrlStr1) andThen {
    case _ =>
      println("Future completed :)")
      system.terminate()
  }
}

object ActorSystemFun2 extends App {
  val configString = """
    akka {
      // dumps out configuration onto console when enabled and loglevel >= "INFO"
      logConfigOnStart=on
      stdout-loglevel = "WARNING" # startup log level
      loglevel = "INFO" # loglevel once ActorSystem is started
      actor {
        my-dispatcher {
          type = Dispatcher,
          max-pool-size-max = 64, # default
          throughput = 5, # default
          core-pool-size-max = 64 # default
        }
      }
    }"""
  implicit val system = ActorSystem("actorSystem", ConfigFactory.parseString(configString))
  implicit val executor = system.dispatcher
  futures.readUrlFuture(goodUrlStr1) andThen {
    case _ =>
      println("Future completed :)")
      system.terminate()
  }
}

object ActorSystemFun3 extends App {
  implicit val system = ActorSystem("default", ConfigFactory.defaultReference)
  implicit val executor = system.dispatcher
  futures.readUrlFuture(goodUrlStr1) andThen {
    case _ =>
      println("Future completed :)")
      system.terminate()
  }
}

object ActorSystemFun4 extends App {
  val configString = "akka { daemonic = on }"
  implicit val system = ActorSystem("default", ConfigFactory.parseString(configString))
  implicit val executor = system.dispatcher
  futures.readUrlFuture(goodUrlStr1) andThen {
    case _ =>
      println("Future completed :)")
      system.terminate()
  }
  synchronized { wait() }
}

object ActorSystemFun5 extends App {
  val configString = "akka { daemonic = on }"
  implicit val system = ActorSystem("default", ConfigFactory.parseResources("blah.conf").withFallback(ConfigFactory.parseString(configString)))
  implicit val executor = system.dispatcher
  futures.readUrlFuture(goodUrlStr1) andThen {
    case _ =>
      println("Future completed :)")
      system.terminate()
  }
  synchronized { wait() }
}

object ActorSystemFun6 extends App {
  implicit val system = ActorSystem()
  system.logConfiguration() // outputs hundreds of lines
  println(s"ActorSystem name=${ system.name }")
  println(s"Before shutdown: isTerminated=${ system.whenTerminated.isCompleted }")
  system.terminate() // allow System.exit()
  println(s"After shutdown: isTerminated=${ system.whenTerminated.isCompleted }")
  time("awaitTermination")(Await.result(system.whenTerminated, Duration.Inf))
  println("After awaitTermination(), isTerminated=" + system.whenTerminated.isCompleted)
}
