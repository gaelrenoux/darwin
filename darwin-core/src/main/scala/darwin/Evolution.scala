package darwin

import darwin.model.Revision

/** A representation of what needs to be executed for going to this revision from the the previous one. The details of
  * what to execute in each parts can depend on the result of execution in the previous parts. */
case class Evolution(
                      revision: Revision,
                      parts: Seq[EvolutionPart]
                    )
