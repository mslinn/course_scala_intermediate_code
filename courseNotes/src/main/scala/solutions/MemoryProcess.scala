package solutions

import scala.sys.process._
import java.net.URL

object MemoryProcess extends App {
  val p = Process(new URL("https://raw.github.com/mslinn/ExecutorBenchmark/master/README.md"))
  val os = new java.io.ByteArrayOutputStream
  p #> os !;
  os.toString.split(" ") foreach { w =>
    print(s" ${w.capitalize}")
  }
}
