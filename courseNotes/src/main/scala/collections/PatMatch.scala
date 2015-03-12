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

  def hasLeadingZero2(array: Array[Int]): Boolean =
    array match {
      case Array(0, remainder) => true
      case _ ⇒ false
    }

  def hasLeading0(seq: Seq[Int]): Boolean = seq.headOption.contains(0)

  println(s"hasLeadingZero(List(0, 1, 2))=${hasLeadingZero(List(0, 1, 2))}")
  println(s"hasLeadingZero(List(1, 2, 3)) = ${hasLeadingZero(List(1, 2, 3))}")

  println(s"hasLeadingZero(List(0))=${hasLeadingZero(List(0))}")
  println(s"hasLeadingZero(List(1)) = ${hasLeadingZero(List(1))}")

  println(s"hasLeadingZero(Array(0, 1, 2))=${hasLeadingZero(Array(0, 1, 2))}")
  println(s"hasLeadingZero(Array(1, 2, 3)) = ${hasLeadingZero(Array(1, 2, 3))}")

  println(s"hasLeadingZero2(List(0, 1, 2))=${hasLeadingZero(List(0, 1, 2))}")
  println(s"hasLeadingZero2(List(1, 2, 3)) = ${hasLeadingZero(List(1, 2, 3))}")

  println(s"hasLeadingZero2(Array(0, 1, 2))=${hasLeadingZero(Array(0, 1, 2))}")
  println(s"hasLeadingZero2(Array(1, 2, 3)) = ${hasLeadingZero(Array(1, 2, 3))}")

  println(s"hasLeading0(List(0, 1, 2))=${hasLeading0(List(0, 1, 2))}")
  println(s"hasLeading0(List(1, 2, 3)) = ${hasLeading0(List(1, 2, 3))}")

  println(s"hasLeading0(Array(0, 1, 2))=${hasLeading0(Array(0, 1, 2))}")
  println(s"hasLeading0(Array(1, 2, 3)) = ${hasLeading0(Array(1, 2, 3))}")
}

/** Your mission: eat dessert.
  * Meals with only 1 course do not have dessert, so you can only eat dessert as part of a 3 course meal.
  * You can feed the first course to the dog if you don't like it, but the dog cannot eat spinach since that is poisonous to dogs. */
object MatchAlias extends App {
  case class Food(name: String, calories: Int, yumminess: Int) {
    def dogCanEat = name!="Spinach"
  }

  case class Menu(foods: Food*) {
    def totalCalories = foods.map(_.calories).sum

    def mostYucky = foods.sortBy(_.yumminess).head

    def mostTasty = foods.sortBy(_.yumminess).reverse.head

    def isWorthOrdering: Boolean = {
      //println(s"totalCalories=$totalCalories; mostYucky.yumminess=${mostYucky.yumminess}; mostTasty.yumminess=${mostTasty.yumminess}")
      totalCalories<200 || mostTasty.yumminess>=9
    }

    override def toString = s"${foods.map(_.name).mkString("_")}: totalCalories=$totalCalories; mostYucky=${mostYucky.name}; mostTasty=${mostTasty.name}"
  }

  val course1a = Food("Spinach",  10,  2)
  val course2a = Food("Turnips",  10,  1)
  val course1b = Food("Peas",     50,  5)
  val course2b = Food("Potatoes", 110, 6)
  val dessert  = Food("BelgianChocolate", 135, 9)

  val menu1 = Menu(course1a)
  val menu2 = Menu(course1a, course2a)
  val menu3 = Menu(course1a, course2a, dessert)
  val menu4 = Menu(course1b, course2a, dessert)
  val menu5 = Menu(course1b, course2b, dessert)

  val result = List(menu1, menu2, menu3, menu4, menu5) filter {
    case menu @ Menu(c1, c2, c3) if menu.isWorthOrdering || c1.dogCanEat => true
    case _ => false
  }

  println(result.mkString("\n"))
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

  def extract3[T](list: List[T]): String = list match {
      case List(x1, x2, x3, remainder) => s"x1=$x1, x2=$x2, x3=$x3"
      case _ => "Nope"
    }

  def extract3[T](array: Array[T]): String = extract3(array.toList)

  println(s"""extract("one two three blah blah".split(" ")=${extract("one two three blah blah".split(" "))}""")
  println(s"""extract2("one two three blah blah".split(" ").toList=${extract2("one two three blah blah".split(" ").toList)}""")
  println(s"""extract2("one two three blah blah".split(" ")=${extract2("one two three blah blah".split(" "))}""")
  println(s"""extract3("one two three blah blah".split(" ").toList=${extract3("one two three blah blah".split(" ").toList)}""")
  println(s"""extract3("one two three blah blah".split(" ")=${extract3("one two three blah blah".split(" "))}""")
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
