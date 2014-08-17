object BoundAndGagged extends App {
  case class Blarg(i: Int, s: String)

  object OtherContext {
    var externallyBoundVar = Blarg(0, "")
  }

  import OtherContext.externallyBoundVar

  case class BadContainer(blarg: Blarg) {
    /** This function is not a combinator because it accesses boundVar, which is external state. BAD! */
    def unpredictable(f: Blarg => Blarg): Blarg =
      f(blarg.copy(i=blarg.i + externallyBoundVar.i))
  }

  // start of black box
  import akka.actor.ActorSystem
  import concurrent.duration._
  import concurrent.ExecutionContext.Implicits.global

  val system = ActorSystem()
  system.scheduler.schedule(0 milliseconds, 50 milliseconds) {
    // Continuously modify externallyBoundVar on another thread
    externallyBoundVar =  if (System.currentTimeMillis % 2 == 0) externallyBoundVar
      else externallyBoundVar.copy(i=externallyBoundVar.i + 1)
  }
  // end of black box

  val blarg = Blarg(1, "hello")
  do {
    val badContainer = BadContainer(blarg).unpredictable { blarg => blarg.copy(i=blarg.i*2) }
    println(s"badContainer returned ${badContainer.i}")
    if (badContainer.i>=3) {
      system.shutdown()
      sys.exit(0)
    }
  } while (true)
}

object Combinators extends App {
  val vector = Vector(0, 1, 2, 3)
  println(s"""vector.map( _/2 ) = ${vector.map( _/2 )}""")
  println(s"""vector.map { i => "abcdefg".substring(0, 1 + i) } = ${vector.map { i => "abcdefg".substring(0, 1 + i) }}""")

  println(s"""List(List(1), Nil, List(2,3)).flatten = ${List(List(1), Nil, List(2,3)).flatten}""")

  val vector2 = Vector(Some(1), None, Some(3), Some(4))
  println(s"""vector2.flatten = ${Vector(Some(1), None, Some(3), Some(4)).flatten}""")

  println(s"""vector2.filter(_.isDefined).flatMap(v => Some(v.get*2)) = ${vector2.filter(_.isDefined).flatMap(v => Some(v.get*2))}""")
  println(s"""vector2.filter(_.isDefined).map(v => Some(v.get*2)) = ${vector2.filter(_.isDefined).map(v => Some(v.get*2))}""")
  //println(s"""vector2.flatMap(v => Some(v.get*2)) = ${vector2.flatMap(v => Some(v.get*2))}""")
  //println(s"""vector2.filter(_.isDefined).flatMap(Some(_.get*2)) = ${vector2.filter(_.isDefined).flatMap(Some(_.get*2))}""")

  println(s"""vector.filter( _%2==0 ) = ${vector.filter( _%2==0 )}""")
  println(s"""vector.filterNot( _%2==0 ) = ${vector.filterNot( _%2==0 )}""")
  println(s"""vector.partition( _%2==0 ) = ${vector.partition( _%2==0 )}""")

  val (pass, fail) = vector.partition( _%2==0 )
  println(s"""pass = $pass""")
  println(s"""fail = $fail""")

  val (pass2: Vector[Int], fail2: Vector[Int]) = vector.partition( _%2==0 )
  println(s"""pass2 = $pass2""")
  println(s"""fail2 = $fail2""")

  println(s"""Vector(1, 2, 2, 3).distinct = ${Vector(1, 2, 2, 3).distinct}""")
  println(s"""vector.exists(_%2==0) = ${vector.exists(_%2==0)}""")
  println(s"""vector.find(_%2==0) = ${vector.find(_%2==0)}""")
  println(s"""vector.forall(_%2==0) = ${vector.forall(_%2==0)}""")

  val map = Map(1 -> "a", 2 -> "b", 3 -> "c", 4 -> "d")
  println(s"""map.map { nv => (nv._1 * 3, nv._2 * 3) } = ${ map.map { nv => (nv._1 * 3, nv._2 * 3) } }""")

  println(s"""map.filter( x => x._1 % 2 == 0 ) = ${map.filter( x => x._1 % 2 == 0 )}""")
  println(s"""map.filter(_._1%2==0) = ${map.filter(_._1%2==0)}""")

  val group = map.groupBy(_._1%2==0)
  println(s"""group(true) = ${group(true)}""")
  println(s"""group(false) = ${group(false)}""")

  val (even, odd) = map.partition(_._1%2==0)
  println(s"""even = $even""")
  println(s"""odd = $odd""")
}

object FuncMeth extends App {
  class Klass {
    val x = 3
    def method1(y: Int) = s"x=$x and y=$y from method 1"
    val function1 = (y: Int) => s"x=$x and y=$y from function 1"
  }

  val klass = new Klass()
  println(klass.method1(4))
  println(klass.function1(4))
}
