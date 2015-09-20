import java.io.{FileInputStream, File, Closeable, InputStream}
import scala.util.{Failure, Success, Try}

object UnlessRevisited extends App {
  import java.util.Date

  /** @param color pigment color of this spray can of paint
    * @param capacityRemaining milliliters of paint remaining in this can
    * @param gramsPerMeter amount of paint required to spray a line one meter long
    * @param maybeWhenLastShaken Some(Date) of when the can was last shaken, defaults to None which means never shaken */
  case class SprayCan(
    color: String,
    var capacityRemaining: Double,
    gramsPerMeter: Double,
    secondsBetweenShakes: Long=15,
    var maybeWhenLastShaken: Option[Date]=None
  ) {
    @inline def distanceRemaining: Double = capacityRemaining / gramsPerMeter

    @inline def doesNotNeedShaking: Boolean =
      maybeWhenLastShaken.exists(new Date().getTime - _.getTime <= secondsBetweenShakes * 1000)

    @inline def doIfNonEmpty(action: SprayCan => Unit) =
      if (nonEmpty) action(this) else println("Sorry, paint can is empty")

    @inline def isEmpty: Boolean = capacityRemaining==0

    @inline def needsShaking = !doesNotNeedShaking

    @inline def nonEmpty: Boolean = !isEmpty

    @inline def shake(): Unit = maybeWhenLastShaken = {
      println("Can is ready to spray")
      Some(new Date)
    }

    @inline def spray(distance: Double, degrees: Double) = {
      if (needsShaking) println("Cannot spray, need to shake the paint can")
      else {
        val remaining = distanceRemaining
        if (remaining<distance) println (f"Spraying $distance%.1f meters @ $degrees%.1f degrees.")
        else println (f"Spraying $remaining%.1f meters @ $degrees%.1f degrees.")
        capacityRemaining = math.max(0, capacityRemaining-gramsPerMeter*distance)
        if (isEmpty) println("Can is empty.")
      }
    }
  }

  val greenSprayCan = SprayCan("green", 355, 6.3)

  greenSprayCan.doIfNonEmpty { self =>
    self.spray(3, 45)
    self.shake()
    self.spray(3, -45)
    self.spray(3, 180)
    if (self.nonEmpty)
      println(f"Spray can has ${self.capacityRemaining}%.1f milliliters remaining and can spray ${self.distanceRemaining}%.1f meters more.")
  }

  greenSprayCan.doIfNonEmpty { self =>
    self.shake()
    self.spray(3, 45)
    self.spray(3, -45)
    self.spray(3, 180)
    if (self.nonEmpty)
      println(f"Spray can has ${self.capacityRemaining}%.1f milliliters remaining and can spray ${self.distanceRemaining}%.1f meters more.")
  }
}

object PartiallyAppliedStuff {
  def read(inputStream: InputStream): String =
    Iterator.continually(inputStream.read)
            .takeWhile(_ != -1)
            .map(_.toChar)
            .mkString

  def first2lines(msg: String, inputStream: InputStream): String = {
    val string = read(inputStream)
    s"$msg\n " + string.split("\n").take(2).mkString("\n  ")
  }

  def withT[T, U](t: T)(operation: T => U): U = operation(t)

  def withCloseable[C <: Closeable, T](factory: => C)(operation: C ⇒ T): Try[T] = {
      val closeable = factory
      try {
        val result: T = operation(closeable)
        closeable.close()
        Success(result)
      } catch {
        case throwable: Throwable ⇒
          try { closeable.close() } catch { case _: Throwable ⇒ }
          Failure(throwable)
      }
    }

  val file = new File("/etc/passwd")
  def fileInputStream = new FileInputStream(file)

}

object PartiallyAppliedUsing extends App {
  import java.io._
  import PartiallyAppliedStuff._
  import scala.util.Try

  withT(new java.util.Date) { println }

  withCloseable(fileInputStream)(fis => first2lines("withCloseable example 1", fis))
  withCloseable(fileInputStream)(first2lines("withCloseable example 2", _))

  val tryContents1: Try[String] = withCloseable(fileInputStream) { first2lines("tryContents1", _) }
  val tryContents2: Try[String] = withCloseable(fileInputStream) { first2lines("tryContents2", _).toUpperCase }

  val openFileInputStream3 = withCloseable(fileInputStream)(_: FileInputStream => String)
  val tryContents3: Try[String] = openFileInputStream3 { first2lines("tryContents3", _).reverse }

  val openFileInputStream4: (FileInputStream => String) => Try[String] = withCloseable(fileInputStream)
  val tryContents4: Try[String] = openFileInputStream4(first2lines("tryContents4", _).toUpperCase.reverse)

  val noGood = withCloseable(fileInputStream) _

  val openFileInputStream5 = withCloseable[FileInputStream, String](fileInputStream) _
  val tryContents5: Try[String] = openFileInputStream5(first2lines("tryContents5", _).toUpperCase)

  def openFileInputStream6[T] = withCloseable[FileInputStream, T](fileInputStream) _
  val tryContents6: Try[String] = openFileInputStream6 { first2lines("tryContents6", _).replace(":", "#") }

  def withBufferedInputStream1[T](input: File) =
    withCloseable(new BufferedInputStream(new FileInputStream(input))) (_: BufferedInputStream => T)

  def withBufferedInputStream[T](input: File): (BufferedInputStream => T) => Try[T] =
    withCloseable(new BufferedInputStream(new FileInputStream(input)))

  withBufferedInputStream(file) { first2lines("withBufferedInputStream", _) } foreach println

  def withBufferedOutputStream[T](input: File): (BufferedOutputStream ⇒ T) ⇒ Try[T] =
    withCloseable(new BufferedOutputStream(new FileOutputStream(input)))

  withBufferedInputStream(file) { inputStream ⇒
    withBufferedOutputStream(new File("/tmp/blah")) { outputStream ⇒
      read(inputStream).foreach(outputStream.write(_))
    }
  }

  def stringMunger(a: String)(f: String ⇒ String) = f(a)
  val abc = stringMunger("abc") _
  abc { x => x * 4 }
  abc { _ * 4 }

  def twoStringMunger[T](a: String)(b: String)(f: (String, String) ⇒ T) = f(a, b)
  val twoStringToIntPF = twoStringMunger[Int]("abcdefghi")("def") _
  twoStringToIntPF { (s1, s2) ⇒ s1.indexOf(s2) }

  def concatStringsAndFnValue(a: String)(b: String)(f: (String, String) ⇒ String) = a + b + f(a, b)
  val r3b = concatStringsAndFnValue("x")("y") { (u, v) ⇒ (u.length + v.length).toString }
  val xyHuh = concatStringsAndFnValue("x")("y") _
  def u2v3(u: String, v: String): String = (u.length * 2 + v.length * 3).toString
  xyHuh { u2v3 }
}
