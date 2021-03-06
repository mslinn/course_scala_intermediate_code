import java.io.{BufferedReader, FileReader}
import scala.collection.mutable.ListBuffer

object Variance extends App {
  class BaseClass
  class SubClass extends BaseClass

  trait Container
  class InvariantContainer[T](t: T) extends Container
  class CovariantContainer[+T](t: T) extends Container
  class ContravariantContainer[-T](t: T) extends Container

  val baseClass = new BaseClass
  val subClass  = new SubClass

  val invariantContainingBase     = new InvariantContainer(baseClass)
  val invariantContainingSub      = new InvariantContainer(subClass)

  val covariantContainingBase     = new CovariantContainer(baseClass)
  val covariantContainingSub      = new CovariantContainer(subClass)

  val contravariantContainingBase = new ContravariantContainer(baseClass)
  val contravariantContainingSub  = new ContravariantContainer(subClass)

  val icb1 = new InvariantContainer(baseClass)
  val idb2 = new InvariantContainer[BaseClass](baseClass)
  val idb3: InvariantContainer[BaseClass] = new InvariantContainer(baseClass)
  val idb4 = new InvariantContainer[BaseClass](subClass)

  def base(container: Container): String = container.getClass.getName
  base(invariantContainingBase)
  base(invariantContainingSub)
  base(covariantContainingBase)
  base(covariantContainingSub)
  base(contravariantContainingBase)
  base(contravariantContainingSub)

  def invariantWithBase(container: InvariantContainer[BaseClass]): String = container.getClass.getName
  invariantWithBase(invariantContainingBase)

  def invariantWithSuper(container: InvariantContainer[SubClass]): String = container.getClass.getName
  invariantWithSuper(invariantContainingSub)

  def covariantWithBase(container: CovariantContainer[BaseClass]): String = container.getClass.getName
  covariantWithBase(covariantContainingBase)
  covariantWithBase(covariantContainingSub)

  def covariantWithSuper(container: CovariantContainer[SubClass]): String = container.getClass.getName
  covariantWithSuper(covariantContainingSub)

  def contravariantWithBase(container: ContravariantContainer[BaseClass]): String = container.getClass.getName
  contravariantWithBase(contravariantContainingBase)

  def contravariantWithSuper(container: ContravariantContainer[SubClass]): String = container.getClass.getName
  contravariantWithSuper(contravariantContainingBase)
  contravariantWithSuper(contravariantContainingSub)
}

object ParametricBounds extends App {
  abstract class Clothing(val size: Int, val manufacturer: String) extends Ordering[Clothing] {
    def compare(a: Clothing, b: Clothing): Int =  {
      val primaryKey = a.size - b.size
      if (primaryKey!=0) primaryKey else a.manufacturer compare b.manufacturer
    }

    override def equals(other: Any): Boolean = {
      try {
        val that = other.asInstanceOf[Clothing]
        this.size == that.size && this.manufacturer==that.manufacturer
      } catch {
        case e: Exception => false
      }
    }

    override def hashCode: Int = super.hashCode

    override def toString = s"$productPrefix by $manufacturer of size $size"

    def productPrefix: String
  }

  object Clothing {
    implicit val ClothingOrdering: Ordering[Clothing] = Ordering.by { clothing: Clothing =>
      (clothing.size, clothing.manufacturer)
    }
  }

  case class Dress(override val size: Int, override val manufacturer: String) extends Clothing(size, manufacturer)

  case class Pants(override val size: Int, override val manufacturer: String) extends Clothing(size, manufacturer)

  case class Hat(override val size: Int, override val manufacturer: String) extends Clothing(size, manufacturer)

  class ShoppingCart[A <: Clothing] {  // ShoppingCart can hold Clothing and subclasses
    val items: collection.mutable.ListBuffer[A] = ListBuffer.empty

    def pick(item: A, count: Int): ShoppingCart[A] = {
      1 to count foreach { i =>
        items.+=:(item)
        s"Adding size ${item.size} by ${item.manufacturer} to shopping cart"
      }
      this
    }

    override def toString: String = {
      val strings = items.map(item => s"${item.productPrefix} size ${item.size} by ${item.manufacturer}").mkString("\n  ", "\n  ", "\n")
      s"ShoppingCart has ${items.size} items in it:$strings"
    }
  }

  val clothing: Array[Clothing] = Array(Hat(5, "Gucci"), Hat(4, "Ralph Lauren"))

  val hat   = Hat(5, "Gucci")
  val pants = Pants(5, "Ralph Lauren")
  val dress = Dress(4, "Donna Karan")

  val shoppingCart = new ShoppingCart[Clothing]
                           .pick(hat, 2)
                           .pick(dress, 3)
                           .pick(pants, 5)
  println(shoppingCart)


  /** Bag can hold Clothing and subclasses */
  class Bag[T <: Clothing] {
    import collection.mutable

    val items: mutable.Buffer[Clothing] = mutable.Buffer.empty

    def put[U >: T <: Clothing](item: U, quantity: Int): Bag[T] = {
      1 to quantity foreach { x => items += item }
      this
    }

    def findBySize(i: Int): List[Clothing] = items.filter(_.size == i).toList

    def findByManufacturer(s: String): List[Clothing] = items.filter(_.manufacturer == s).toList

    override def toString: String = {
      val strings = items.map(item => s"${ item.productPrefix } size ${ item.size } by ${ item.manufacturer }").mkString("\n  ", "\n  ", "\n")
      s"Bag has ${ items.size } items in it:$strings"
    }
  }

  val bag = new Bag[Clothing].put(hat, 1).put(pants, 2).put(dress, 3)
  println(bag.findBySize(5).mkString("Bag contains:\n  ", "\n  ", ""))
  println(bag.findByManufacturer("Donna Karan").mkString("Bag contains:\n  ", "\n  ", ""))
}

object UpperBound extends App {
  import java.io.File

  trait Output {
    def maybeFile: Option[File]
  }

  case class Storage(key: String, value: String) extends Output {
    val maybeFile: Option[File] = try {
      import java.nio.file._
      val file = new File(key).toPath
      val path: Path = Files.write(file, value.getBytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
      Some(path.toFile)
    } catch {
      case e: Exception =>
        println(e)
        None
    }
  }

  def read[A <: Output](a: A): String = {
    val maybeFile = a.maybeFile
    maybeFile.map(readLines).getOrElse("")
  }

  def readLines(file: File): String =
    using(new BufferedReader(new FileReader(file))) { _.readLine() }

  def using[A <: AutoCloseable, B](resource: A)
                                  (block: A => B): B = try block(resource) finally resource.close()

  val readBackContents: String = read(Storage("storage.txt", "Blah blah blah"))
  println(s"Contents are '$readBackContents'")
}

object TypeSafety2 extends App {
//  case class Container2[+A] {
//    def consume(a: A): Unit = println(a)
//  }
//  error: covariant type A occurs in contravariant position in type A of value a
//  def consume(a: A): Unit = println(a)


//  val ss: Array[String] = Array("a", "b", "c")
//  val os: Array[Object] = ss
//  error: type mismatch;
//  found   : Array[String]
//  required: Array[Object]
//  Note: String <: Object, but class Array is invariant in type T.
//    You may wish to investigate a wildcard such as `_><: Object`. (SLS 3.2.10)
//  val os: Array[Object] = ss
//  ^>

  case class Container3[+A](a: A) {
    def consume[B >: A](b: B): Unit = println(s"$a $b")
  }

  Container3("Hello to all my").consume("fans")
}
