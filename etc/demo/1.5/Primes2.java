import fj.data.Natural;
import fj.data.Stream;
import static fj.data.Stream.*;
import static fj.pre.Ord.naturalOrd;
import fj.pre.Show;
import static fj.pre.Show.streamShow;
import static fj.pre.Show.naturalShow;
import static fj.data.Natural.ZERO;
import static fj.data.Natural.natural;
import fj.P1;
import fj.F;
import static fj.data.Enumerator.naturalEnumerator;

public class Primes2
  {public static Stream<Natural> sieve(final Stream<Natural> xs)
    {return cons(xs.head(), new P1<Stream<Natural>>()
      {public Stream<Natural> _1()
        {return sieve(xs.tail()._1().bind(new F<Natural, Stream<Natural>>()
          {public Stream<Natural> f(final Natural x)
            {return naturalOrd.eq(x.mod(xs.head()), ZERO) ?
                    single(x) : Stream.<Natural>nil();}}));}});}

  public static Stream<Natural> primes(final Natural n)
    {return sieve(forever(naturalEnumerator, natural(2).some()))
            .takeWhile(naturalOrd.isLessThan(n));}

  public static void main(final String[] a)
    {final Stream<Natural> primes =
        primes(natural(Long.valueOf(a[0])).some());
     final Show<Stream<Natural>> s = streamShow(naturalShow);
     s.println(primes);
     s.println(forever(naturalEnumerator, natural(0).some())
               .minus(naturalOrd.equal(), primes));}
}
