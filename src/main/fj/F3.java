package fj;

/**
 * A transformation function of arity-3 from <code>A</code>, <code>B</code> and <code>C</code> to
 * <code>D</code>. This type can be represented using the Java 7 closure syntax.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public abstract class F3<A, B, C, D> implements F<A, F<B, F<C, D>>> {
  /**
   * Transform <code>A</code>, <code>B</code> and <code>C</code> to <code>D</code>.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @param c The <code>C</code> to transform.
   * @return The result of the transformation.
   */
  public abstract D f(A a, B b, C c);

  private F3<A, B, C, D> self = this;

  /**
   * Curries this function
   * @param a The parameter to the curried function
   * @return The curried function
   */
  public F2<B, C, D> f(final A a) {
    return new F2<B, C, D>() {
      public D f(final B b, final C c) {
        return self.f(a, b, c);
      }
    };
  }

}
