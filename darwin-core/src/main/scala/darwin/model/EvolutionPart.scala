package darwin.model

/** A single part in an evolution. Takes a map of variables to values, and produces the resulting SQL Scripts to
  * execute.
  *
  * For each Variable, we may have several values. We will have one SQL script per possible combination in the methods
  * output. */
abstract class EvolutionPart {
  def f: Function[Map[Variable, Seq[Value]], Seq[Sql]]

  def apply(vtv: Map[Variable, Seq[Value]]): Seq[Sql] = f(vtv)
}

case class EvolutionUp(f: Function[Map[Variable, Seq[Value]], Seq[Sql]]) extends EvolutionPart

case class EvolutionDown(f: Function[Map[Variable, Seq[Value]], Seq[Sql]]) extends EvolutionPart

case class EvolutionDefine(variable: Variable, f: Function[Map[Variable, Seq[Value]], Seq[Sql]]) extends EvolutionPart
