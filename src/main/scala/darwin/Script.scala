package darwin

import darwin.model.Revision

/**
  * Created by gael on 15/10/17.
  */
case class Script(
              revision: Revision,
              parts: Seq[ScriptPart] = Seq()
            ) {

  def :+(part: ScriptPart): Script = copy(parts = parts :+ part)
}