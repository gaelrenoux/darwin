package darwin.util

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by gael on 26/09/17.
  */
class IncrementalTupleIteratorSpec extends FlatSpec with Matchers {

  behavior of "next"

  it should "initialize before the first position" in {
    val pos = IncrementalTupleIterator(Seq(7, 5, 42))
    pos.next should be (Some(IncrementalTupleIterator(Seq(7, 5, 42), Seq(0, 0, 0))))
  }

  it should "increment the first element if possible" in {
    val pos = IncrementalTupleIterator(Seq(7, 5, 42), Seq(1, 1, 40))
    pos.next should be (Some(IncrementalTupleIterator(Seq(7, 5, 42), Seq(2, 1, 40))))
  }

  it should "increment the first element to the limit" in {
    val pos = IncrementalTupleIterator(Seq(7, 5, 42), Seq(5, 1, 40))
    pos.next should be (Some(IncrementalTupleIterator(Seq(7, 5, 42), Seq(6, 1, 40))))
  }

  it should "increment the second element and reduce previous elements to 0 if the first element cannot be incremented" in {
    val pos = IncrementalTupleIterator(Seq(7, 5, 42), Seq(6, 1, 40))
    pos.next should be (Some(IncrementalTupleIterator(Seq(7, 5, 42), Seq(0, 2, 40))))
  }

  it should "increment the third element and reduce previous elements to 0 if the first and second element cannot be incremented" in {
    val pos = IncrementalTupleIterator(Seq(7, 5, 42), Seq(6, 4, 40))
    pos.next should be (Some(IncrementalTupleIterator(Seq(7, 5, 42), Seq(0, 0, 41))))
  }

  it should "increment correctly even with a count of 1" in {
    val pos = IncrementalTupleIterator(Seq(7, 1, 42), Seq(6, 0, 40))
    pos.next should be (Some(IncrementalTupleIterator(Seq(7, 1, 42), Seq(0, 0, 41))))
  }

  it should "return None if not element is possible" in {
    val pos = IncrementalTupleIterator(Seq(7, 5, 42), Seq(6, 4, 41))
    pos.next should be (None)
  }

  behavior of "toSeq"

  it should "convert the iterator to a list of all values" in {
    val pos = IncrementalTupleIterator(Seq(3, 4))
    pos.toSeq should be (Seq(
      Seq(0, 0),
      Seq(1, 0),
      Seq(2, 0),
      Seq(0, 1),
      Seq(1, 1),
      Seq(2, 1),
      Seq(0, 2),
      Seq(1, 2),
      Seq(2, 2),
      Seq(0, 3),
      Seq(1, 3),
      Seq(2, 3)
    ))
  }
}

