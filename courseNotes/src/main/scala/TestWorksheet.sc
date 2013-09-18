object x {
  val alphabet =
    """ !.,;'""" +
      (('a' to 'z').toList :::
      ('A' to 'Z').toList :::
      (0 to 9).toList).mkString
  val random = Random
  val i = random.next
  alphabet.substring()
}
