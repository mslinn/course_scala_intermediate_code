object D2 {
  val array = Array.ofDim[Int](4, 4)
  array.foreach(row => println(row.mkString(", ")))
  for {
    i <- 0 until array(0).length
    j <- 0 until array(1).length
  } array(i)(j) = (i + 1) * 2 * (j + 1)
  array.foreach(row => println(row.mkString(", ")))

  for {
    i <- List(1,2,3)
    str <- List("a", "b", "c")
  } println(str * i)
}
