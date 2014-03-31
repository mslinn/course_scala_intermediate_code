object MF {
  val list: List[Int] = for {
    x <- List(1+2)
    y <- List(2+3)
    z <- List(4+5, 7)
  } yield x + y + z

  val option: Option[Int] = for {
    x <- Option(1+2)
    y <- Option(2+3)
    z <- Option(4+5)
  } yield x + y + z
}
