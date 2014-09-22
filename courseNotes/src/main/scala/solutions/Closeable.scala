package solutions

trait Closeable {
  import scala.util.Try

  //type Closer = AutoCloseable // this works fine, but there is a more flexible approach:
  /** Alias for structural type that has a method called close which does not accept parameters and returns nothing */
  type Closer = { def close(): Unit }

  /** Modified to handle Closeable or AutoCloseable by using a structural type */
  def withCloseable[C <: Closer, T](factory: => C)(operation: C => T): Try[T] = {
    val closeable = factory
    try {
      import scala.util.Success
      val result: T = operation(closeable)
      closeable.close()
      Success(result)
    } catch {
      case throwable: Throwable =>
        import scala.util.Failure
        try { closeable.close() } catch { case _: Throwable => }
        println(throwable.toString)
        Failure(throwable)
    }
  }
}
