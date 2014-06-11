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

  val maybeName= Some("Chloe")
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
