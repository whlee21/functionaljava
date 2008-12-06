package fj;

/**
 * A transformation function of arity-6 from <code>A</code>, <code>B</code>, <code>C</code>,
 * <code>D</code>, <code>E</code> and <code>F$</code> to <code>G</code>. This type can be
 * represented using the Java 7 closure syntax.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public abstract class F6<A, B, C, D, E, F$, G> implements F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> {
  /**
   * Transform <code>A</code>, <code>B</code>, <code>C</code>, <code>D</code>, <code>E</code> and
   * <code>F$</code> to <code>G</code>.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @param c The <code>C</code> to transform.
   * @param d The <code>D</code> to transform.
   * @param e The <code>E</code> to transform.
   * @param f The <code>F$</code> to transform.
   * @return The result of the transformation.
   */
  public abstract G f(A a, B b, C c, D d, E e, F$ f);

  private F6<A, B, C, D, E, F$, G> self = this;

  /**
   * Curries this function
   * @param a The parameter to the curried function
   * @return The curried function
   */
  public F5<B, C, D, E, F$, G> f(final A a) {
    return new F5<B, C, D, E, F$, G>() {
      public G f(final B b, final C c, final D d, final E e, final F$ f) {
        return self.f(a, b, c, d, e, f);
      }
    };
  }
}
