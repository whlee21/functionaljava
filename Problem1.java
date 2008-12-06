import static fj.pre.Monoid.intAdditionMonoid;
import static fj.data.List.range;
import fj.F;

public class Problem1 {
  public static void main(final String[] args) {
    final int problem1 = intAdditionMonoid.sumLeft(range(0, 1000).filter(new F<Integer, Boolean>() {
      public Boolean f(final Integer a) {
        return a % 3 == 0 || a % 5 == 0;
      }
    }));

    System.out.println(problem1);
  }
}
