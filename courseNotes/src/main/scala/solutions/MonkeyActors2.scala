package solutions

import akka.actor.{ Actor, ActorRef, ActorSystem, Props, PoisonPill}
import concurrent.ExecutionContext.Implicits.global
import concurrent.duration._
import scala.util.Random

object MonkeySim2 extends App {
  /** Alphabet defining allowable characters */
  val allowableChars = """ !.,;'""" + (('a' to 'z').toList ::: ('A' to 'Z').toList ::: (0 to 9).toList).mkString

  /** Number of times to have a monkey generate and match a string */
  val numSims = 10000

  /** number of simulations to run */
  val strLen = 1000

  /** length of string a monkey types */
  val numMonkeyActors = 30

  /** number of Actors doing simulations */
  val showProgress = false // show progress minimally

  val target = "I thought I saw a lolcat! I did, I did see a lolcat!"

  implicit val system = ActorSystem("MonkeySim2")

  print("Monkey Simulation running ")
  val bookKeeper = system.actorOf(Props(classOf[BookKeeper], numSims, showProgress, system), name="bookKeeper")
  val monkeyProps = Props(classOf[Monkey], target, strLen, allowableChars)
  1 to numMonkeyActors foreach { i =>
    system.actorOf(monkeyProps, name=s"monkey-$i")
  }
  // The actors are created, but they need a message to make them do work
}

case class IAmHere(monkey: ActorRef)

case class DoSimulation(seed: Int)

case class Result(result: String, monkey: ActorRef)

class BookKeeper(numSims: Int, showProgress: Boolean=false, system: ActorSystem) extends Actor {
  var nSimsLeft: Long = numSims
  var bestResult = ""
  private val randGen = Random

  def handleResult(result: String) = {
    if (showProgress && nSimsLeft % (numSims / 50) == 0) print(".")
    if (nSimsLeft > 0 && result.length > bestResult.length) {
      bestResult = result
      if (showProgress) print("+")
    }
    nSimsLeft -= 1
    if (nSimsLeft == 0) {
      println(s"\nLongest common substring: '$bestResult'")
      system.scheduler.scheduleOnce(1.second, self, PoisonPill) // Is this here so the message has time to print out?
    } else if (nSimsLeft < 0) {
      if (showProgress) print("-")
    }
  }

  def respondToMonkey(monkey: ActorRef) = {
    if (nSimsLeft > 0) monkey ! DoSimulation(randGen.nextInt())
    else system.stop(monkey)
  }

  def receive = {
    case IAmHere(monkey) =>
      respondToMonkey(monkey)

    case Result(result, monkey) =>
      handleResult(result)
      respondToMonkey(monkey)
  }
}

class Monkey(target: String, strLen: Int, allowableChars: String) extends Actor {
  val bookKeeper = context.system.actorSelection("bookKeeper")

  override def preStart() = bookKeeper ! IAmHere(self)

  /** Find the longest common substring where the target is matched against each segment of monkeyString */
  def longestCommonSubstring(monkeyString: String) =
    (0 to monkeyString.length - target.length)
      .map(j => matchSubstring(monkeyString.drop(j), target))
      .foldLeft("")(longestStr)

  def longestStr(s1: String, s2: String) = if (s1.length >= s2.length) s1 else s2

  def matchSubstring(str1: String, str2: String): String =
    str1.view.zip(str2).takeWhile(Function.tupled(_ == _)).map(_._1).mkString

  def receive = {
    case DoSimulation(seed) =>
      val mRandGen = new Random(seed)
      val monkeyString = randomString(strLen, mRandGen)
      val resultStr = longestCommonSubstring(monkeyString)
      bookKeeper ! Result(resultStr, self)

    case IAmHere =>
      bookKeeper ! IAmHere(self)
  }

  /** Generate a string of length n of random characters */
  def randomString(n: Int, rand: Random) = (1 to n).map { _ =>
      val i = rand.nextInt(allowableChars.length - 1)
      allowableChars(i)
  }.mkString
}
