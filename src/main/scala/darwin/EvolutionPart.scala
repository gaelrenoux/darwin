package darwin

import darwin.EvolutionPart.VariableToValues
import darwin.model.{Sql, Value, Variable}

/**
  * A single part in an evolution. Takes a map of variables to values, and produces the resulting SQL Scripts to execute.
  */
abstract class EvolutionPart {
  def f: Function[VariableToValues, Seq[Sql]]

  def apply(vtv: VariableToValues): Seq[Sql] = f(vtv)
}

case class EvolutionUp(f: Function[VariableToValues, Seq[Sql]]) extends EvolutionPart

case class EvolutionDown(f: Function[VariableToValues, Seq[Sql]]) extends EvolutionPart

case class EvolutionDefine(variable: Variable, f: Function[VariableToValues, Seq[Sql]]) extends EvolutionPart

object EvolutionPart {
  /** For each Variable, we may have several values (the script will be executed once per value). */
  type VariableToValues = Map[Variable, Seq[Value]]
}