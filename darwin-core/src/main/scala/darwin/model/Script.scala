package darwin.model

/** A script, parsed from a file, corresponding to a specific version. */
case class Script(
              revision: Revision,
              parts: Seq[ScriptPart] = Seq()
            ) {

  /** Returns a new script, with the argument part added. */
  def :+(part: ScriptPart): Script = copy(parts = parts :+ part)
}