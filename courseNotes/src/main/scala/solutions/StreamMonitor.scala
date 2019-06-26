package solutions

object StreamMonitor extends App {
  import java.io.{FileInputStream, InputStream, OutputStream}

  def alert(watchWord: String, inputStream: InputStream)
           (implicit alertStream: OutputStream): Unit = {
    val buffer = new Array[Byte](100)

    def producer: (Int, Array[Byte]) = {
      val len = inputStream.read(buffer)
      //println(s"Got $len bytes")
      (len, buffer)
    }

    def predicate(tuple: (Int, Array[Byte])): Boolean = {
      tuple._1 >= 0
    }

    def consumer(len: Int, buffer: Array[Byte])
                (implicit alertStream: OutputStream): Unit = {
      val string = buffer.map(_.toChar).mkString
      val index = string.indexOf(watchWord)
      if (index>=0)
        alertStream.write(s"\n\nFound '$watchWord' at position $index:\n$string".toCharArray.map(_.toByte))
    }

    Iterator continually { producer } takeWhile { predicate } foreach { case (len, bytes) => consumer(len, bytes) }
  }

  alert("bin", new FileInputStream("/etc/passwd"))(Console.out)
}
