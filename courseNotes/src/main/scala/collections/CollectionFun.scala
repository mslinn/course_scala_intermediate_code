package collections

import scala.collection._

object CollectionFun extends App {
  val iterator = io.Source.fromFile("build.sbt")
  println(s"iterator.mkString #1 = ${ iterator.mkString }")
  println(s"iterator.mkString #2 = ${ iterator.mkString }")

  val charList = io.Source.fromFile("build.sbt").toList

  // IntelliJ IDEA does not like trailing dots (messes up indentation)
  val lines = io.Source.fromFile("build.sbt")
    .getLines
    .filter(_.contains("Scala"))
    .mkString(", ")
.replaceAll(" +", " ")
  println(s"lines = $lines")

  val lines2 = io.Source.fromFile("build.sbt")
    .getLines
    .filter(_.contains("Scala"))
    .mkString(">>>\n\t", "\n\t", "\n<<<")
    .replaceAll(" +", " ")
  println(s"lines2 = $lines2")

  val x = mutable.Set(1, 2, 3)
  val y = immutable.Set(1, 2, 3)

  println(s"immutable.HashSet(1.0, 2) = ${ immutable.HashSet(1.0, 2) }")
  val set: immutable.HashSet[Number] = immutable.HashSet(1.0, 2)  // IntelliJ shows this line has errors, but IntelliJ is wrong and this works just fine
  println(s"set = $set")

  val set2: immutable.Set[Number] = immutable.HashSet(1.0, 2)     // IntelliJ shows this line has errors, but IntelliJ is wrong and this works just fine
  println(s"set2 = $set2")

  def newCollection(values: Number*): immutable.Set[Number] = immutable.Set[Number](values: _*)
  println(s"newCollection(1.0, 2) = ${ newCollection(1.0, 2) }")
  val set3 = immutable.HashSet[Number](1.0, 2)
  println(s"set3 = $set3")
}

/** Custom collection authors need to know about steppers; this course does not address that audience */
object StepperFun extends App {
  val treeSeqMap: Map[Int, String] = immutable.TreeSeqMap(1 -> "One", -2 -> "Negative two", 3 -> "Three")
  val y: IntStepper = treeSeqMap.keysIterator.stepper
  val stepper: IntStepper = treeSeqMap.keyStepper
  val isES = stepper.isInstanceOf[Stepper.EfficientSplit]
  val x: IntStepper = stepper.trySplit()
}
