package foopkg {
  trait CanFoo[A] {
    def foos(x: A): String
  }

  object CanFoo {
    implicit val companionIntFoo: CanFoo[Int] = new CanFoo[Int] {
      def foos(x: Int): String = "companionIntFoo:" + x.toString
    }
  }

  trait Implicit {
    implicit lazy val intFoo: CanFoo[Int] = new CanFoo[Int] {
      def foos(x: Int): String = "traitFoo:" + x.toString
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

