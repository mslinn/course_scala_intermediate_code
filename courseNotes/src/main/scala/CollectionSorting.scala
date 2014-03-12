import collection._

object CollectionSorting extends App {
  println(s"""List(3, 7, 5, 2).sortWith((x, y) => x < y) = ${List(3, 7, 5, 2).sortWith((x, y) => x < y)}""")
  println(s"""List(3, 7, 5, 2).sortWith(_ < _) = ${List(3, 7, 5, 2).sortWith(_ < _)}""")

  println(s""" = ${}""")
  println(s""" = ${}""")
  println(s""" = ${}""")
  println(s""" = ${}""")
  println(s""" = ${}""")
  println(s""" = ${}""")
  println(s""" = ${}""")

}
