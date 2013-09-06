package attoparsec

import scalaz._
import Scalaz._

/** Text parsers. */
trait Text extends Combinators {

  /** Parser that returns a `Char` if it satisfies predicate `p`. */
  def elem(p: Char => Boolean, what: => String = "elem(...)"): Parser[Char] = 
    ensure(1) ~> get flatMap (s => {
      val c = s.charAt(0)
      if (p(c)) put(s.substring(1)) ~> ok(c)
      else err(what)
    }) asOpaque what

  /** Parser that skips a `Char` if it satisfies predicate `p`. */
  def skip(s: String, p: Char => Boolean, what: => String = "skip(...)"): Parser[Unit] = 
    ensure(1) ~> get flatMap (s => {
      if (p(s.charAt(0))) put(s.substring(1))
      else err(what)
    }) asOpaque what

  /** Parser that returns a `String` of length `n` if it satisfies predicate `p`. */
  def takeWith(n: Int, p: String => Boolean, what: => String = "takeWith(...)"): Parser[String] =
    ensure(n) ~> get flatMap (s => {
      val w = s.substring(0,n)
      if (p(w)) put(s.substring(n)) ~> ok(w)
      else err(what)
    }) asOpaque what

  /** Parser that returns the next `n` characters as a `String`. */
  def take(n: Int): Parser[String] = 
    takeWith(n, _ => true, "take(" + n + ")")

  /** Parser that matches and returns only `c`. */
  def char(c: Char): Parser[Char] = 
    elem(_==c, "'" + c.toString + "'")

  /** Parser that matches and returns only `s`. */
  def string(s: String): Parser[String] = 
    takeWith(s.length, _ == s, "\"" + s + "\"")

  def stringTransform(f: String => String, s: String, what: => String = "stringTransform(...)"): Parser[String] = 
    takeWith(s.length, f(_) == f(s), what)

  /** Parser that matches a char optionally, otherwise fails. */
  def optElem[A](p: Char => Option[A], what: => String = "optElem(...)"): Parser[A] = 
    ensure(1) ~> get flatMap { s => 
      p(s.head).cata(a => put(s.tail) ~> ok(a), err(what))
    } asOpaque what
 
  /** Parser for a decimal digit. */
  val digit: Parser[Digit] = 
    optElem(Digit.digitFromChar, "digit")

  /** Parser for a decimal number. */
  val long: Parser[Long] =
    many1(digit).map(Digit.longDigits(_)) as "long"

  /** Parser for a decimal number. */
  val int: Parser[Int] =
    long.map(_.toInt) as "int"

}