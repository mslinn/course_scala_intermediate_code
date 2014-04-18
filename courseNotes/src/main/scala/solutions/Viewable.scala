package solutions

object Viewable extends App {
  /** Speed of light in m/s */
  val C: Double = 299293458d

  /** @param weight in kilograms */
  case class Matter(name: String, weight: Double) {
    /** @return matter-energy equivalence in megajoules */
    def energy: Double = weight * C * C / 1000000d

    def megaJouleMsg: String = f"$name's mass-energy equivalence is $energy%.0f megajoules."
  }

  case class Animal(name: String, height: Double, weight: Double)
  case class Vegetable(name: String, height: Double, weight: Double)
  case class Mineral(name: String, weight: Double)

  case class Bug(name: String, height: Double, weight: Double, canFly: Boolean)
  case class Whale(name: String, height: Double, weight: Double, hasTeeth: Boolean)

  case class AppleTree(name: String, height: Double, weight: Double, age: Int)
  case class Grass(name: String, height: Double, weight: Double, edible: Boolean)

  case class Sand(name: String, color: String, weight: Double)
  case class Rock(name: String, color: String, weight: Double)

  implicit def sandToMineral(sand: Sand) = Mineral(sand.name, sand.weight)
  implicit def rockToMineral(rock: Rock) = Mineral(rock.name, rock.weight)

  implicit def appleTreeToVegetable(tree: AppleTree) = Vegetable(tree.name,  tree.height,  tree.weight)
  implicit def grassToVegetable(grass: Grass)        = Vegetable(grass.name, grass.height, grass.weight)

  implicit def bugToAnimal(bug: Bug)       = Animal(bug.name, bug.height, bug.weight)
  implicit def whaleToAnimal(whale: Whale) = Animal(whale.name, whale.height, whale.weight)

  implicit def animalToMatter[X <% Animal](animal: X)          = Matter(animal.name,    animal.weight)
  implicit def vegetableToMatter[X <% Vegetable](vegetable: X) = Matter(vegetable.name, vegetable.weight)
  implicit def mineralToMatter[X <% Mineral](mineral: X)       = Matter(mineral.name,   mineral.weight)

  //println(Animal("Poodle", 1.0, 8.0).megaJouleMsg)
  //println(AppleTree("Spartan", 2.3, 26.2, 12).megaJouleMsg)
  //println(Rock("Quartz crystal", "white", 2.3).megaJouleMsg)
}
