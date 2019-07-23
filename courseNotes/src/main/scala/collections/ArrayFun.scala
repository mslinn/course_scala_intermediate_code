package collections

class ArrayFun extends App {
  object ArrayFun extends App {
    val a1 = Array(1, 2, 3)
    println(s"a1=${ a1.mkString(", ") }")

    val a2 = Array.empty[Int]

    val a3: Array[Int] = Array(1, 2, 3)

    a1(0) = a1(0) + 1
    println(s"a1=${ a1.mkString(", ") }")
    a1.update(0, a1(0) + 1)
    println(s"a1=${ a1.mkString(", ") }")
  }
}
