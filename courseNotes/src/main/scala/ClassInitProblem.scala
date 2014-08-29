object Outer { 
  val x = 3
  List(0).par.map(_ + Outer.x) 
}

