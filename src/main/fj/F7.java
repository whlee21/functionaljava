package fj;

/**
 * A transformation function of arity-7 from <code>A</code>, <code>B</code>, <code>C</code>,
 * <code>D</code>, <code>E</code>, <code>F$</code> and <code>G</code> to <code>H</code>. This type
 * can be represented using the Java 7 closure syntax.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public abstract class F7<A, B, C, D, E, F$, G, H> implements F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> {
  /**
   * Transform <code>A</code>, <code>B</code>, <code>C</code>, <code>D</code>, <code>E</code>,
   * <code>F$</code> and <code>G</code> to <code>H</code>.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @param c The <code>C</code> to transform.
   * @param d The <code>D</code> to transform.
   * @param e The <code>E</code> to transform.
   * @param f The <code>F$</code> to transform.
   * @param g The <code>G</code> to transform.
   * @return The result of the transformation.
   */
  public abstract H f(A a, B b, C c, D d, E e, F$ f, G g);

  private F7<A, B, C, D, E, F$, G, H> self = this;

  /**
   * Curries this function
   *
   * @param a The parameter to the curried function
   * @return The curried function
   */
  public F6<B, C, D, E, F$, G, H> f(final A a) {
    return new F6<B, C, D, E, F$, G, H>() {
      public H f(final B b, final C c, final D d, final E e, final F$ f, final G g) {
        return self.f(a, b, c, d, e, f, g);
      }
    };
  }

}
