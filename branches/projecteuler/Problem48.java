import static fj.data.List.asString;
import static fj.data.Stream.range;
import static fj.data.Enumerator.intEnumerator;
import fj.F;
import static fj.pre.Monoid.bigintAdditionMonoid;
import static fj.pre.Show.bigintShow;
import static java.math.BigInteger.valueOf;
import java.math.BigInteger;

public class Problem48 {
  public static void main(final String[] args) {
    final String t = asString(bigintShow.show(bigintAdditionMonoid.sumLeft(
        range(intEnumerator, 1, 1000).map(new F<Integer, BigInteger>() {
      public BigInteger f(final Integer i) {
        return valueOf(i).pow(i);
      }
    }).toList())).reverse().take(10).reverse());

    System.out.println(t);
  }
}
