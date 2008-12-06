package fj;

/**
 * A product-2.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public abstract class P2<A, B> {
  /**
   * Access the first element of the product.
   *
   * @return The first element of the product.
   */
  public abstract A _1();

  /**
   * Access the second element of the product.
   *
   * @return The second element of the product.
   */
  public abstract B _2();

  /**
   * Map the first element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public <X> P2<X, B> map1(final F<A, X> f) {
    return new P2<X, B>() {
      public X _1() {
        return f.f(P2.this._1());
      }

      public B _2() {
        return P2.this._2();
      }
    };
  }

  /**
   * Map the second element of the product.
   *
   * @param f The function to map with.
   * @return A product with the given function applied.
   */
  public <X> P2<A, X> map2(final F<B, X> f) {
    return new P2<A, X>() {
      public A _1() {
        return P2.this._1();
      }

      public X _2() {
        return f.f(P2.this._2());
      }
    };
  }

  /**
   * Returns a function that returns the first element of a product.
   *
   * @return A function that returns the first element of a product.
   */
  public static <A, B> F<P2<A, B>, A> __1() {
    return new F<P2<A, B>, A>() {
      public A f(final P2<A, B> p) {
        return p._1();
      }
    };
  }

  /**
   * Returns a function that returns the second element of a product.
   *
   * @return A function that returns the second element of a product.
   */
  public static <A, B> F<P2<A, B>, B> __2() {
    return new F<P2<A, B>, B>() {
      public B f(final P2<A, B> p) {
        return p._2();
      }
    };
  }
}
