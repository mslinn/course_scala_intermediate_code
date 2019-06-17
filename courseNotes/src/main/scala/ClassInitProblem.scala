import scala.collection.parallel.CollectionConverters._

// This hangs:
object Outer extends App {
  val x = 3
  List(0).par.map(_ + Outer.x)
}

object NoHanging1 extends App {
  class Outer {
    val x = 3
    List(0).par.map(_ + Outer.this.x)
  }

  new Outer().x
}

object NoHanging2 extends App {
  def method(x: Int) = List(0).par.map(_ + x)

  object Outer {
    val x = 3
    method(x)
  }

  val y = Outer.x
}
