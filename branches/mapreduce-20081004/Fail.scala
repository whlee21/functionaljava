import fjs.test.Property._
import fjs.test.Arbitrary.{arbSChar, arbLocale}
import fj.test.CheckResult.summary
import fjs.F2._
import java.util.Locale

object S {
  def main(args: Array[String]) {
    val as = arbSChar > (String.valueOf(_))

    val p = prop((s: String, l: Locale) => (s toLowerCase) == (s toLowerCase l))
    summary println p(1000000, 0, 0, 100) // Falsified after 297861 passed tests with arguments: [Ì㑧ቤ睻冞尰,lt]
  }
}

