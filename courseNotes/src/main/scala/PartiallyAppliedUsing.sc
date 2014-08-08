object PartiallyApplliedUsing {
  def stringMunger(a: String)(f: String ⇒ String) = f(a)
  val abc = stringMunger("abc") _
  abc { x => x * 4 }
  abc { _ * 4 }
  //
  def repeat3(a: String)(b: String)(f: (String, String) ⇒ String) = f(a, b)
  val abcdef = repeat3("abc")("def") _
  abcdef { (s1, s2) ⇒ s1 + s2 }
  def repeat3b(a: String)(b: String)(f: (String, String) ⇒ String) = a + b + f(a, b)
  val r3b = repeat3b("x")("y") _
  r3b { (u, v) ⇒ (u.length + v.length).toString }
  //

}
