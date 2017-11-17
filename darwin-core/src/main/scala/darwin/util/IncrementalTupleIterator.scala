package darwin.util

import scala.annotation.tailrec

/**
  * This class counts on tuples of Int, increment a tuple starting with the leftmost element, reducing it to zero with
  * a leftover to the next if it reaches the limit for the current element. If there is no element left to apply the
  * leftover to, it will return None.
  *
  * Please note, the limit is not included. If the limit is 10, then the integers will only be go from 0 to 9.
  */
case class IncrementalTupleIterator private(limitPerPosition: Seq[Int], wrapped: Seq[Int]) {

  assert(!limitPerPosition.exists(_ <= 0))

  def next: Option[IncrementalTupleIterator] = {
    val (failed, newWrapped) =
      wrapped.zipWithIndex.foldLeft((true, Seq.empty[Int])) { case ((carrying, stored), (element, index)) =>
        if (!carrying) (false, stored :+ element)
        else if (element + 1 < limitPerPosition(index)) (false, stored :+ (element + 1))
        else (true, stored :+ 0)
      }
    if (failed) None else Some(new IncrementalTupleIterator(limitPerPosition, newWrapped))
  }

  def apply(i: Int): Int = wrapped(i)

  def toSeq: Seq[Seq[Int]] = IncrementalTupleIterator.loop(this)
}

object IncrementalTupleIterator {
  /** First element returned should be a tuple of zeros */
  def apply(limitPerPosition: Seq[Int]): IncrementalTupleIterator =
    new IncrementalTupleIterator(limitPerPosition, Seq.tabulate(limitPerPosition.size) { i => if (i == 0) -1 else 0 })

  @tailrec
  private def loop(previous: IncrementalTupleIterator, accumulator: Seq[Seq[Int]] = Seq()): Seq[Seq[Int]] =
    previous.next match {
      case None => accumulator
      case Some(it) => loop(it, accumulator :+ it.wrapped)
    }
}
