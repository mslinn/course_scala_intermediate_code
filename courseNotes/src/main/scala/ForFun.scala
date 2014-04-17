object ForFun extends App {
  println(s"""List(1, 2, 3).map(x => x.toString) = ${List(1, 2, 3).map(x => x.toString)}""")
  println(s"""List(1, 2, 3).map(_.toString) = ${List(1, 2, 3).map(_.toString)}""")
  println(s"""Vector(Some(1), None, Some(3), Some(4)).flatten = ${Vector(Some(1), None, Some(3), Some(4)).flatten}""")
  println(s"""Vector(Some(1), None, Some(3), Some(4)).filter(_.isDefined).flatMap(x => Some(x.get*2)) = ${Vector(Some(1), None, Some(3), Some(4)).filter(_.isDefined).flatMap(x => Some(x.get*2))}""")
  println(s"""Vector(Some(1), None, Some(3), Some(4)).filter(_.isDefined).map(x => Some(x.get*2)) = ${Vector(Some(1), None, Some(3), Some(4)).filter(_.isDefined).map(x => Some(x.get*2))}""")
  //println(s"""Vector(Some(1), None, Some(3), Some(4)).flatMap(x => Some(x.get*2)) = ${Vector(Some(1), None, Some(3), Some(4)).flatMap(x => Some(x.get*2))}""")
  //println(s"""Vector(Some(1), None, Some(3), Some(4)).filter(_.isDefined).flatMap(Some(_.get*2)) = ${Vector(Some(1), None, Some(3), Some(4)).filter(_.isDefined).flatMap(Some(_.get*2))}""")

  for ( i <- 1 to 3 ) println("Hello, world!")

  import java.util.Calendar

  val xmass = Calendar.getInstance()
  xmass.set(Calendar.MONTH, Calendar.DECEMBER)
  xmass.set(Calendar.DAY_OF_MONTH, 25)

  def secsUntilXmas: Long = (xmass.getTimeInMillis - System.currentTimeMillis) / 1000

  for ( i <- 1 to 3 ) {
    println(s"Only $secsUntilXmas seconds until Christmas!")
    Thread.sleep(5000)
  }

  val array = Array.ofDim[Int](4, 4)
  for {
   i <- 0 until array(0).length
   j <- 0 until array(1).length
  } array(i)(j) = (i+1) * 2*(j+1)
  array.foreach(row => println(row.mkString(", ")))

  for (i <- 1 to 10 if i % 2 == 0) println(i)
  1 to 10 filter( _ % 2 == 0) foreach { i => println(i) }

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
    data.get("selectedKeys").toList.flatMap { selectedKeysSeq: Seq[String] =>
      selectedKeysSeq
    }
  }

  println(s"""result = $result""")
  println(s"""result2 = $result2""")
  println(s"""result3 = $result3""")
}
