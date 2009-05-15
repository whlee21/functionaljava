import fj.F;
import fj.P1;
import static fj.data.Enumerator.naturalEnumerator;
import fj.data.Natural;
import static fj.data.Natural.ZERO;
import static fj.data.Natural.natural;
import fj.data.Stream;
import static fj.data.Stream.*;
import fj.data.vector.V2;
import static fj.pre.Ord.naturalOrd;
import static fj.pre.Show.naturalShow;
import static fj.pre.Show.unlineShow;

public class Primes {
  public static final Stream<Natural> primes = cons(natural(2).some(), new P1<Stream<Natural>>() {
    public Stream<Natural> _1() {
      return forever(naturalEnumerator, natural(3).some(), 2)
          .filter(new F<Natural, Boolean>() {
            public Boolean f(final Natural n) {return primeFactors(n).length() == 1;}
          });
    }
  });

  public static Stream<Natural> primeFactors(final Natural n) {return factor(n, natural(2).some(), primes.tail());}

  public static Stream<Natural> factor(final Natural n, final Natural p,
                                       final P1<Stream<Natural>> ps) {
    Stream<Natural> ns = cons(p, ps);
    Stream<Natural> ret = nil();
    while (ns.isNotEmpty() && ret.isEmpty()) {
      final Natural h = ns.head();
      final P1<Stream<Natural>> t = ns.tail();
      if (naturalOrd.isGreaterThan(h.multiply(h), n))
        ret = single(n);
      else {
        final V2<Natural> dm = n.divmod(h);
        if (naturalOrd.eq(dm._2(), ZERO))
          ret = cons(h, new P1<Stream<Natural>>() {
            public Stream<Natural> _1() {return factor(dm._1(), h, t);}
          });
        else ns = ns.tail()._1();
      }
    }
    return ret;
  }

  public static void main(final String[] a) {
    unlineShow(naturalShow).println(primes.takeWhile
        (naturalOrd.isLessThan(natural(Long.valueOf(a[0])).some())));
  }
}