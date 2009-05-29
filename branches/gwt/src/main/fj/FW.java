package fj;

import static fj.Function.compose;
import fj.data.Option;

/**
 * A wrapper for functions of arity 1, that decorates them with higher-order functions.
 */
public final class FW<A, B> implements F<A, B> {
  private final F<A, B> f;

  private FW(final F<A, B> f) {
    this.f = f;
  }

  /**
   * Wraps the given function, decorating it with higher-order functions.
   *
   * @param f A function to wrap.
   * @return The given function, wrapped.
   */
  public static <A, B> FW<A, B> $(final F<A, B> f) {
    return new FW<A, B>(f);
  }

  /**
   * Function application
   *
   * @param a The <code>A</code> to transform.
   * @return The result of the transformation.
   */
  public B f(final A a) {
    return f.f(a);
  }

  /**
   * Function composition
   *
   * @param g A function to compose with this one.
   * @return The composed function such that this function is applied last.
   */
  public <C> FW<C, B> o(final F<C, A> g) {
    return $(compose(f, g));
  }

  /**
   * First-class function composition
   *
   * @return A function that composes this function with another.
   */
  public <C> FW<F<C, A>, F<C, B>> o_() {
    return $(new F<F<C, A>, F<C, B>>() {
      public F<C, B> f(final F<C, A> g) {
        return $(o(g));
      }
    });
  }

  /**
   * Function composition flipped.
   *
   * @param g A function with which to compose this one.
   * @return The composed function such that this function is applied first.
   */
  public <C> FW<A, C> andThen(final F<B, C> g) {
    return $(compose(g, this));
  }

  /**
   * First-class composition flipped.
   *
   * @return A function that invokes this function and then a given function on the result.
   */
  public <C> FW<F<B, C>, F<A, C>> andThen_() {
    return $(new F<F<B, C>, F<A, C>>() {
      public F<A, C> f(final F<B, C> g) {
        return andThen(g);
      }
    });
  }

  /**
   * Simultaneously covaries and contravaries a function.
   *
   * @return A co- and contravariant function that invokes this function on its argument.
   */
  public FW<? super A, ? extends B> vary() {
    return $(Function.vary(this));
  }

  /**
   * Binds a given function across this function (Reader Monad).
   *
   * @param g A function that takes the return value of this function as an argument, yielding a new function.
   * @return A function that invokes this function on its argument and then the given function on the result.
   */
  public <C> FW<A, C> bind(final F<B, F<A, C>> g) {
    return $(Function.bind(this, g));
  }

  /**
   * First-class function binding.
   *
   * @return A function that binds another function across this function.
   */
  public <C> FW<F<B, F<A, C>>, F<A, C>> bind() {
    return $(new F<F<B, F<A, C>>, F<A, C>>() {
      public F<A, C> f(final F<B, F<A, C>> bff) {
        return bind(bff);
      }
    });
  }

  /**
   * Function application in an environment (Applicative Functor).
   *
   * @param g A function with the same argument type as this function, yielding a function that takes the return
   *          value of this function.
   * @return A new function that invokes the given function on its argument, yielding a new function that is then
   *         applied to the result of applying this function to the argument.
   */
  public <C> FW<A, C> apply(final F<A, F<B, C>> g) {
    return $(Function.apply(g, this));
  }

  /**
   * First-class function application in an environment.
   *
   * @return A function that applies a given function within the environment of this function.
   */
  public <C> FW<F<A, F<B, C>>, F<A, C>> apply() {
    return $(new F<F<A, F<B, C>>, F<A, C>>() {
      public F<A, C> f(final F<A, F<B, C>> aff) {
        return apply(aff);
      }
    });
  }

  /**
   * Applies this function over the arguments of another function.
   *
   * @param g The function over whose arguments to apply this function.
   * @return A new function that invokes this function on its arguments before invoking the given function.
   */
  public <C> FW<A, F<A, C>> on(final F<B, F<B, C>> g) {
    return $(Function.on(g, this));
  }

  /**
   * First-class composition with a function of arity-2
   *
   * @return A function that applies this function over the arguments of a given function.
   */
  public <C> FW<F<B, F<B, C>>, F<A, F<A, C>>> on() {
    return $(new F<F<B, F<B, C>>, F<A, F<A, C>>>() {
      public F<A, F<A, C>> f(final F<B, F<B, C>> bff) {
        return on(bff);
      }
    });
  }

  /**
   * Promotes this function so that it returns its result in a product-1. Kleisli arrow for P1.
   *
   * @return This function promoted to return its result in a product-1.
   */
  public FW<A, P1<B>> lazy() {
    return $(P1.curry(this));
  }

  /**
   * Promotes this function so that it returns its result in an Option. Kleisli arrow for Option.
   *
   * @return This function promoted to return its result in an Option.
   */
  @SuppressWarnings("unchecked")
  public FW<A, Option<B>> option() {
    return $(Option.<B>some_()).o(f);
  }
}
