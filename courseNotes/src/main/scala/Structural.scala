object Ducks1 extends App {
  case class Mallard(age: Double) {
    def quack(count: Int): Unit = println("Quack! " * count)
  }

  case class FrenchDuck(weight: Double) {
    def quack(count: Int): Unit = println("Le quack! " * count)
  }

  val mallard: { def quack(count: Int): Unit } = Mallard(4)
  val frenchDuck: { def quack(count: Int): Unit } = FrenchDuck(5)

  mallard.quack(2)
  frenchDuck.quack(3)
}

object Ducks2 extends App {
  type Duck = { def quack(count: Int): Unit }

  case class Mallard(age: Double) {
    def quack(count: Int): Unit = println("Quack! " * count)
  }

  case class FrenchDuck(weight: Double) {
    def quack(count: Int): Unit = println("Le quack! " * count)
  }

  val mallard: Duck = Mallard(4)
  val frenchDuck: Duck = FrenchDuck(5)

  mallard.quack(2)
  frenchDuck.quack(3)
}

object Structural extends App {
  type Closeable = { def close(): Unit }

  def using[A <: Closeable, B](closeable: A)(f: A ⇒ B): B = {
    try {
      f(closeable)
    } finally {
      try {
        closeable.close()
      } catch { case _: Throwable ⇒ () }
    }
  }

  val byteStream = new java.io.ByteArrayInputStream("hello world".getBytes)
  using(byteStream){ in ⇒
    val str = io.Source.fromInputStream(in).mkString
    println(s"'$str' has ${str.length} characters")
  }
}

object Structural2 extends App {
  type Closeable = { def close(): Unit }

  def using[A <: Closeable, B](closeable: ⇒ A)(f: A ⇒ B): B = {
    val closeableRef = closeable // only reference closeable once
    try {
      f(closeableRef)
    } finally {
      try {
        closeableRef.close()
      } catch { case _: Throwable ⇒ () }
    }
  }

  val byteStream = new java.io.ByteArrayInputStream("hello world".getBytes)
  using(byteStream){ in ⇒
    val str = io.Source.fromInputStream(in).mkString
    println(s"'$str' has ${str.length} characters")
  }
}

object SelfStructural extends App {
  type Openable = { def open(): Unit }
  type Closeable = { def close(): Unit }

  trait Door { self: Openable with Closeable ⇒
    def doSomething(f: () ⇒ Unit): Unit = try {
      open()
      f()
    } finally {
      close()
    }
  }

  class FrontDoor extends Door {
    def open(): Unit = println("Door is open")

    def walkThrough(): Unit = doSomething { () ⇒ println("Walking through door") }

    def close(): Unit = println("Door is closed")
  }

  val frontDoor = new FrontDoor
  frontDoor.walkThrough()
}
