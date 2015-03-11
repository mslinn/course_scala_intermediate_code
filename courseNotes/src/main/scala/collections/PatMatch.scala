package collections

object IsEmpty extends App {
  def isEmpty[T](seq: Seq[T]): Boolean = seq match {
    case Seq() => true
    case _ => false
  }

  println(s"isEmpty(List(1, 2, 3))=${isEmpty(List(1, 2, 3))}")
  println(s"isEmpty(List())=${isEmpty(List())}")
  println(s"isEmpty(Nil)=${isEmpty(Nil)}")

  println(s"List(1, 2, 3).isEmpty=${List(1, 2, 3).isEmpty}")
  println(s"Array(1, 2).isEmpty=${Array(1, 2).isEmpty}")
  println(s"Vector.empty.isEmpty=${Vector.empty.isEmpty}")
}

object HasLeadingZero extends App {
  def hasLeadingZero(seq: Seq[Int]): Boolean =
    seq match {
      case Seq(0, _*) => true
      case _ ⇒ false
    }

  def hasLeadingZero(array: Array[Int]): Boolean =
    array match {
      case Array(0, _*) => true
      case _ ⇒ false
    }

  def hasLeadingZero2(seq: Seq[Int]): Boolean =
    seq match {
      case Seq(0, remainder) => true
      case _ ⇒ false
    }

  println(s"hasLeadingZero(List(0, 1, 2))=${hasLeadingZero(List(0, 1, 2))}")
  println(s"hasLeadingZero(List(1, 2, 3)) = ${hasLeadingZero(List(1, 2, 3))}")

  println(s"hasLeadingZero(Array(0, 1, 2))=${hasLeadingZero(Array(0, 1, 2))}")
  println(s"hasLeadingZero(Array(1, 2, 3)) = ${hasLeadingZero(Array(1, 2, 3))}")

  println(s"hasLeadingZero2(List(0, 1, 2))=${hasLeadingZero(List(0, 1, 2))}")
  println(s"hasLeadingZero2(List(1, 2, 3)) = ${hasLeadingZero(List(1, 2, 3))}")


  def hasLeading0(seq: Seq[Int]): Boolean = seq.headOption.contains(0)

  println(s"hasLeading0(List(0, 1, 2))=${hasLeading0(List(0, 1, 2))}")
  println(s"hasLeading0(List(1, 2, 3)) = ${hasLeading0(List(1, 2, 3))}")

  println(s"hasLeading0(Array(0, 1, 2))=${hasLeading0(Array(0, 1, 2))}")
  println(s"hasLeading0(Array(1, 2, 3)) = ${hasLeading0(Array(1, 2, 3))}")
}

object MatchAlias extends App {
  def isReadme(string: String): Boolean = string.toLowerCase.startsWith("readme")

  val mergeStrategy = List("a", "b", "README.md")

  val result = mergeStrategy match {
    case Seq("reference.conf") => Some("MergeStrategy.concat")
    case Seq(ps @ _*) if isReadme(ps.last) => Some("MergeStrategy.rename")
    case _ => None
  }

  println(s"result=$result")


  val result2 = mergeStrategy match {
    case Seq("reference.conf") => Some("MergeStrategy.concat")
    case ps: Seq[String] if isReadme(ps.last) => Some("MergeStrategy.rename")
    case _ => None
  }

  println(s"result2=$result2")
}

object ListExtractAnyLen extends App {
  def extract[T](list: List[T]): String = list match {
    case x1 :: x2 :: x3 :: rest => s"x1=$x1, x2=$x2, x3=$x3"
    case _ => "Nope"
  }

  def extract[T](array: Array[T]): String = extract(array.toList)

  def extract2[T](list: List[T]): String = list match {
      case List(x1, x2, x3, _*) => s"x1=$x1, x2=$x2, x3=$x3"
      case _ => "Nope"
    }

  def extract2[T](array: Array[T]): String = array match {
    case Array(x1, x2, x3, _*) => s"x1=$x1, x2=$x2, x3=$x3"
    case _ => "Nope"
  }

  println(s"""extract("one two three blah blah".split(" ")=${extract("one two three blah blah".split(" "))}""")
  println(s"""extract2("one two three blah blah".split(" ").toList=${extract2("one two three blah blah".split(" ").toList)}""")
  println(s"""extract2("one two three blah blah".split(" ")=${extract2("one two three blah blah".split(" "))}""")
}

object ListExtractAssertLen extends App {
  val result = "one two three".split(" ").toList match {
    case x1 :: x2 :: x3 :: Nil => s"x1=$x1, x2=$x2, x3=$x3"
    case _ => "Nope"
  }

  val result2 = "one two three".split(" ") match {
    case Array(x1, x2, x3) => s"x1=$x1, x2=$x2, x3=$x3"
    case _ => "Nope"
  }

  println(result)
  println(result2)
}

object VectorHeadTail extends App {
  def maybeHeadTail[A](vector: Vector[A]): Option[(A, Vector[A])] = vector match {
    case head +: tail => Some(head -> tail)
    case _ => None
  }

  println(s"maybeHeadTail(Vector(1, 2, 3, 4))=${maybeHeadTail(Vector(1, 2, 3, 4))}")
  println(s"maybeHeadTail(Vector(1))=${maybeHeadTail(Vector(1))}")
  println(s"maybeHeadTail(Vector())=${maybeHeadTail(Vector())}")
}

object VectorInitLast extends App {
  def maybeInitLast[A](vector: Vector[A]): Option[(Vector[A], A)] = vector match {
    case head :+ last => Some(head -> last)
    case _ => None
  }

  println(s"maybeInitLast(Vector(1, 2, 3, 4))=${maybeInitLast(Vector(1, 2, 3, 4))}")
  println(s"maybeInitLast(Vector(1))=${maybeInitLast(Vector(1))}")
  println(s"maybeInitLast(Vector())=${maybeInitLast(Vector())}")
}
