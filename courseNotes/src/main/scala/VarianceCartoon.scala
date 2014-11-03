object VarianceCartoon extends App {
  trait EnergySource
  trait Vegetable extends EnergySource
  trait Bamboo extends Vegetable

  trait Consumer
  trait Vegetarian extends Consumer
  trait Panda extends Vegetarian

  trait Entertainment
  trait Music extends Entertainment
  trait Metal extends Music

  trait Producer
  trait Musician extends Producer
  trait HeadBanger extends Musician

  trait Entertainer[???] {
    def feed(entertainer: ???): Unit = {}
    def entertain(entertainer: ???): Unit = {}
  }

  trait Restaurant {
    val producers: Seq[Producer] = List.empty
    val consumers: Seq[Consumer] = List.empty

    // define a method that can feed any Consumer the appropriate EnergySource or subtrait

    // define a method that can employ any Producer or subtrait to provide the desired entertainment
  }
}
