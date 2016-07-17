object ImplicitClasses extends App {
  implicit class randomName(int: Int) {
    val length: Int = int.toString.length
  }

  println(s"5.length=${5.length}")
}

object ImplicitValueClass extends App {
  implicit class randomName2(val long: Long) extends AnyVal {
    @inline def length: Int = long.toString.length
  }

  println(s"5L.length=${5L.length}")
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

object Rates extends App {
  import SymbolToCurrency._

  println('CAD(100.0))
  println('USD(100.0))
  println('JPY(100.0))
  println('BLA(100.0))
}

object SymbolToCurrency {
  /** See https://openexchangerates.org/quick-start */
  implicit class SymbolLookup(val symbol: Symbol) extends AnyVal {
    @inline def apply(value: Double): String = try {
      val convertedValue = value * rateMap(symbol)
      s"$convertedValue ${symbol.name}"
    } catch {
      case nsee: NoSuchElementException =>
        println(s"Currency ${symbol.name} unknown. Available currencies are: ${rateMap.keys.map(_.name).toSeq.sorted.mkString(", ")}")
        ""
    }
  }

  // define open exchange key as an environment variable before runnin this (bogus key shown):
  // export OPEN_EXCHANGE_KEY=7364734638483498732987423
  protected val openExchangeKey = sys.env("OPEN_EXCHANGE_KEY")
  protected val urlStr = s"http://openexchangerates.org/api/latest.json?app_id=$openExchangeKey"
  protected val latestRates: String = io.Source.fromURL(urlStr).mkString
  protected val RateRegex = """(?s)rates.: \{(.*?)\}""".r.unanchored
  protected val RateRegex(rates) = latestRates

  protected val rateTuples: List[(Symbol, Double)] = (for {
    rateStr <- rates.split(",").toList
  } yield {
    rateStr.replaceAll("[ \n\"]", "").split(":") match {
      case Array(k, v) =>
        try {
          Some(Symbol(k) -> v.toDouble)
        } catch {
          case e: Exception =>
            println(s"${e.getClass.getName} while parsing $v: ${e.getMessage}; ignored")
            None
        }

      case wat =>
        println(s"$wat could not be parsed, ignored")
        None
    }
  }).flatten

  protected val rateMap: Map[Symbol, Double] = rateTuples.toMap

  def apply(symbol: Symbol): Double = rateMap(symbol)
}
