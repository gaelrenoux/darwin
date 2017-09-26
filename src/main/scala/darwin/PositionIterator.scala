package darwin

/**
  * Created by gael on 26/09/17.
  */
case class PositionIterator(variableValuesCount: Seq[Int], wrapped: Seq[Int]) {

  def next: Option[PositionIterator] = {
    val (failed, newWrapped) =
      wrapped.zipWithIndex.foldLeft((true, Seq.empty[Int])) { case ((carrying, stored), (element, index)) =>
        if (!carrying) (false, stored :+ element)
        else if (element + 1 < variableValuesCount(index)) (false, stored :+ (element + 1))
        else (carrying, stored :+ 0)
      }
    if (failed) None else Some(new PositionIterator(variableValuesCount, newWrapped))
  }

  def apply(i: Int): Int = wrapped(i)

}

object PositionIterator {
  def apply(variableValuesCount: Seq[Int]): PositionIterator =
    new PositionIterator(variableValuesCount, Seq.tabulate(variableValuesCount.size) { i => if (i == 0) -1 else 0 })
}
