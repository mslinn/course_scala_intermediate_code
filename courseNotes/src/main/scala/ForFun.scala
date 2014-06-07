object ForFun extends App {
  val vector = Vector(0, 1, 2, 3)

  println(s"""vector.map(x => x.toString) = ${vector.map(x => x.toString)}""")
  println(s"""List(1, 2, 3).map(_.toString) = ${List(1, 2, 3).map(_.toString)}""")

  for ( i <- 1 to 3 ) println("Hello, world!")

  val array = Array.ofDim[Int](4, 4)
  for {
   i <- 0 until array(0).length
   j <- 0 until array(1).length
  } array(i)(j) = (i+1) * 2*(j+1)
  array.foreach(row => println(row.mkString(", ")))

  for {
    i <- List(1, 2, 3)
    string <- List("a", "b", "c")
  } println(string * i)

  for (i <- 1 to 10 if i % 2 == 0) println(i)
  1 to 10 filter( _ % 2 == 0) foreach { i => println(i) }

  val vector2 = Vector(Some(1), None, Some(3), Some(4))
  vector2.filter(_.isDefined).flatMap { v => Some(v.get*2) }
  val fc1 = vector2.filter(_.isDefined).flatMap { v => Some(v.get*2) }

  val result = for {
    v <- vector2
    x <- v
  } yield x*2

  val sameResult = for {
    v: Option[Int] <- vector2
    x: Int <- v
  } yield x*2

  val selectedKeys = Map("selectedKeys"->Seq("one", "two", "three"))
  val otherKeys = Map("otherKeys"->Seq("four", "five"))
  val list: List[Map[String, Seq[String]]] = List(selectedKeys, otherKeys)
  val result2: List[String] = for {
    data <- list
    selectedKeysSeq <- data.get("selectedKeys").toList
    id <- selectedKeysSeq.toList
  } yield id

  val result3: List[String] = for {
    data: Map[String, Seq[String]] <- list
    selectedKeysSeq: Seq[String] <- data.get("selectedKeys").toList
    id: String <- selectedKeysSeq.toList
  } yield id

  val result4: List[String] = list.flatMap { data: Map[String, Seq[String]] =>
    data.get("selectedKeys").toList.flatMap { selectedKeysSeq: Seq[String] =>
      selectedKeysSeq
    }
  }

  println(s"""result = $result""")
  println(s"""result2 = $result2""")
  println(s"""result3 = $result3""")
  println(s"""result4 = $result4""")
}

object ForFun2 extends App {
  case class Postcard(state: String, from: String, to: String) {
    def generate: String = s"Dear $to,\n\nWish you were here in $state!\n\nLove, $from\n\n"
  }

  val locations  = List("Bedrock", "Granite City")
  val relatives  = List("Barney", "Betty")
  val travellers = List("Wilma", "Fred")

  def writePostCards(locations: List[String], travellers: List[String], relatives: List[String]): List[Postcard] =
    for {
      sender    <- travellers
      recipient <- relatives
      state     <- locations
    } yield Postcard(state, sender, recipient)

  val postcards: List[Postcard] = writePostCards(locations, travellers, relatives)
  val output = postcards.map(_.generate).mkString("\n")
  println(output)
}

object ForFun3 extends App {
  case class Money(dollars: Int) {
    def -(cost: Money): Money = Money(dollars - cost.dollars)

    def +(extra: Money): Money = Money(dollars + extra.dollars)

    def >=(other: Money): Boolean = dollars >= other.dollars

    def <=(other: Money): Boolean = dollars <= other.dollars

    def ===(other: Money): Boolean = dollars == other.dollars

    override def hashCode = dollars.hashCode
  }

  case class Wallet(money: Money) {
    def -(cost: Money): Wallet = Wallet(money - cost)

    def +(extra: Money): Wallet = Wallet(money + extra)
  }

  class InventoryItem(val weight: Int, val price: Money)

  case class CharcoalBag(override val weight: Int, override val price: Money) extends InventoryItem(weight, price)

  case class LighterFluid(override val weight: Int, override val price: Money) extends InventoryItem(weight, price)

  case class Tofu(override val weight: Int, override val price: Money) extends InventoryItem(weight, price)

  class Inventory(val quantity: Int, val item: InventoryItem) {
    def buy(wallet: Wallet): Option[(Wallet, InventoryItem)] =
      if (isNotEmpty && wallet.money>=item.price) {
        val newWallet = wallet - item.price
        Some((newWallet, item))
      } else None

    def isNotEmpty = quantity > 0
  }

  case class CharcoalBagInventory(override val quantity: Int) extends Inventory(quantity, CharcoalBag(5, Money(10)))

  case class LighterFluidInventory(override val quantity: Int) extends Inventory(quantity, LighterFluid(2, Money(7)))

  case class TofuInventory(override val quantity: Int) extends Inventory(quantity, Tofu(1, Money(2)))

  case class BBQ(charcoalBag: CharcoalBag, lighterFluid: LighterFluid) {
    def light: Option[BBQ] =
      if (charcoalBag.weight>1 && lighterFluid.weight>1) {
        Some(BBQ(charcoalBag.copy(weight=charcoalBag.weight-1), lighterFluid.copy(weight=lighterFluid.weight-1)))
      } else None

    def grill: String = "Yummy dinner!"
  }

  var wallet                = Wallet(Money(100)) // change to 10 to cause pizza to be ordered due to insufficient funds
  var charcoalBagInventory  = CharcoalBagInventory(100)
  var lighterFluidInventory = LighterFluidInventory(50)
  var tofuInventory         = TofuInventory(12)

  var maybeBBQ: Option[BBQ] = for {
    (wallet2, charcoalBag: CharcoalBag) <- charcoalBagInventory.buy(wallet).orElse {
          println("Too poor to buy charcoal.")
          None
        }
    (wallet3, lighterFluid: LighterFluid) <- lighterFluidInventory.buy(wallet2).orElse {
          println("Too poor to buy lighter fluid.")
          None
        }
    (wallet4, tofu) <- tofuInventory.buy(wallet3).orElse {
          println("Too poor to buy tofu.")
          None
        }
    wallet = wallet4 // only reduce wallet contents if all purchases succeed
    bbq = BBQ(charcoalBag, lighterFluid)
    newBBQ <- bbq.light.orElse {
          println("Not enough BBQ material to light it.")
          None
        }
  } yield newBBQ

  def orderPizza: String = "Not pizza again!"

  val result = maybeBBQ.fold(orderPizza)(_.grill)
  println(result)
}
