object PartiallyApplliedUsing {
  def stringMunger(a: String)(f: String ⇒ String) = f(a)
  val abc = stringMunger("abc") _
  abc { x => x * 4 }
  abc { _ * 4 }
  //
  def twoStringMunger[T](a: String)(b: String)(f: (String, String) ⇒ T) = f(a, b)
  val twoStringToIntPF = twoStringMunger[Int]("abcdefghi")("def") _
  twoStringToIntPF { (s1, s2) ⇒ s1.indexOf(s2) }
  //
  def concatStringsAndFnValue(a: String)(b: String)(f: (String, String) ⇒ String) = a + b + f(a, b)
  val r3b = concatStringsAndFnValue("x")("y") { (u, v) ⇒ (u.length + v.length).toString }
  val xyHuh = concatStringsAndFnValue("x")("y") _
  def u2v3(u: String, v: String): String = (u.length * 2 + v.length * 3).toString
  xyHuh { u2v3 }
}
