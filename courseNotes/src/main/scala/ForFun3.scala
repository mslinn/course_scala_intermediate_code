import util.{Try, Failure, Success}
import util.control.NoStackTrace

object ForFun3Model {

  case class Money(dollars: Int) {
    def -(cost: Money): Money = Money(dollars - cost.dollars)

    def +(extra: Money): Money = Money(dollars + extra.dollars)

    def >=(other: Money): Boolean = dollars >= other.dollars

    def <=(other: Money): Boolean = dollars <= other.dollars

    def ==(other: Money): Boolean = dollars == other.dollars

    override def hashCode = dollars.hashCode

    override def toString = s"$dollars dollars"
  }

  case class Wallet(money: Money) {
    def -(cost: Money): Wallet = Wallet(money - cost)

    def +(extra: Money): Wallet = Wallet(money + extra)

    override def toString = s"$money"
  }

  sealed abstract class InventoryItem(val weight: Int, val price: Money)

  case class CharcoalBag(override val weight: Int, override val price: Money) extends InventoryItem(weight, price)

  case class LighterFluid(override val weight: Int, override val price: Money) extends InventoryItem(weight, price)

  case class Tofu(override val weight: Int, override val price: Money) extends InventoryItem(weight, price)

  sealed abstract class Inventory(val quantity: Int, val item: InventoryItem) {
    def isNotEmpty = quantity > 0
  }

  case class CharcoalBagInventory(override val quantity: Int) extends Inventory(quantity, CharcoalBag(5, Money(10)))

  case class LighterFluidInventory(override val quantity: Int) extends Inventory(quantity, LighterFluid(2, Money(7)))

  case class TofuInventory(override val quantity: Int) extends Inventory(quantity, Tofu(1, Money(2)))

  case class BBQ(charcoalBag: CharcoalBag, lighterFluid: LighterFluid) {
    def grill: String = "Yummy dinner!"
  }

  var wallet                = Wallet(Money(100)) // change to 10 to cause pizza to be ordered due to insufficient funds
  var charcoalBagInventory  = CharcoalBagInventory(100)
  var lighterFluidInventory = LighterFluidInventory(50)
  var tofuInventory         = TofuInventory(12)

  def orderPizza: String = "Not pizza again!"
}

object ForFun3Option1 extends App {
  import ForFun3Model._

  implicit class RichInventory(inventory: Inventory) {
    def maybeBuy(wallet: Wallet): Option[(Wallet, InventoryItem)] =
      if (inventory.isNotEmpty && wallet.money >= inventory.item.price) {
        val newWallet = wallet - inventory.item.price
        Some((newWallet, inventory.item))
      } else None
  }

  implicit class RichBBQ(bbq: BBQ) {
    def maybeLight: Option[BBQ] =
      if (bbq.charcoalBag.weight > 1 && bbq.lighterFluid.weight > 1) {
        Some(BBQ(bbq.charcoalBag.copy(weight = bbq.charcoalBag.weight - 1), bbq.lighterFluid.copy(weight = bbq.lighterFluid.weight - 1)))
      } else None
  }

  def maybeBBQ: Option[BBQ] = for {
    (wallet2, charcoalBag: CharcoalBag) <- charcoalBagInventory.maybeBuy(wallet)
    (wallet3, lighterFluid: LighterFluid) <- lighterFluidInventory.maybeBuy(wallet2)
    (wallet4, tofu) <- tofuInventory.maybeBuy(wallet3)
    _ <- Some{ wallet = wallet4 } // only reduce wallet contents if all purchases succeed
    bbq = BBQ(charcoalBag, lighterFluid)
    newBBQ <- bbq.maybeLight
  } yield newBBQ

  val result = maybeBBQ.fold(orderPizza)(_.grill)
  println(result)
  println(s"wallet has ${wallet.money} remaining")
}

object ForFun3Option2 extends App {
  import ForFun3Model._
  import ForFun3Option1.{RichBBQ, RichInventory}

  def maybeBBQ: Option[BBQ] = for {
    (wallet2, charcoalBag: CharcoalBag) <- charcoalBagInventory.maybeBuy(wallet).orElse {
        println("Too poor to buy charcoal and the next door neighbor has none.")
        None
      }
    (wallet3, lighterFluid: LighterFluid) <- lighterFluidInventory.maybeBuy(wallet2).orElse {
        println("Borrowed some lighter fluid from next door.")
        Some((wallet2, LighterFluid(3, Money(0)))) // return default value and pass back unmodified wallet
      }
    (wallet4, tofu) <- tofuInventory.maybeBuy(wallet3).orElse {
        println("Borrowed some tofu from next door.")
        Some((wallet3, Tofu(1, Money(0))))  // return default value and pass back unmodified wallet
      }
    _ <- Some{ wallet = wallet4 } // only reduce wallet contents if all purchases succeed
    bbq = BBQ(charcoalBag, lighterFluid)
    newBBQ <- bbq.maybeLight.orElse {
        println("Not enough BBQ material to light it.")
        None
      }
  } yield newBBQ

  val result = maybeBBQ.fold(orderPizza)(_.grill)
  println(result)
  println(s"wallet has ${wallet.money} remaining")
}

object ForFun3Try extends App {
  import ForFun3Model._

  implicit class RichInventory(inventory: Inventory) {
    object InsufficientFunds extends Exception(s"Not enough money in the wallet to make a purchase") with NoStackTrace

    def tryBuy(wallet: Wallet): Try[(Wallet, InventoryItem)] =
      if (inventory.isNotEmpty && wallet.money >= inventory.item.price) {
        val newWallet = wallet - inventory.item.price
        Success((newWallet, inventory.item))
      } else Failure(InsufficientFunds)
  }

  implicit class RichBBQ(bbq: BBQ) {
    object BBQNoCharcoal extends Exception("Not enough charcoal left to light the BBQ") with NoStackTrace
    object BBQNoFluid extends Exception("Not enough lighter fluid left to light the BBQ") with NoStackTrace

    def tryLight: Try[BBQ] =
      if (bbq.charcoalBag.weight <= 1)
        Failure(BBQNoCharcoal)
      else if (bbq.lighterFluid.weight <= 1)
        Failure(BBQNoFluid)
      else
        Success(BBQ(bbq.charcoalBag.copy(weight = bbq.charcoalBag.weight - 1), bbq.lighterFluid.copy(weight = bbq.lighterFluid.weight - 1)))
  }

  def tryBBQ: Try[BBQ] = for {
    (wallet2, charcoalBag: CharcoalBag) <- charcoalBagInventory.tryBuy(wallet)
    (wallet3, lighterFluid: LighterFluid) <- lighterFluidInventory.tryBuy(wallet2)
    (wallet4, tofu) <- tofuInventory.tryBuy(wallet3)
    _ <- Try{ wallet = wallet4 } // only reduce wallet contents if all purchases succeed
    bbq = BBQ(charcoalBag, lighterFluid)
    newBBQ <- bbq.tryLight
  } yield newBBQ

  val result = tryBBQ.map(_.grill).recover {
    case x => // log the exception and pass it along
      println(x)
      x
  }.getOrElse(orderPizza)
  println(result)
  println(s"wallet has ${wallet.money} remaining")
}
