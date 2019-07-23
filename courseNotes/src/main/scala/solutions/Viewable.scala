package solutions

import scala.language.implicitConversions

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

  implicit def sandToMineral(sand: Sand): Mineral = Mineral(sand.name, sand.weight)
  implicit def rockToMineral(rock: Rock): Mineral = Mineral(rock.name, rock.weight)

  implicit def appleTreeToVegetable(tree: AppleTree): Vegetable = Vegetable(tree.name,  tree.height,  tree.weight)
  implicit def grassToVegetable(grass: Grass): Vegetable = Vegetable(grass.name, grass.height, grass.weight)

  implicit def bugToAnimal(bug: Bug): Animal = Animal(bug.name, bug.height, bug.weight)
  implicit def whaleToAnimal(whale: Whale): Animal = Animal(whale.name, whale.height, whale.weight)

  // View bounds are deprecated, so using implicit parameter instead
  implicit def animalToMatter[X](animal: X)
                                (implicit ev: X => Animal): Matter = Matter(animal.name,    animal.weight)

  implicit def vegetableToMatter[X](vegetable: X)
                                   (implicit ev: X => Vegetable): Matter = Matter(vegetable.name, vegetable.weight)

  implicit def mineralToMatter[X](mineral: X)
                                 (implicit $1: X => Mineral): Matter = Matter(mineral.name,   mineral.weight)

  //println(Animal("Poodle", 1.0, 8.0).megaJouleMsg)
  //println(AppleTree("Spartan", 2.3, 26.2, 12).megaJouleMsg)
  //println(Rock("Quartz crystal", "white", 2.3).megaJouleMsg)
}
