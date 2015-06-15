object ForTry extends App {
  import util.Try

  def tryCompute(a: Int, b: Int): Try[Int] = Try { a / b }

  def tryFor(w: Int, x: Int, y: Int, z: Int): Try[Int] = for {
    result1 <- tryCompute(w, x)
    result2 <- tryCompute(y, z)
  } yield result1 / result2

  println(s"""tryFor(2, 1, 4, 2) = ${tryFor(2, 1, 4, 2)}""")
  println(s"""tryFor(2, 0, 4, 2) = ${tryFor(2, 0, 4, 2)}""") // fails in first generator
  println(s"""tryFor(2, 1, 4, 0) = ${tryFor(2, 1, 4, 0)}""") // fails in second generator
  println(s"""tryFor(0, 1, 0, 2) = ${tryFor(0, 1, 0, 2)}""") // fails in yield generator
}

object ForFun extends App {
  val vector = Vector(0, 1, 2, 3)

  println( s"""vector.map(x => x.toString) = ${vector.map(x => x.toString)}""")
  println( s"""List(1, 2, 3).map(_.toString) = ${List(1, 2, 3).map(_.toString)}""")

  for (i <- 1 to 3) println("Hello, world!")

  val array = Array.ofDim[Int](4, 4)
  for {
    i <- 0 until array(0).length
    j <- 0 until array(1).length
  } array(i)(j) = (i + 1) * 2 * (j + 1)
  array.foreach(row => println(row.mkString(", ")))

  for {
    i <- List(1, 2, 3)
    string <- List("a", "b", "c")
  } println(string * i)

  for {
    i <- List(1, 2, 3)
  } {
    println(s"i=$i")
    for {
      string <- List("a", "b", "c")
    } println(string * i)
  }

  for {
    i <- List(1, 2, 3)
    _ <- List(println(s"i=$i"))
    string <- List("a", "b", "c")
  } println(string * i)

  var outerVariable = 0
  for {
    i <- List(1, 2, 3)
    _ <- List(outerVariable = i) if i % 2 == 0
    string <- List("a", "b", "c")
  } println(string * i)
  println(s"outerVariable=$outerVariable")

  outerVariable = 0
  for {
    i <- List(1, 2, 3)
    outerVariable = i // wrong, defines a shadow variable
    string <- List("a", "b", "c") if i % 2 == 0
  } println(string * i)
  println(s"outerVariable=$outerVariable")

  val maybeName = Some("Chloe")
  for {
    x <- maybeName.map(_.toUpperCase).orElse(Some("UNKNOWN NAME"))
  } println(x)

  for (i <- 1 to 10 if i % 2 == 0) println(i)
  1 to 10 filter (_ % 2 == 0) foreach { i => println(i)}

  val vector2 = Vector(Some(1), None, Some(3), Some(4))
  vector2.filter(_.isDefined).flatMap { v => Some(v.get * 2)}
  for {v ← vector2 if v.isDefined} yield v.get * 2

  val result = for {
    v <- vector2
    x <- v
  } yield x * 2
  println(s"""result = $result""")

  val sameResult = for {
    v: Option[Int] <- vector2
    x: Int <- v
  } yield x * 2

  vector2.flatten.map {
    _ * 2
  }

  for {v ← vector2.flatten} yield v * 2
}

object ForFun1 extends App {
  val selectedKeys = Map("selectedKeys"->Seq("one", "two", "three"))
  val otherKeys = Map("otherKeys"->Seq("four", "five"))
  val list: List[Map[String, Seq[String]]] = List(selectedKeys, otherKeys)
  val result: List[String] = for {
    data <- list
    selectedKeysSeq <- data.get("selectedKeys").toList
    id <- selectedKeysSeq.toList
  } yield id

  val result2: List[String] = for {
    data: Map[String, Seq[String]] <- list
    selectedKeysSeq: Seq[String] <- data.get("selectedKeys").toList
    id: String <- selectedKeysSeq.toList
  } yield id

  val result3: List[String] = list.flatMap { data: Map[String, Seq[String]] =>
    data.get("selectedKeys").toList.flatMap { selectedKeysSeq: Seq[String] => selectedKeysSeq }
  }

