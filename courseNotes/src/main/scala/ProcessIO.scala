import scala.language.postfixOps
import scala.sys.process._

object ProcessIO extends App {
  val passwds = "ps -u" !!;
  println(s"passwds=$passwds")

  val fileNames = "ls" !!;
  println(s"fileNames=$fileNames")

  val grepX = "printf xray\\nyankee\\nzulu" #> "grep x" !!;
  println(s"grepX=$grepX")

  val cmd = List("psql", "-h", "localhost", "-U", "postgres", "-p", "5432", "-c", "select * from pg_tables where schemaname='public'")
  val cwd = None
  val extraEnv = ("PGPASSWORD", "mypass")
  try {
    val result = Process(cmd, cwd, extraEnv).!!.trim
    println(s"psql tables are:\n$result")
  } catch {
    case e: RuntimeException =>

    case e: java.io.IOException =>
      println(s"${e.getMessage}\nPerhaps PostgreSQL is not installed on your system, or psql is not on the path?")

    case e: Exception =>
      println(e.getMessage)
  }
}

object URLBuilderDemo extends App {
  import java.io.File
  import java.net.URL
  import sys.process._

  new URL("http://scalacourses.com") #> new File("scalaCourses.html") !;
  println("cat scalaCourses.html" !!)
}

object FileBuilderDemo extends App {
  import java.io.File
  import sys.process._

  "ls" #> new File("dirContents.txt") !;
  println("cat dirContents.txt" !!)
}

object ComposedPBuilders extends App {
  import java.io.File
  import java.net.URL

  /** Reads the contents of the web page at `url` and saves into `fileName` */
  def readUrlPBuilder(urlStr: String, fileName: String): ProcessBuilder = try {
    val url = new URL(urlStr)
    url.openConnection().connect() // Might trigger an Exception in the current thread
    url #> new File(fileName) cat  // should not fail in other thread
  } catch {
    case e: Exception =>
      println(s"${e.getClass.getName}: ${e.getMessage}")
      Process(false)  // return a failed Process
  }

  readUrlPBuilder("http://scalacourses.com", "scalaCourses.html") !; // note the semicolon
  val wc = ("cat scalaCourses.html" #> "wc" !!) split " |\r|\n" filter(_.nonEmpty)
  println(s"scalacourses.com has ${wc(0)} lines, ${wc(1)} words and ${wc(2)} characters.")

  readUrlPBuilder("http://n_o_s_u_c_h.com", "nosuch.html")
  readUrlPBuilder("http://scalacourses.com", "scalacourses.html") #&& "echo yes" !; // note the semicolon
  readUrlPBuilder("http://n_o_s_u_c_h.com", "nosuch.html") #|| "echo no" !
}
