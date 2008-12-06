package fj;

/**
 * A transformation function of arity-8 from <code>A</code>, <code>B</code>, <code>C</code>,
 * <code>D</code>, <code>E</code>, <code>F$</code>, <code>G</code> and <code>H</code> to
 * <code>I</code>. This type can be represented using the Java 7 closure syntax.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public abstract class F8<A, B, C, D, E, F$, G, H, I> implements F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> {
  /**
   * Transform <code>A</code>, <code>B</code>, <code>C</code>, <code>D</code>, <code>E</code>,
   * <code>F$</code>, <code>G</code> and <code>H</code> to <code>I</code>.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @param c The <code>C</code> to transform.
   * @param d The <code>D</code> to transform.
   * @param e The <code>E</code> to transform.
   * @param f The <code>F$</code> to transform.
   * @param g The <code>G</code> to transform.
   * @param h The <code>H</code> to transform.
   * @return The result of the transformation.
   */
  public abstract I f(A a, B b, C c, D d, E e, F$ f, G g, H h);

  private F8<A, B, C, D, E, F$, G, H, I> self = this;

  /**
   * Curries this function
   *
   * @param a The parameter to the curried function
   * @return The curried function
   */
  public F7<B, C, D, E, F$, G, H, I> f(final A a) {
    return new F7<B, C, D, E, F$, G, H, I>() {
      public I f(final B b, final C c, final D d, final E e, final F$ f, final G g, final H h) {
        return self.f(a, b, c, d, e, f, g, h);
      }
    };
  }
}
