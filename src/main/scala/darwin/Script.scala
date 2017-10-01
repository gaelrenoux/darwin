package darwin

case class Script(
                   revision: Revision,
                   ups: Seq[String] = Seq.empty,
                   downs: Seq[String] = Seq.empty,
                   defines: Seq[(String, String)] = Seq.empty //Variable name then script
                 )
