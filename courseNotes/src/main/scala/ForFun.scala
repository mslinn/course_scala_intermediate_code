object ForFun extends App {
  val vector = Vector(0, 1, 2, 3)

  println(s"""vector.map(x => x.toString) = ${vector.map(x => x.toString)}""")
  println(s"""List(1, 2, 3).map(_.toString) = ${List(1, 2, 3).map(_.toString)}""")

  for ( i <- 1 to 3 ) println("Hello, world!")

  val array = Array.ofDim[Int](4, 4)
  for {
   i <- 0 until array(0).length
   j <- 0 until array(1).length
  } array(i)(j) = (i+1) * 2*(j+1)
  array.foreach(row => println(row.mkString(", ")))

  for {
    i <- List(1, 2, 3)
    string <- List("a", "b", "c")
  } println(string * i)

  for (i <- 1 to 10 if i % 2 == 0) println(i)
  1 to 10 filter( _ % 2 == 0) foreach { i => println(i) }

  val vector2 = Vector(Some(1), None, Some(3), Some(4))
  vector2.filter(_.isDefined).flatMap { v => Some(v.get*2) }
  val fc1 = vector2.filter(_.isDefined).flatMap { v => Some(v.get*2) }

  val result = for {
    v <- vector2
    x <- v
  } yield x*2

  val sameResult = for {
    v: Option[Int] <- vector2
    x: Int <- v
  } yield x*2

  val selectedKeys = Map("selectedKeys"->Seq("one", "two", "three"))
  val otherKeys = Map("otherKeys"->Seq("four", "five"))
  val list: List[Map[String, Seq[String]]] = List(selectedKeys, otherKeys)
  val result2: List[String] = for {
    data <- list
    selectedKeysSeq <- data.get("selectedKeys").toList
    id <- selectedKeysSeq.toList
  } yield id

  val result3: List[String] = for {
    data: Map[String, Seq[String]] <- list
    selectedKeysSeq: Seq[String] <- data.get("selectedKeys").toList
    id: String <- selectedKeysSeq.toList
  } yield id

  val result4: List[String] = list.flatMap { data: Map[String, Seq[String]] =>
    data.get("selectedKeys").toList.flatMap { selectedKeysSeq: Seq[String] =>
      selectedKeysSeq
    }
  }

  println(s"""result = $result""")
  println(s"""result2 = $result2""")
  println(s"""result3 = $result3""")
  println(s"""result4 = $result4""")
}