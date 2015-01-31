package multi

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.util.{Failure, Random, Success}

class MyActor extends Actor {
  def receive = {
    case msg =>
      println(s"MyActor got '$msg'")
      sender ! "Got the message" // reply to sender
  }
}

object ActorFun1 extends App {
    val system: ActorSystem = ActorSystem("myActorSystem")
    val myActor1: ActorRef = system.actorOf(Props[MyActor], name="myActor1")
    val aMessage = "This is a message"
    implicit val timeout = Timeout(60 seconds)
    val response: Future[Any] = myActor1 ? aMessage

    val result1: Any = Await.result(myActor1 ? aMessage, 5 seconds)
    println(s"result1=$result1 (${result1.getClass.getName})")

    val result2: String = Await.result((myActor1 ? aMessage).mapTo[String], 5 seconds)
    println(s"result2=$result2 (${result2.getClass.getName})")

    myActor1 ? aMessage map ( result3 => println(s"result3=$result3 (${result3.getClass.getName})") )

    myActor1 ? aMessage foreach ( result4 => println(s"result4=$result4 (${result4.getClass.getName})") )
    myActor1 ? aMessage onComplete ( result5 => println(s"result5=$result5 (${result5.getClass.getName})") )
}

object ActorFun2 extends App {
  implicit val system = ActorSystem("myActorSystem")
  implicit val timeout = Timeout(60 seconds)

  class SupervisorActor extends Actor {
    def start: Future[String] = {
      val childActor: ActorRef = context.actorOf(Props[MyActor])
      (childActor ? SupervisorActor.doIt andThen {
        case Success(response)  => s"SupervisorActor got response from childActor '$response'"
        case Failure(exception) => s"SupervisorActor - ${exception.getClass.getName}: ${exception.getMessage}"
      }).mapTo[String]
    }

    def receive = {
      case msg if msg == SupervisorActor.goMsg =>
        println(s"SupervisorActor received '$msg'")
        sender ! time("message exchange")(Await.result(start, timeout.duration))
    }
  }

  object SupervisorActor {
    val goMsg = "go"
    val doIt = "Do my bidding"
  }

  val supervisor: ActorRef = system.actorOf(Props[SupervisorActor])
  supervisor ? SupervisorActor.goMsg andThen {
    case Success(response)  => println(response)
    case Failure(exception) => println(s"${exception.getClass.getName}: ${exception.getMessage}")
  } andThen {
    case _ => system.shutdown()
  }
}

object ActorExercise {
  case class ChunkerMsg(numWorkers: Int, tries: Int, text: String)

  case class PersistenceMsg(attemptCount: Int, text: String)

  case class ResultMsg(text: String)

  case class WorkerMsg(numChars: Int, tries: Int)

  /** Supervisor, creates actor hierarchy to generate candidate data and an actor to identify the best match */
  class Chunker extends Actor {
    var rootActor: ActorRef = _

    def receive = {
      case msg: ChunkerMsg =>
        //println(s"Chunker actor got $msg")
        rootActor = sender()
        context.actorOf(Props[Persistence], name = "persistence") ! msg
        1 to msg.numWorkers foreach { i =>
          context.actorOf(Props[Worker], name = s"worker$i") ! WorkerMsg(msg.text.length, msg.tries)
        }

      case msg: ResultMsg =>
        //println(s"Chunker actor got $msg")
        sender ! PoisonPill
        rootActor ! msg

      case msg =>
        println(s"Invalid message $msg of type ${msg.getClass.getName} received by Chunker actor")
    }
  }

  /** Performs long computation */
  class Worker extends Actor {
    def receive = {
      case msg: WorkerMsg =>
        //println(s"Worker actor got $msg")
        val random = new Random()
        msg.tries to 0 by -1 foreach { i =>
          val randomString = (1 to msg.numChars).map( c => random.nextPrintableChar() ).mkString
          val persistence = context.actorSelection("../persistence")
          persistence ! PersistenceMsg(i, randomString)
        }

      case msg =>
        println(s"Invalid message $msg of type ${msg.getClass.getName} received by Worker actor")
    }
  }

  /** Compares Worker results against desired result and identifies the best match */
  class Persistence extends Actor {
    var targetString = ""
    var bestMatchString = ""
    var workers = 0
    //println(s"Persistence actor path is ${context.self.path}")

    def receive = {
      case msg: ChunkerMsg =>
        //println(s"Persistence actor got $msg")
        targetString = msg.text
        workers = msg.numWorkers

      case msg: PersistenceMsg =>
        //println(s"Persistence actor got $msg")
        val matching = matchSubstring(msg.text, targetString)
        if (matching.length>bestMatchString.length) {
          bestMatchString = matching
          //println(s"Matched $matching")
        }
        if (msg.attemptCount==0) {
          sender ! PoisonPill
          workers = workers - 1
          if (workers==0)
            context.parent ! ResultMsg(bestMatchString)
        }

      case msg =>
        println(s"Invalid message $msg of type ${msg.getClass.getName} received by Persistence actor")
    }
  }
}

object ActorFun3 extends App {
  import ActorExercise._

//  val pool: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors)
//  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutor(pool)

  implicit val timeout = Timeout(60 seconds)

  val system = ActorSystem("monkeyCage")
  val chunker = system.actorOf(Props[Chunker], name = "chunker")
  val future = chunker ? ChunkerMsg(50, 30000, "Once upon a time, there was a little girl who dreamed she could fly.")
  val waiter = concurrent.Promise[String]()
  future.mapTo[ResultMsg] onComplete {
    case Success(value) =>
      waiter.success(s"Best match is '${value.text}' (${value.text.length} characters})")

    case Failure(msg) =>
      waiter.failure(new Exception(msg))
  }
  val result = concurrent.Await.result(waiter.future, concurrent.duration.Duration.Inf)
  println(result)
  system.shutdown()
}
