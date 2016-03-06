package solutions

object TwoD extends App {
  val array = Array.ofDim[Int](3, 4)
  for {
    row    <- array.indices
    column <- array.head.indices
  } array(row)(column) = (row + 1) * 2 * (column + 1)

  for { row <- array } println(row.mkString(", "))

  array.foreach(row => println(row.mkString(", ")))
}
