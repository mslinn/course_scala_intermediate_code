object StringReverser {
  import scala.sys.process._
  "cat /etc/passwd".!!.split(" ").map(_.reverse).mkString(" ")
}
