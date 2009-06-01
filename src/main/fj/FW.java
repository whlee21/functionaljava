package fj;

import fj.data.*;
import static fj.data.Validation.either;
import static fj.F2W.$$;
import static fj.Function.*;
import static fj.P.p;
import fj.control.parallel.Actor;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;

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
    return $(P1.curry(f));
  }

  /**
   * Promotes this function to map over a product-1.
   *
   * @return This function promoted to map over a product-1.
   */
  public FW<P1<A>, P1<B>> mapP1() {
    return $(P1.<A, B>fmap(f));
  }

  /**
   * Promotes this function so that it returns its result in an Option. Kleisli arrow for Option.
   *
   * @return This function promoted to return its result in an Option.
   */
  public FW<A, Option<B>> option() {
    return $(Option.<B>some_()).o(f);
  }

  /**
   * Promotes this function to map over an optional value.
   *
   * @return This function promoted to map over an optional value.
   */
  public FW<Option<A>, Option<B>> mapOption() {
    return $(Option.<A, B>map().f(f));
  }

  /**
   * Promotes this function so that it returns its result in a List. Kleisli arrow for List.
   *
   * @return This function promoted to return its result in a List.
   */
  public FW<A, List<B>> list() {
    return $$(List.<B>cons()).flip().f(List.<B>nil()).o(f);
  }

  /**
   * Promotes this function to map over a List.
   *
   * @return This function promoted to map over a List.
   */
  public FW<List<A>, List<B>> mapList() {
    return $(List.<A, B>map_().f(f));
  }

  /**
   * Promotes this function so that it returns its result in a Stream. Kleisli arrow for Stream.
   *
   * @return This function promoted to return its result in a Stream.
   */
  public FW<A, Stream<B>> stream() {
    return $$(Stream.<B>cons()).flip().f(p(Stream.<B>nil())).o(f);
  }

  /**
   * Promotes this function to map over a Stream.
   *
   * @return This function promoted to map over a Stream.
   */
  public FW<Stream<A>, Stream<B>> mapStream() {
    return $(Stream.<A, B>map_().f(f));
  }

  /**
   * Promotes this function so that it returns its result in an Array. Kleisli arrow for Array.
   *
   * @return This function promoted to return its result in an Array.
   */
  public FW<A, Array<B>> array() {
    return $(new F<A, Array<B>>() {
      public Array<B> f(final A a) {
        return Array.single(f.f(a));
      }
    });
  }

  /**
   * Promotes this function to map over a Stream.
   *
   * @return This function promoted to map over a Stream.
   */
  public FW<Array<A>, Array<B>> mapArray() {
    return $(Array.<A, B>map().f(f));
  }

  /**
   * Returns a function that comaps over a given actor.
   *
   * @return A function that comaps over a given actor.
   */
  public FW<Actor<B>, Actor<A>> comapActor() {
    return $(new F<Actor<B>, Actor<A>>() {
      public Actor<A> f(final Actor<B> actor) {
        return actor.comap(f);
      }
    });
  }

  /**
   * Promotes this function to a concurrent function that returns a Promise of a value.
   *
   * @param s A parallel strategy for concurrent execution.
   * @return A concurrent function that returns a Promise of a value.
   */
  public FW<A, Promise<B>> promise(final Strategy<Unit> s) {
    return $(Promise.promise(s, f));
  }

  /**
   * Promotes this function to map over a Promise.
   *
   * @return This function promoted to map over Promises.
   */
  public FW<Promise<A>, Promise<B>> mapPromise() {
    return $(new F<Promise<A>, Promise<B>>() {
      public Promise<B> f(final Promise<A> promise) {
        return promise.fmap(f);
      }
    });
  }

  /**
   * Promotes this function so that it returns its result on the left side of an Either.
   * Kleisli arrow for the Either left projection.
   *
   * @return This function promoted to return its result on the left side of an Either.
   */
  public <C> FW<A, Either<B, C>> eitherLeft() {
    return $(Either.<B, C>left_()).o(f);
  }

  /**
   * Promotes this function so that it returns its result on the right side of an Either.
   * Kleisli arrow for the Either right projection.
   *
   * @return This function promoted to return its result on the right side of an Either.
   */
  public <C> FW<A, Either<C, B>> eitherRight() {
    return $(Either.<C, B>right_()).o(f);
  }


}
