object ImplicitClasses extends App {
  implicit class randomName(int: Int) { def length: Int = int.toString.length }

  println(s"5.length=${5.length}")
}

object ImplicitValueClass extends App {
  implicit class randomName2(val long: Long) extends AnyVal { def length: Int = long.toString.length }

  println(s"5L.length=${5L.length}")
}

object AppleFanBoi extends App {
  implicit class IosInt(val i: Int) extends AnyVal { def s: Int = i + 1 }

  println(s"I have an iPhone ${4.s}")
}

object EnhanceMyLibrary extends App {
  case class Dog(name: String) {
    override def equals(that: Any): Boolean = canEqual(that) && hashCode==that.hashCode
    override def hashCode = name.hashCode
  }

  class Stick

  case class Ball(color: String)

  implicit class DogCommands(val dog: Dog) extends AnyVal {
    def call(me: String): String = s"Here, ${dog.name} come to $me"

    def fetch(stick: Stick): String = s"${dog.name}, fetch the stick!"

    def fetch(ball: Ball): String = s"${dog.name}, fetch the ${ball.color} ball!"
  }

  val dog = Dog("Fido")
  println(s"""dog.call("me") => ${dog.call("me")}""")
  println(s"""dog.fetch(new Stick) => ${dog.fetch(new Stick)}""")
  println(s"""dog.fetch(new Ball("green")) => ${dog.fetch(new Ball("green"))}""")
}
