import fj.P1;
import static fj.FW.$;
import static fj.data.Enumerator.naturalEnumerator;
import fj.data.Natural;
import static fj.data.Natural.ZERO;
import static fj.data.Natural.mod;
import static fj.data.Natural.natural;
import fj.data.Stream;
import static fj.data.Stream.*;
import static fj.pre.Ord.naturalOrd;
import fj.pre.Show;
import static fj.pre.Show.naturalShow;
import static fj.pre.Show.streamShow;

import java.math.BigInteger;

/**
 * Prints all primes less than n, then all non-primes.
 */
public class Primes2 {
  // Finds primes in a given stream.
  public static Stream<Natural> sieve(final Stream<Natural> xs) {
    return cons(xs.head(), new P1<Stream<Natural>>() {
      public Stream<Natural> _1() {
        return sieve(xs.tail()._1().filter($(naturalOrd.equal().eq(ZERO)).o(mod.f(xs.head()))));
      }
    });
  }

  // A stream of all primes less than n.
  public static Stream<Natural> primes(final Natural n) {
    return sieve(forever(naturalEnumerator, natural(2).some())).takeWhile(naturalOrd.isLessThan(n));
  }

  public static void main(final String[] a) {
    final Natural n = natural(new BigInteger(a[0])).some();
    final Show<Stream<Natural>> s = streamShow(naturalShow);

    s.println(primes(n));
    s.println(range(naturalEnumerator, ZERO, n).minus(naturalOrd.equal(), primes(n)));
  }
}
