package solutions

import akka.actor.{Actor, ActorRef, ActorLogging, ActorSystem, Props}
import akka.event.Logging
import akka.pattern._
import akka.util.Timeout
import concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Random, Success}
import com.typesafe.config.ConfigFactory

object MonkeyActors extends App {
  /** Alphabet defining allowable characters */
  val allowableChars = """ !.,;'""" + (('a' to 'z').toList ::: ('A' to 'Z').toList ::: (0 to 9).toList).mkString

  val target = "I thought I saw a lolcat! I did, I did see a lolcat!"

  /** Number of times each monkey actor generates and matches a string */
  val numSims = 100000

  /** Number of Monkeys simulating random typing */
  val numMonkeyActors = 100

  /** show progress */
  val showProgress = if (args.length>0) args(0).toBoolean else false

  val configString =   """akka {
                         |  # change to "DEBUG" to see more output
                         |  loglevel = "WARNING"
                         |  actor {
                         |    debug {
                         |      # enable function of LoggingReceive, which is to log any received message at DEBUG level
                         |      receive = on
                         |
                         |      # enable DEBUG logging of actor lifecycle changes
                         |      lifecycle = on
                         |    }
                         |  }
                         |}""".stripMargin
  val config = ConfigFactory.parseString(configString).withFallback(ConfigFactory.load)
  implicit val system = ActorSystem("MonkeyActors", config)
  implicit val ec = system.dispatcher
  implicit val timeout = Timeout(1 day)

  println(f"Monkey typing simulation with $numMonkeyActors%,d monkey actors each running $numSims%,d simulations.")
  println("A dot is shown for each 1% of the total simulation.")
  println("As increasingly longer matched strings are generated, the extra matching characters are displayed.")
  println(s"String to match: '$target'")
  print("Running: ")
  val bookKeeper: ActorRef = system.actorOf(Props(classOf[BookKeeper], numSims, showProgress), name="bookKeeper")
  (bookKeeper ? BookkeeperStart(target, allowableChars, numMonkeyActors)) andThen {
    case Success(BestResult(bestResult)) =>
      val percentComplete = 100.0 - 100.0 * (target.length.toDouble-bestResult.length.toDouble) / target.length
      println(f"\n\nLongest matched substring: '$bestResult'; ${bestResult.length} of ${target.length} characters, $percentComplete%.1f%% of goal.")
    case Failure(e) =>
      println(s"${e.getClass.getName}: ${e.getMessage}")
  } andThen { case _ => system.shutdown() }
  system.awaitTermination()
}


case class BookkeeperStart(target: String, allowableChars: String, numMonkeyActors: Int)

/** Tell a Monkey to start typing, using the given random number seed */
case class DoSimulation(seed: Int)

/** A Monkey reports it is ready by sending this message */
case class MonkeyReady(monkey: ActorRef)

/** A Monkey reports its results from running a simulation by sending this message */
case class MonkeyResult(result: String, monkey: ActorRef)

/** A Monkey reports its results from running a simulation by sending this message */
case class BestResult(result: String)


class BookKeeper(numSims: Int, showProgress: Boolean=false) extends Actor with ActorLogging {
  var nSimsLeft: Long = 0
  var nSimsLeftPercent: Long = 0
  var bestResult = ""
  var targetLength = 0
  val random = new Random()
  var parent: ActorRef = _

  def longestStr(s1: String, s2: String) = if (s1.length >= s2.length) s1 else s2

  def matchSubstring(str1: String, str2: String): String =
    str1.view.zip(str2).takeWhile(Function.tupled(_ == _)).map(_._1).mkString

  def handleResult(result: String) = {
    val betterResult = result.length > bestResult.length
    if (betterResult) {
      if (showProgress) print(result.substring(bestResult.length))
      bestResult = result
    } else if (showProgress && (nSimsLeft % nSimsLeftPercent == 0))
      print(".")
    if (targetLength == bestResult.length) // The monkeys matched the entire string!
      nSimsLeft = 0
    else
      nSimsLeft -= 1
    if (nSimsLeft < 1) {
      log.debug(s"\nLongest common substring: '$bestResult'; nSimsLeft=$nSimsLeft")
      context.children.foreach { monkey => context.stop(monkey) }
      parent ! BestResult(bestResult)
      self ! akka.actor.PoisonPill
    }
  }

  /** Keep asking the given Monkey to type until nSimsLeft reaches zero. */
  def respondToMonkey(monkey: ActorRef) = {
    if (nSimsLeft > 0) monkey ! DoSimulation(random.nextInt())
    else context.stop(monkey)
  }

  def receive = {
    case BookkeeperStart(target, allowableChars, numMonkeyActors) =>
      targetLength = target.length
      nSimsLeft = numMonkeyActors * numSims
      nSimsLeftPercent = nSimsLeft / 100
      parent = sender()

      /** Find the longest common substring where the target is matched against each segment of monkeyString */
      val longestCommonSubstring = (monkeyString: String) =>
        (0 to monkeyString.length - target.length)
          .map(j => matchSubstring(monkeyString.drop(j), target))
          .foldLeft("")(longestStr)

      val monkeyProps = Props(classOf[Monkey], target.length, allowableChars, longestCommonSubstring)
      1 to numMonkeyActors foreach { i =>
        context.actorOf(monkeyProps, name=s"monkey-$i")
      }

    case MonkeyReady(monkey) =>
      log.debug(s"BookKeeper got MonkeyReady message from ${monkey.path}")
      respondToMonkey(monkey)

    case MonkeyResult(result, monkey) =>
      //if (result.length>0)
        log.debug(s"BookKeeper got MonkeyResult message '$result' from ${monkey.path}")
      respondToMonkey(monkey)
      handleResult(result)
  }
}

/** @param strLen number of characters to generate during a simulation
 * @param alphabet alphabet to use when generating characters
 * @param scoringFn opaque scoring function that receives the generated string by this monkey simulation and returns the longest matching substring */
class Monkey(strLen: Int, alphabet: String, scoringFn: String => String) extends Actor with ActorLogging {
  protected[solutions] val random = new Random()
  protected[solutions] val alphabetLength = alphabet.length

  override def preStart() = context.parent ! MonkeyReady(self)

  def receive = {
    case DoSimulation(seed) =>
      log.debug(s"Monkey ${self.path} got DoSimulation message")
      val resultStr = scoringFn(randomString)
      sender ! MonkeyResult(resultStr, self)
  }

  /** Generate a string of length n of random characters taken from alphabet */
  protected[solutions] def randomString: String = (1 to strLen).map { _ =>
    val index = random.nextInt(alphabetLength)
    alphabet(index)
  }.mkString
}
