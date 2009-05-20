package euler;

import fj.F;
import static fj.data.List.range;
import static fj.pre.Monoid.intAdditionMonoid;

import static java.lang.System.out;

/**
 * Add all the natural numbers below one thousand that are multiples of 3 or 5.
 */
public class Problem1 {
  public static void main(final String[] args) {
    out.println(intAdditionMonoid.sumLeft(range(0, 1000).filter(new F<Integer, Boolean>() {
      public Boolean f(final Integer a) {
        return a % 3 == 0 || a % 5 == 0;
      }
    })));
  }
}
