import static fj.FW.$;
import fj.P1;
import static fj.data.Enumerator.naturalEnumerator;

import fj.Show2;
import fj.data.Natural;
import static fj.data.Natural.*;
import fj.data.Stream;
import static fj.data.Stream.*;
import static fj.Ord.naturalOrd;
import static fj.Show2.naturalShow;
import static fj.Show2.streamShow;

import java.math.BigInteger;

/**
 * Prints all primes less than n
 */
public class Primes2 {
  // Finds primes in a given stream.
  public static Stream<Natural> sieve(final Stream<Natural> xs) {
    return cons(xs.head(), new P1<Stream<Natural>>() {
      public Stream<Natural> _1() {
        return sieve(xs.tail()._1().removeAll($(naturalOrd.equal().eq(ZERO)).o(mod.f(xs.head()))));
      }
    });
  }

  // A stream of all primes less than n.
  public static Stream<Natural> primes(final Natural n) {
    return sieve(forever(naturalEnumerator, natural(2).some())).takeWhile(naturalOrd.isLessThan(n));
  }

  public static void main(final String[] a) {
    final Natural n = natural(new BigInteger(a[0])).some();
    final Show2<Stream<Natural>> s = streamShow(naturalShow);

    s.println(primes(n));
  }
}
