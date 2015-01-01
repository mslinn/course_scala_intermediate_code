package multi

import akka.actor._
import com.typesafe.config.ConfigFactory

object ActorSystemFun1 extends App {
  implicit val system = ActorSystem("MySystem")
  system.shutdown()
}

object ActorSystemFun2 extends App {
  val configString = """
    akka {
      // dumps out configuration onto console when enabled and
      // loglevel >= "INFO"
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
  system.shutdown()
}

object ActorSystemFun3 extends App {
  implicit val system = ActorSystem("default", ConfigFactory.defaultReference)
  system.shutdown()
}

object ActorSystemFun4 extends App {
  val configString = "akka { daemonic = on }"
  implicit val system = ActorSystem("default", ConfigFactory.parseString(configString))
  system.shutdown()
}

object ActorSystemFun5 extends App {
  val configString = "akka { daemonic = on }"
  implicit val system = ActorSystem("default", ConfigFactory.parseResources("blah.conf").withFallback(ConfigFactory.parseString(configString)))
  system.shutdown()
}

object ActorSystemFun6 extends App {
  implicit val system = ActorSystem()
  system.logConfiguration() // outputs hundreds of lines
  println(s"ActorSystem name=${system.name}")
  println(s"Before shutdown: isTerminated=${system.isTerminated}")
  system.shutdown() // allow System.exit()
  println(s"After shutdown: isTerminated=${system.isTerminated}")
  Thread.sleep(500)
  println("After isTerminated=" + system.isTerminated)
}
