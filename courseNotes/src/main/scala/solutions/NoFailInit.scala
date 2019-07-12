package solutions

import scala.language.higherKinds

object NoFailInit extends App {
  implicit class RichIterableOps[+A, +CC[_], +C](iterableOps: collection.IterableOps[A, CC, C]) {
    def init2: C = iterableOps.take(iterableOps.size-1)
  }

  println(s"List(1,2,3).init2 = ${ List(1,2,3).init2 }")
  println(s"List().init2 = ${ List().init2 }")
  println(s"Vector().init2 = ${ Vector().init2 }")
  println(s"Seq().init2 = ${ Seq().init2 }")
}
