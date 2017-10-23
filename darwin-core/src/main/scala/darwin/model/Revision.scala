package darwin.model

import scala.annotation.tailrec

sealed trait Revision

case class NumberedRevision(number: Int) extends Revision with Ordered[NumberedRevision] {
  override def compare(that: NumberedRevision): Int = number.compare(that.number)

  override def toString: String = s"NR($number)"
}

case class VersionedRevision(numbers: List[Int], descriptor: Option[String]) extends Revision with Ordered[VersionedRevision] {
  override def compare(that: VersionedRevision): Int = {
    val numbersCompare = VersionedRevision.compare(numbers, that.numbers)
    if (numbersCompare != 0) numbersCompare else (descriptor, that.descriptor) match {
      case (Some(a), Some(b)) => a.compare(b)
      case (Some(_), None) => 1
      case (None, Some(_)) => -1
      case (None, None) => 0
    }
  }

  override def toString: String = s"VR(${numbers.mkString(".")}${descriptor.getOrElse("")})"
}

object VersionedRevision {
  private val VersionFormat = """\d+(\.\d+)*""".r

  def apply(version: String): VersionedRevision = {
    val numbersString = VersionFormat.findPrefixOf(version)
    val numbers = numbersString.map(_.split("\\.").map(_.toInt).toList).getOrElse(Nil)
    val descriptor = version.drop(numbersString.map(_.length).getOrElse(0))
    new VersionedRevision(numbers, if (descriptor.isEmpty) None else Some(descriptor))
  }

  @tailrec
  private def compare(a: List[Int], b: List[Int]): Int = (a, b) match {
    case (g :: p, h :: q) =>
      val headCompare = g.compare(h)
      if (headCompare != 0) headCompare else compare(p, q)
    case (g :: p, Nil) => 1
    case (Nil, h :: q) => -1
    case (Nil, Nil) => 0
  }
}