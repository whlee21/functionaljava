package fj;

import static fj.FW.$;

/**
 * A wrapper for functions of arity 2, that decorates them with higher-order functions.
 */
public final class F2W<A, B, C> implements F2<A, B, C> {
  private final F2<A, B, C> f;

  private F2W(final F2<A, B, C> f) {
    this.f = f;
  }

  /**
   * Function application.
   *
   * @param a The <code>A</code> to transform.
   * @param b The <code>B</code> to transform.
   * @return The result of the transformation.
   */
  public C f(final A a, final B b) {
    return f.f(a, b);
  }

  /**
   * Wraps a given function, decorating it with higher-order functions.
   *
   * @param f The function to wrap.
   * @return The wrapped function.
   */
  public static <A, B, C> F2W<A, B, C> $$(final F2<A, B, C> f) {
    return new F2W<A, B, C>(f);
  }

  /**
   * Flips the arguments of this function.
   *
   * @return A new function with the arguments of this function flipped.
   */
  public F2W<B, A, C> flip() {
    return $$(Function.flip(this));
  }

  /**
   * Uncurries this function to a function on tuples.
   *
   * @return A new function that calls this function with the elements of a given tuple.
   */
  public FW<P2<A, B>, C> untuple() {
    return $(new F<P2<A, B>, C>() {
      public C f(final P2<A, B> p) {
        return F2W.this.f(p._1(), p._2());
      }
    });
  }
}
