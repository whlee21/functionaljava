package fj;

import static fj.FW.$;
import static fj.Function.curry;
import static fj.Function.uncurryF2;
import fj.control.parallel.Promise;
import fj.data.*;
import fj.pre.Ord;

/**
 * A wrapper for functions of arity 2, that decorates them with higher-order functions.
 */
public final class F2W<A, B, C> implements F2<A, B, C>, F<A, F<B, C>> {
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
   * Partial application.
   *
   * @param a The <code>A</code> to which to apply this function.
   * @return The function partially applied to the given argument.
   */
  public FW<B, C> f(final A a) {
    return $(curry(f).f(a));
  }

  /**
   * Curries this wrapped function to a wrapped function of arity-1 that returns another wrapped function.
   *
   * @return a wrapped function of arity-1 that returns another wrapped function.
   */
  public FW<A, F<B, C>> curryW() {
    return $(new F<A, F<B, C>>() {
      public F<B, C> f(final A a) {
        return F2W.this.f(a);
      }
    });
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
   * Wraps a given function, decorating it with higher-order functions.
   *
   * @param f The function to wrap.
   * @return The wrapped function.
   */
  public static <A, B, C> F2W<A, B, C> $$(final F<A, F<B, C>> f) {
    return $$(uncurryF2(f));
  }

  /**
   * Flips the arguments of this function.
   *
   * @return A new function with the arguments of this function flipped.
   */
  public F2W<B, A, C> flip() {
    return $$(Function.flip(f));
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

  /**
   * Promotes this function to a function on Arrays.
   *
   * @return This function promoted to transform Arrays.
   */
  public F2W<Array<A>, Array<B>, Array<C>> array() {
    return $$(new F2<Array<A>, Array<B>, Array<C>>() {
      public Array<C> f(final Array<A> a1, final Array<B> a2) {
        return a2.apply(curryW().mapArray().f(a1));
      }
    });
  }

  /**
   * Promotes this function to a function on Promises.
   *
   * @return This function promoted to transform Promises.
   */
  public F2W<Promise<A>, Promise<B>, Promise<C>> promise() {
    return $$(Promise.<A, B, C>liftM2(this));
  }

  /**
   * Promotes this function to a function on Iterables.
   *
   * @return This function promoted to transform Iterables.
   */
  public F2W<Iterable<A>, Iterable<B>, IterableW<C>> iterable() {
    return $$(IterableW.liftM2(this));
  }

  /**
   * Promotes this function to a function on Lists.
   *
   * @return This function promoted to transform Lists.
   */
  public F2W<List<A>, List<B>, List<C>> list() {
    return $$(List.liftM2(this));
  }

  /**
   * Promotes this function to a function on non-empty lists.
   *
   * @return This function promoted to transform non-empty lists.
   */
  public F2W<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>> nel() {
    return $$(new F2<NonEmptyList<A>, NonEmptyList<B>, NonEmptyList<C>>() {
      public NonEmptyList<C> f(final NonEmptyList<A> as, final NonEmptyList<B> bs) {
        return NonEmptyList.fromList(as.toList().bind(bs.toList(), F2W.this)).some();
      }
    });
  }

  /**
   * Promotes this function to a function on Options.
   *
   * @return This function promoted to transform Options.
   */
  public F2W<Option<A>, Option<B>, Option<C>> option() {
    return $$(Option.liftM2(this));
  }

  /**
   * Promotes this function to a function on Sets.
   *
   * @param o An ordering for the result of the promoted function.
   * @return This function promoted to transform Sets.
   */
  public F2W<Set<A>, Set<B>, Set<C>> set(final Ord<C> o) {
    return $$(new F2<Set<A>, Set<B>, Set<C>>() {
      public Set<C> f(final Set<A> as, final Set<B> bs) {
        Set<C> cs = Set.empty(o);
        for (final A a : as)
          for (final B b : bs)
            cs = cs.insert(F2W.this.f(a, b));
        return cs;
      }
    });
  }

  /**
   * Promotes this function to a function on Streams.
   *
   * @return This function promoted to transform Streams.
   */
  public F2W<Stream<A>, Stream<B>, Stream<C>> stream() {
    return $$(new F2<Stream<A>, Stream<B>, Stream<C>>() {
      public Stream<C> f(final Stream<A> as, final Stream<B> bs) {
        return as.bind(bs, F2W.this);
      }
    });
  }

}
