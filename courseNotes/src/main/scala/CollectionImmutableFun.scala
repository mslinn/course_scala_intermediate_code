object CollectionImmutableFun extends App {
  val list1 = List(1, 2, 3)
  val list2 = 4 :: 5 :: 6 :: Nil
  val list3 = 7 +: 8 +: 9 +: Nil
  println(s"""list1 ::: list2 = ${list1 ::: list2}""")
  println(s"""list1 ++ list2 = ${list1 ++ list2}""")

  case class Thing(i: Int, s: String)

  val thing1 = Thing(1, "z")
  val thing2 = Thing(2, "y")
  val thing3 = Thing(3, "x")
  val things = List(thing3, thing1, thing2)
  println(s"""List(thing3, thing1, thing2) = ${List(thing3, thing1, thing2)}""")
  println(s"""List.empty[Double] = ${List.empty[Double]}""")
  val emptyList: List[Double] = List.empty
  println(s"""emptyList = $emptyList""")
  val emptyList2: List[Double] = List()
  println(s"""emptyList2 = $emptyList2""")

  println(s"""5 :: Nil = ${5 :: Nil}""")
  println(s"""Nil.::(5) = ${Nil.::(5)}""")
  println(s"""5 +: Nil = ${5 +: Nil}""")
  println(s"""Nil.+:(5) = ${Nil.+:(5)}""")
  println(s""" = ${}""")

  val vector1 = Vector(1, 2, 3)
  println(s"""0 +: vector1 = ${0 +: vector1}""")
  println(s"""Vector(thing1, thing2, thing3) = ${Vector(thing1, thing2, thing3)}""")

  val vector2 = Vector(4, 5, 6)
  println(s"""vector1 ++ vector2 = ${vector1 ++ vector2}""")

  def doSomething(seq: Seq[Int]) = seq.foreach(println)

  println(s"""doSomething(List(1, 2, 3)) = ${doSomething(List(1, 2, 3))}""")
  println(s"""doSomething(Vector(4, 5, 6)) = ${doSomething(Vector(4, 5, 6))}""")

  println(s"""things.sortBy(_.i) = ${things.sortBy(_.i)}""")
  println(s"""things.sortBy(_.s) = ${things.sortBy(_.s)}""")

  val tuples = Vector((4, "z"), (2, "q"), (5, "b"))
  println(s"""tuples.sortBy(_._1) = ${tuples.sortBy(_._1)}""")
  println(s"""tuples.sortBy(_._2) = ${tuples.sortBy(_._2)}""")
  println(s"""tuples.sortBy(t => (t._1, t._2)) = ${tuples.sortBy(t => (t._1, t._2))}""")

  val seq: Seq[Int] = Vector(1, 2, 3)
  println(s"""seq.head = ${seq.head}""")
  try { println(s"""Vector.empty.head = ${Vector.empty.head}""") } catch { case e: Exception => println(e.getMessage) }
  println(s"""seq.last = ${seq.last}""")
  try { println(s"""Nil.head = ${Nil.head}""") } catch { case e: Exception => println(e.getMessage) }
  println(s"""seq.lastOption = ${seq.lastOption}""")
  println(s"""Nil.lastOption = ${Nil.lastOption}""")

  println(s"""seq.init = ${seq.init}""")
  println(s"""Vector(1).init = ${Vector(1).init}""")
  try { println(s"""Nil.init = ${Nil.init}""") } catch { case e: Exception => println(e.getMessage) }
  println(s"""seq.take(2) = ${seq.take(2)}""")
  println(s"""seq.take(0) = ${seq.take(0)}""")
  println(s"""seq.take(4) = ${seq.take(4)}""")
  println(s"""seq.drop(2) = ${seq.drop(2)}""")
  println(s"""seq.drop(0) = ${seq.drop(0)}""")
  println(s"""seq.drop(-4) = ${seq.drop(-4)}""")
  println(s"""seq.drop(4) = ${seq.drop(4)}""")

  val ids = Stream.continually(System.nanoTime)
  println(s"""ids.take(5).toVector = ${ids.take(5).toVector}""")
  println(s"""ids.head = ${ids.head}""")

  val stream = Stream.continually(System.nanoTime).takeWhile(_ => !Console.readLine("\nMore? <Y/n>: ").toLowerCase.startsWith("n"))
}
