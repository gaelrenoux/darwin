package darwin.util

/**
  * Created by gael on 22/10/17.
  */
object Collections {


  /**
    * Produces a Seq from a seed and a function. Copied from play.api.libs.Collections.
    */
  def unfoldLeft[A, B](seed: B)(f: B => Option[(B, A)]): Seq[A] = {
    def loop(seed: B)(ls: List[A]): List[A] = f(seed) match {
      case Some((b, a)) => loop(b)(a :: ls)
      case None => ls
    }

    loop(seed)(Nil)
  }


}