  val result4 = list.flatMap { data =>
    data.get("selectedKeys").toList.flatMap { selectedKeysSeq => selectedKeysSeq }
  }

  println(s"""result = $result""")
  println(s"""result2 = $result2""")
  println(s"""result3 = $result3""")
  println(s"""result4 = $result4""")
}

object ForFun2 extends App {
  case class Postcard(state: String, from: String, to: String) {
    def generate: String = s"Dear $to,\n\nWish you were here in $state!\n\nLove, $from\n\n"
  }

  val locations  = List("Bedrock", "Granite City")
  val relatives  = List("Barney", "Betty")
  val travellers = List("Wilma", "Fred")

  def writePostCards(locations: List[String], travellers: List[String], relatives: List[String]): List[String] =
    for {
      sender    <- travellers
      recipient <- relatives
      state     <- locations
    } yield Postcard(state, sender, recipient).generate

  val postcards: List[String] = writePostCards(locations, travellers, relatives)
  val output = postcards.mkString("\n")
  println(output)
}

object ForFunMonads extends App {
  def reps(list: List[Int], maybeString: Option[String]): List[String] = for {
    j <- maybeString.toList
    i <- list
  } yield j*i
  println(reps(List(1, 2, 3), Some("a")))
}

object EitherFun extends App {
  val a: Either[Int, Int] = Right(1)
  val b: Either[Int, Int] = Left(1)

  val a: Either[Int, Int] = Right(1)
  val b: Either[Int, Int] = Left(2)
  val c: Either[Int, Int] = Left(3)

  val r1a = for {
    x <- a.right
  } yield x
  println(s"r1a=$r1a")

  val r1b = for {
    x <- a.left
  } yield x
  println(s"r1b=$r1b")

  val r2a = for {
    x <- a.right
    y <- b.right
  } yield y
  println(s"r2a=$r2a")

  val r2b = for {
    x <- a.right
    y <- b.left
  } yield y
  println(s"r2b=$r2b")

  val r2c = for {
    x <- a.left
    y <- b.right
  } yield y
  println(s"r2c=$r2c")

  val r2d = for {
    x <- a.left
    y <- b.left
  } yield y
  println(s"r2d=$r2d")

  val r3a = for {
    x <- a.right
    y <- b.right
    z <- c.right
  } yield z
  println(s"r3a=$r3a")

  val r3b = for {
    x <- a.right
    y <- b.right
    z <- c.left
  } yield z
  println(s"r3b=$r3b")

  val r3c = for {
    x <- a.right
    y <- b.left
    z <- c.right
  } yield z
  println(s"r3c=$r3c")

  val r3d = for {
    x <- a.right
    y <- b.left
    z <- c.left
  } yield z
  println(s"r3d=$r3d")

  val r3e = for {
    x <- a.left
    y <- b.right
    z <- c.right
  } yield z
  println(s"r3e=$r3e")

  val r3f = for {
    x <- a.left
    y <- b.right
    z <- c.left
  } yield z
  println(s"r3f=$r3f")

  val r3g = for {
    x <- a.left
    y <- b.left
    z <- c.right
  } yield z
  println(s"r3g=$r3g")

  val r3h = for {
    x <- a.left
    y <- b.left
    z <- c.left
  } yield z
  println(s"r3h=$r3h")

  val r4a = for {
    x <- a.right.toOption
  } yield x
  println(s"r4a=$r4a")

  val r4b = for {
    x <- a.left.toOption
  } yield x
  println(s"r4b=$r4b")

  val r4c = for {
    x <- a.right.toOption
    y <- b.right.toOption
  } yield y
  println(s"r4c=$r4c")

  val r4d = for {
    x <- a.right.toOption
    y <- b.left.toOption
  } yield y
  println(s"r4d=$r4d")

  val r4e = for {
    x <- a.left.toOption
    y <- b.right.toOption
  } yield y
  println(s"r4e=$r4e")

  val r4f = for {
    x <- a.left.toOption
    y <- b.left.toOption
  } yield y
  println(s"r4f=$r4f")
}
