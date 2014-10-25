object VarianceCartoon extends App{
  class EnergySource
  class Vegetable extends EnergySource
  class Bamboo extends Vegetable

  class Consumer
  class Vegetarian extends Consumer
  class Panda extends Vegetarian

  class Entertainment
  class Music extends Entertainment
  class Metal extends Music

  class Producer
  class Musician extends Producer
  class HeadBanger extends Musician

  class Entertainer[???] {
    def feed(entertainer: ???): Unit = {}
    def entertain(entertainer: ???): Unit = {}
  }

  class Restaurant {
    val producers: Seq[Producer] = List.empty
    val consumers: Seq[Consumer] = List.empty

    // define a method that can feed any Consumer the appropriate EnergySource or subclass

    // define a method that can employ any Producer or subclass to provide the desired entertainment
  }
}
