package fj.pre;

import fj.F;
import fj.data.List;

/**
 * A monoid abstraction to be defined across types of the given type argument. Implementations must
 * follow the monoidal laws:
 * <ul>
 * <li><em>Left Identity</em>; forall x. sum(zero(), x) == x</li>
 * <li><em>Right Identity</em>; forall x. sum(x, zero()) == x</li>
 * <li><em>Associativity</em>; forall x. forall y. forall z. sum(sum(x, y), z) == sum(x, sum(y, z))</li>
 * </ul>
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public final class Monoid<A> {
  private final F<A, F<A, A>> sum;
  private final A zero;

  private Monoid(final F<A, F<A, A>> sum, final A zero) {
    this.sum = sum;
    this.zero = zero;
  }

  /**
   * Sums the two given arguments.
   *
   * @param a1 A value to sum with another.
   * @param a2 A value to sum with another.
   * @return The of the two given arguments.
   */
  public A sum(final A a1, final A a2) {
    return sum.f(a1).f(a2);
  }

  /**
   * The zero value for this monoid.
   *
   * @return The zero value for this monoid.
   */
  public A zero() {
    return zero;
  }

  /**
   * Sums the given values with right-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumRight(final List<A> as) {
    return as.foldRight(sum, zero);
  }

  /**
   * Sums the given values with left-fold.
   *
   * @param as The values to sum.
   * @return The sum of the given values.
   */
  public A sumLeft(final List<A> as) {
    return as.foldLeft(sum, zero);
  }

  /**
   * Constructs a monoid from the given sum function and zero value, which must follow the monoidal
   * laws.
   * 
   * @param sum The sum function for the monoid.
   * @param zero The zero for the monoid.
   * @return A monoid instance that uses the given sun function and zero value.
   */
  public static <A> Monoid<A> monoid(final F<A, F<A, A>> sum, final A zero) {
    return new Monoid<A>(sum, zero);
  }

  /**
   * A monoid that adds integers.
   */
  public static final Monoid<Integer> intPlusMonoid = new Monoid<Integer>(new F<Integer, F<Integer, Integer>>() {
      public F<Integer, Integer> f(final Integer i1) {
        return new F<Integer, Integer>() {
          public Integer f(final Integer i2) {
            return i1 + i2;
          }
        };
      }
    }, 0);
}
