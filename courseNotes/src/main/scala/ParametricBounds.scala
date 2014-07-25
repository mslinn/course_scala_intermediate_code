import scala.collection.mutable.ListBuffer

object ExtendJavaSet extends App {

  trait IgnoredCaseSet[T] extends java.util.Set[T] {
    abstract override def add(t: T): Boolean =
      t match {
        case string: String => super.add(string.toLowerCase.asInstanceOf[T])

        case obj => super.add(obj)
      }

    abstract override def contains(obj: Object) =
      obj match {
        case s: String =>
          super.contains(s.toLowerCase)

        case o =>
          super.contains(o)
      }
  }

  class MySet extends java.util.HashSet[String] with IgnoredCaseSet[String]

  val mySet = new MySet() // Java sets are mutable, only the reference is immutable
  mySet.add("One")
  mySet.add("Two")
  mySet.add("Three")
  println(s"mySet=$mySet")
}

object TypeRules extends App {
  class BaseClass
  class SuperClass extends BaseClass

  trait Container
  class InvariantContainer[T](t: T) extends Container
  class CovariantContainer[+T](t: T) extends Container
  class ContravariantContainer[-T](t: T) extends Container

  val baseClass = new BaseClass
  val superClass = new SuperClass

  val invariantContainingBase      = new InvariantContainer[BaseClass](baseClass)
  val invariantContainingSuper     = new InvariantContainer[SuperClass](superClass)

  val covariantContainingBase      = new CovariantContainer[BaseClass](baseClass)
  val covariantContainingSuper     = new CovariantContainer[SuperClass](superClass)

  val contravariantContainingBase  = new ContravariantContainer[BaseClass](baseClass)
  val contravariantContainingSuper = new ContravariantContainer[SuperClass](superClass)

  def base(container: Container) = ???
  base(invariantContainingBase)
  base(invariantContainingSuper)
  base(covariantContainingBase)
  base(covariantContainingSuper)
  base(contravariantContainingBase)
  base(contravariantContainingSuper)

  def invariantWithBase(container: InvariantContainer[BaseClass]) = ???
  invariantWithBase(invariantContainingBase)

  def invariantWithSuper(container: InvariantContainer[SuperClass]) = ???
  invariantWithSuper(invariantContainingSuper)

  def covariantWithBase(container: CovariantContainer[BaseClass]) = ???
  covariantWithBase(covariantContainingBase)
  covariantWithBase(covariantContainingSuper)

  def covariantWithSuper(container: CovariantContainer[SuperClass]) = ???
  covariantWithSuper(covariantContainingSuper)

  def contravariantWithBase(container: ContravariantContainer[BaseClass]) = ???
  contravariantWithBase(contravariantContainingBase)

  def contravariantWithSuper(container: ContravariantContainer[SuperClass]) = ???
  contravariantWithSuper(contravariantContainingBase)
  contravariantWithSuper(contravariantContainingSuper)
}

object ParametricBounds extends App {
  abstract class Clothing(val size: Int, val manufacturer: String) extends Ordering[Clothing] {
    def compare(a: Clothing, b: Clothing) =  {
      val primaryKey = a.size - b.size
      if (primaryKey!=0) primaryKey else a.manufacturer compare b.manufacturer
    }

    override def equals(other: Any) = {
      try {
        val that = other.asInstanceOf[Clothing]
        this.size == that.size && this.manufacturer==that.manufacturer
      } catch {
        case e: Exception => false
      }
    }

    override def hashCode = super.hashCode

    override def toString = s"$productPrefix by $manufacturer of size $size"

    def productPrefix: String
  }

  object Clothing {
    implicit val ClothingOrdering = Ordering.by { clothing: Clothing =>
      (clothing.size, clothing.manufacturer)
    }
  }

  case class Dress(override val size: Int, override val manufacturer: String) extends Clothing(size, manufacturer)

  case class Pants(override val size: Int, override val manufacturer: String) extends Clothing(size, manufacturer)

  case class Hat(override val size: Int, override val manufacturer: String) extends Clothing(size, manufacturer)

  class ShoppingCart[A <: Clothing] {  // ShoppingCart can hold Clothing and subclasses
    val items = ListBuffer.empty[A]

    def pick(item: A, count: Int): ShoppingCart[A] = {
      1 to count foreach { i =>
        items.+=:(item)
        s"Adding size ${item.size} by ${item.manufacturer} to shopping cart"
      }
      this
    }

    override def toString = {
      val strings = items.map(item => s"${item.productPrefix} size ${item.size} by ${item.manufacturer}").mkString("\n  ", "\n  ", "\n")
      s"ShoppingCart has ${items.size} items in it:$strings"
    }
  }

  val hat   = new Hat(5, "Gucci")
  val pants = new Pants(5, "Ralph Lauren")
  val dress = new Dress(4, "Donna Karan")

  val shoppingCart = new ShoppingCart[Clothing].pick(hat, 2).pick(dress, 3).pick(pants, 5)
  println(shoppingCart)


  /** Bag can hold Clothing and subclasses */
  class Bag[T <: Clothing] {
    import collection.mutable

    val items = mutable.MutableList.empty[Clothing]

    def put[U >: T <: Clothing](item: U, quantity: Int): Bag[T] = {
      1 to quantity foreach { x => items += item }
      this
    }

    def findBySize(i: Int): List[Clothing] = items.filter(_.size == i).toList

    def findByManufacturer(s: String): List[Clothing] = items.filter(_.manufacturer == s).toList

    override def toString = {
      val strings = items.map(item => s"${item.productPrefix} size ${item.size} by ${item.manufacturer}").mkString("\n  ", "\n  ", "\n")
      s"Bag has ${items.size} items in it:$strings"
    }
  }

  val bag = new Bag[Clothing].put(hat, 1).put(pants, 2).put(dress, 3)
  println(bag.findBySize(5).mkString("Bag contains:\n  ", "\n  ", ""))
  println(bag.findByManufacturer("Donna Karan").mkString("Bag contains:\n  ", "\n  ", ""))
}
