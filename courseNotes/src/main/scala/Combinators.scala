object BoundAndGagged extends App {
  import akka.actor.ActorSystem
  import concurrent.duration._
  import concurrent.ExecutionContext.Implicits.global

  case class Blarg(i: Int, s: String)

  object OtherContext {
    var externallyBoundVar = Blarg(0, "")
  }

  import OtherContext.externallyBoundVar

  case class BadContainer(blarg: Blarg) {
    /** This function is not a combinator because it accesses boundVar, which is external state. BAD! */
    def unpredictable(f: Blarg => Blarg): Blarg = f(blarg.copy(i=blarg.i + externallyBoundVar.i))
  }

  val system = ActorSystem()
  // Continuously modify externallyBoundVar on another thread
  system.scheduler.schedule(0 milliseconds, 50 milliseconds) {
    externallyBoundVar =  if (System.currentTimeMillis % 2 == 0) externallyBoundVar else externallyBoundVar.copy(i=externallyBoundVar.i + 1)
  }

  val blarg = Blarg(1, "hello")
  do {
    val badContainer = BadContainer(blarg).unpredictable { blarg => blarg.copy(i=blarg.i*2) }
    println(s"badContainer returned ${badContainer.i}")
    if ( badContainer.i>=3) {
      system.shutdown()
      sys.exit(0)
    }
  } while (true)
}

object Combinators extends App {
  val vector = Vector(0, 1, 2, 3)
  println(s"""vector.map( _/2 ) = ${vector.map( _/2 )}""")
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

  val map = Map(1 -> "eh", 2 -> "bee", 3 -> "sea", 4 -> "d")
  println(s"""map.filter(_._1%2==0) = ${map.filter(_._1%2==0)}""")
  val group = map.groupBy(_._1%2==0)
  println(s"""group(true) = ${group(true)}""")
  println(s"""group(false) = ${group(false)}""")

  val (even, odd) = map.partition(_._1%2==0)
  println(s"""even = $even""")
  println(s"""odd = $odd""")
}
