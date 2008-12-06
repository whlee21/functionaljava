import fj.data.Stream;
import fj.P1;
import fj.F2;
import static fj.function.Integers.even;
import static fj.pre.Ord.intOrd;
import static fj.pre.Monoid.intAdditionMonoid;
import static fj.data.Stream.cons;
import static fj.Function.curry;
import static java.lang.System.out;
 
public class Problem2 {
  public static void main(String[] args) {
    Stream<Integer> fibs = new F2<Integer, Integer, Stream<Integer>>() {
      public Stream<Integer> f(final Integer a, final Integer b) {
        return cons(a, P1.curry(curry(this).f(b)).f(a + b));
      }
    }.f(1, 2);
    out.println(intAdditionMonoid.sumLeft(fibs.takeWhile(intOrd.isLessThan(1000001)).filter(even).toList()));
  }
}

