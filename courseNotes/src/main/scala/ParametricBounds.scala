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

  def base(container: Container) = container.getClass.getName
  base(invariantContainingBase)
  base(invariantContainingSub)
  base(covariantContainingBase)
  base(covariantContainingSub)
  base(contravariantContainingBase)
  base(contravariantContainingSub)

  def invariantWithBase(container: InvariantContainer[BaseClass]) = container.getClass.getName
  invariantWithBase(invariantContainingBase)

  def invariantWithSuper(container: InvariantContainer[SubClass]) = container.getClass.getName
  invariantWithSuper(invariantContainingSub)

  def covariantWithBase(container: CovariantContainer[BaseClass]) = container.getClass.getName
  covariantWithBase(covariantContainingBase)
  covariantWithBase(covariantContainingSub)

  def covariantWithSuper(container: CovariantContainer[SubClass]) = container.getClass.getName
  covariantWithSuper(covariantContainingSub)

  def contravariantWithBase(container: ContravariantContainer[BaseClass]) = container.getClass.getName
  contravariantWithBase(contravariantContainingBase)

  def contravariantWithSuper(container: ContravariantContainer[SubClass]) = container.getClass.getName
  contravariantWithSuper(contravariantContainingBase)
  contravariantWithSuper(contravariantContainingSub)
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
    val items = collection.mutable.ListBuffer.empty[A]

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

object TypeSafety2 extends App {
  case class Container2[+A](a: A) {
    def consume[B >: A](b: B): Unit = println(s"$a $b")
  }

  Container2("Hello to all my").consume("fans")
}
