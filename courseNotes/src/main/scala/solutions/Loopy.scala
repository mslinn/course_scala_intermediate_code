package solutions

object Loopy extends App {
  1 to 10 filter( _%2==0) foreach { i => println("abcdefghijk".substring(0, i)) }
}
