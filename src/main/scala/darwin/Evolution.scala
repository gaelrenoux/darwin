package darwin

import darwin.Evolution.Values

/**
  * Each function takes the values for all variables that have been calculated until now (multiple values per variable),
  * and returns all scrips that must be executed.
  */
case class Evolution(
                      revision: Revision,
                      defines: Seq[(String, Function[Values, Seq[String]])],
                      ups: Seq[Function[Values, Seq[String]]],
                      downs: Seq[Function[Values, Seq[String]]]
                    )

object Evolution {
  /* For each variable, the values associated */
  type Values = Map[String, Seq[String]]
}
