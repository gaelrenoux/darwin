package darwin

import darwin.model.Revision

/**
  *
  *
  * Each function takes the values for all variables that have been calculated until now (multiple values per variable),
  * and returns all scrips that must be executed.
  */
case class Evolution(
                      revision: Revision,
                      parts: Seq[EvolutionPart]
                    )
