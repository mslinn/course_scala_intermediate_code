package collections {
  class ThingOrdered(val i: Int, val s: String) extends Ordered[ThingOrdered] {
    def compare(that: ThingOrdered) = {
      val primaryKey = this.i - that.i
      if (primaryKey != 0) primaryKey else this.s compare that.s
    }

    override def equals(other: Any) = {
      val that = other.asInstanceOf[ThingOrdered]
      this.i == that.i && this.s == that.s
    }

    override def hashCode = super.hashCode

    override def toString = s"ThingOrdered($i, $s)"
  }

  class ThingOrdering(val i: Int, val s: String) {
    override def toString = s"ThingOrdering($i, $s)"
  }

  object ThingOrdering {
    implicit val defaultThingOrdering: Ordering[ThingOrdering] = Ordering.by { thing: ThingOrdering =>
      (thing.i, thing.s)
    }
  }
}

package object collections {
  val thingOrdered1 = new ThingOrdered(1, "x")
  val thingOrdered2 = new ThingOrdered(1, "y")
  val thingOrdered3 = new ThingOrdered(33, "b")
  val thingOrdered4 = new ThingOrdered(4, "m")
  val thingOrdered5 = new ThingOrdered(4, "n")
  val thingsOrdered = Array(thingOrdered1, thingOrdered2, thingOrdered3, thingOrdered4, thingOrdered5)

  val thingOrdering1 = new ThingOrdering(1, "x")
  val thingOrdering2 = new ThingOrdering(1, "y")
  val thingOrdering3 = new ThingOrdering(33, "b")
  val thingOrdering4 = new ThingOrdering(4, "m")
  val thingOrdering5 = new ThingOrdering(4, "n")
  val thingOrderings = Array(thingOrdering1, thingOrdering2, thingOrdering3, thingOrdering4, thingOrdering5)

  val orderByI:        Ordering[ThingOrdering] = Ordering.by { _.i } // cannot import if defined in App
  val orderByS:        Ordering[ThingOrdering] = Ordering.by { _.s } // cannot import if defined in App
  val orderByIReverse: Ordering[ThingOrdering] = orderByI.reverse
  val orderBySReverse: Ordering[ThingOrdering] = orderByS.reverse
  val orderBySandI:    Ordering[ThingOrdering] = Ordering.by { x => (x.s, x.i) }
  val orderByIandS:    Ordering[ThingOrdering] = Ordering.by { x => (x.i, x.s) }
}
