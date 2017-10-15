package darwin

import darwin.model.{Sql, Value, Variable}

/**
  * An yet unparsed element in a script: either an up, a down or a define
  */
abstract class ScriptPart {
  /** Order within the script */
  val order: Int

  /** Content of the part */
  val content: String
}

case class ScriptUp(order: Int, content: String) extends ScriptPart

case class ScriptDown(order: Int, content: String) extends ScriptPart

case class ScriptDefine(order: Int, variable: Variable, content: String) extends ScriptPart