import scala.collection.immutable

object StrInterp {
  implicit class MapLookup(val sc: StringContext) {
    val map: immutable.Map[String, Int] = Map(("a", 1), ("b", 2), ("c", 3)).withDefaultValue(0)

    def $(args: Any*): Int = {
      val orig = sc.s(args: _*)
      map(orig)
    }
  }
}
