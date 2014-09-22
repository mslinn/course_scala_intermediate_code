import akka.actor.{PoisonPill, Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import concurrent.duration._
import concurrent.ExecutionContext
import concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.{ExecutorService, Executors}
import util.{Success, Failure, Random}

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
      val matching = ActorFun.matchSubstring(msg.text, targetString)
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

object ActorFun extends App {
  def matchSubstring(str1: String, str2: String): String =
    str1.view.zip(str2).takeWhile(Function.tupled(_ == _)).map(_._1).mkString

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
