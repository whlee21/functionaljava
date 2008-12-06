package fj;

/**
 * A transformation function of arity-2 from <code>A</code> and <code>B</code> to <code>C</code>.
 * This type can be represented using the Java 7 closure syntax.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public abstract class F2<A, B, C> implements F<A, F<B, C>> {
  /**
   * Transform <code>A</code> and <code>B</code> to <code>C</code>.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @return The result of the transformation.
   */
  public abstract C f(A a, B b);

  private F2<A, B, C> self = this;

  /**
   * Curries this function
   * @param a The parameter to the curried function
   * @return The curried function
   */
  public F<B, C> f(final A a) {
    return new F<B, C>() {
      public C f(final B b) {
        return self.f(a, b);
      }
    };
  }
}
