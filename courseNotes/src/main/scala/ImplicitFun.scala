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

  object `package` {
    def foo[A: CanFoo](x: A): String = implicitly[CanFoo[A]].foos(x)
  }

  object CompanionMain extends App {
    println(foo(1))
  }

  object TraitMain extends App with Implicit {
    println(foo(1))
  }
}

