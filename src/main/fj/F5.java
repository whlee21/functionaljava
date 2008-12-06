package fj;

/**
 * A transformation function of arity-5 from <code>A</code>, <code>B</code>, <code>C</code>,
 * <code>D</code> and <code>E</code> to <code>F$</code>. This type can be represented using the Java
 * 7 closure syntax.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public abstract class F5<A, B, C, D, E, F$> implements F<A, F<B, F<C, F<D, F<E, F$>>>>> {
  /**
   * Transform <code>A</code>, <code>B</code>, <code>C</code>, <code>D</code> and <code>E</code> to
   * <code>F$</code>.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @param c The <code>C</code> to transform.
   * @param d The <code>D</code> to transform.
   * @param e The <code>E</code> to transform.
   * @return The result of the transformation.
   */
  public abstract F$ f(A a, B b, C c, D d, E e);

  private F5<A, B, C, D, E, F$> self = this;

  /**
   * Curries this function
   * @param a The parameter to the curried function
   * @return The curried function
   */
  public F4<B, C, D, E, F$> f(final A a) {
    return new F4<B, C, D, E, F$>() {
      public F$ f(final B b, final C c, final D d, final E e) {
        return self.f(a, b, c, d, e);
      }
    };
  }
}
