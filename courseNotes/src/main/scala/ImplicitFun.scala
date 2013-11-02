package foopkg {
  trait CanFoo[A] {
    def foos(x: A): String
  }

  object CanFoo {
    implicit val companionIntFoo = new CanFoo[Int] {
      def foos(x: Int) = "companionIntFoo:" + x.toString
    }
  }

  trait Implicit {
    implicit lazy val intFoo = new CanFoo[Int] {
      def foos(x: Int) = "traitFoo:" + x.toString
    }
  }

  /** Equivalent to a package object except `package object foopkg` would require a separate file */
  object `package` {
    def foo[A : CanFoo](x: A): String = implicitly[CanFoo[A]].foos(x)
  }

  object CompanionMain extends App {
    println(foo(1))
  }

  object TraitMain extends App with Implicit {
    println(foo(1))
  }
}

package yeller {
  case class YellerString(s: String) {
    def yell: String = s.toUpperCase + "!!"
  }

  trait Implicit {
    implicit def stringToYellerString(s: String): YellerString = YellerString(s)
  }

  object `package` extends Implicit
}

object YellerMain extends App {
  import yeller._
  println("banana".yell)
}

package userpkg {
  object `package` extends yeller.Implicit

  object Yeller2Main extends App {
    println("banana".yell)
  }
}
