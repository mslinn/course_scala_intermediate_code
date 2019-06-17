import java.io.{BufferedReader, File, FileReader}

trait AutoCloseableLike {
  def using[A <: AutoCloseable, B](resource: A)
                                  (block: A => B): B =
    try block(resource) finally resource.close()

  def readLines(file: File): String =
    using(new BufferedReader(new FileReader(file))) { _.readLine() }
}
