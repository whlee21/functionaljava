package fj.function;

import fj.F;
import fj.Function;
import fj.P1;
import fj.data.List;
import fj.data.Option;
import fj.pre.Monoid;

import static fj.Function.compose;

/**
 * The essence of the visitor design pattern expressed polymorphically.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public final class Visitor {
  /**
   * Returns the first value available in the given list of optional values. If none is found return the given default value.
   *
   * @param values The optional values to search.
   * @param def The default value if no value is found in the list.
   * @return The first value available in the given list of optional values. If none is found return the given default value.
   */
  public static <X> X findFirst(final List<Option<X>> values, final P1<X> def) {
    return Monoid.<X>firstOptionMonoid().sumLeft(values).orSome(def);
  }

  /**
   * Returns the first non-<code>null</code> value in the given list of optional values. If none is found return the given default value.
   *
   * @param values The potentially <code>null</code> values to search.
   * @param def The default value if no value is found in the list.
   * @return The first non-<code>null</code> value in the given list of optional values. If none is found return the given default value.
   */
  public static <X> X nullablefindFirst(final List<X> values, final P1<X> def) {
    return findFirst(values.map(Option.<X>fromNull()), def);
  }

  /**
   * Returns the first value found in the list of visitors after application of the given value, otherwise returns the
   * given default.
   *
   * @param visitors The list of visitors to apply.
   * @param def The default if none of the visitors yield a value.
   * @param value The value to apply to the visitors.
   * @return The first value found in the list of visitors after application of the given value, otherwise returns the
   * given default.
   */
  public static <A, B> B visitor(final List<F<A, Option<B>>> visitors, final P1<B> def, final A value) {
    return findFirst(visitors.map(Function.<A, Option<B>>apply(value)), def);
  }

  /**
   * Returns the first non-<code>null</code> value found in the list of visitors after application of the given value,
   * otherwise returns the given default.
   *
   * @param visitors The list of visitors to apply looking for a non-<code>null</code>.
   * @param def The default if none of the visitors yield a non-<code>null</code> value.
   * @param value The value to apply to the visitors.
   * @return The first value found in the list of visitors after application of the given value, otherwise returns the
   * given default.
   */
  public static <A, B> B nullableVisitor(final List<F<A, B>> visitors, final P1<B> def, final A value) {
    return visitor(visitors.map(new F<F<A, B>, F<A, Option<B>>>() {
      public F<A, Option<B>> f(final F<A, B> k) {
        return compose(Option.<B>fromNull(), k);
      }
    }), def, value);
  }
}
