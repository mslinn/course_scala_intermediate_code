implicit class MapLookup(val sc: StringContext) {
  val map = Map(("a", 1), ("b", 2), ("c", 3)).withDefaultValue(0)
   
  def $(args: Any*): Int = {
    val orig = sc.s(args: _*)
    map.get(orig)
  }
}
