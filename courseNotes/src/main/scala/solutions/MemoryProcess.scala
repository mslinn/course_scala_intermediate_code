package solutions

import scala.sys.process._
import java.net.URL

object MemoryProcess extends App {
  val p = Process(new URL("https://raw.github.com/mslinn/ExecutorBenchmark/master/README.md"))
  val os = new java.io.ByteArrayOutputStream
  p #> os !!;
  os.toString.split(" ") foreach { w =>
    val word = w.length match {
      case 0 => ""
      case 1 => w.toUpperCase
      case _ => w.substring(0, 1).toUpperCase + w.substring(1)
    }
    print(s" $word")
  }
}
