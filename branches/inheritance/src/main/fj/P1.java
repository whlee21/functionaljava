package fj;

/**
 * A product-1.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public abstract  class P1<A> {
  /**
   * Access the first element of the product.
   *
   * @return The first element of the product.
   */
  public abstract A _1();

  /**
   * Map the element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public <X> P1<X> map(final F<A, X> f) {
    return new P1<X>() {
      public X _1() {
        return f.f(P1.this._1());
      }
    };
  }

  /**
   * Returns a function that returns the first element of a product.
   *
   * @return A function that returns the first element of a product.
   */
  public static <A> F<P1<A>, A> __1() {
    return new F<P1<A>, A>() {
      public A f(final P1<A> p) {
        return p._1();
      }
    };
  }
}
