package darwin.model

/** En element in a script: either an up, a down or a define. The content has not been parsed yet. */
abstract class ScriptPart {
  /** Order within the script */
  val order: Int

  /** Content of the part */
  val content: String
}

case class ScriptUp(order: Int, content: String) extends ScriptPart

case class ScriptDown(order: Int, content: String) extends ScriptPart

case class ScriptDefine(order: Int, variable: Variable, content: String) extends ScriptPart