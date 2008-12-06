import fjs.test.Property._
import fjs.test.Arbitrary.{arbSByte, arbLocale}
import fj.test.CheckResult.summary
import fjs.F2._
import java.util.Locale

object Pass {
  def main(args: Array[String]) {
    val as = arbSByte > (b => String.valueOf(b.toChar))

    val p = prop((s: String, l: Locale) => (s toLowerCase) == (s toLowerCase l))
    summary println +p // OK, passed 100 tests.
  }
}

