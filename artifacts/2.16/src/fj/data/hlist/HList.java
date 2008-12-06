package fj.data.hlist;

import fj.F;
import fj.F2;
import fj.F3;
import fj.P;
import fj.P2;
import fj.Unit;
import static fj.Function.compose;

/**
 * Type-safe heterogeneous lists.
 *
 * @param <A> The specific type of the list, as a subtype of HList
 */
public abstract class HList<A extends HList<A>> {

  /**
   * Extends (cons) this list by prepending the given element, returning a new list.
   *
   * @param e an element to prepend to this list.
   * @return a new heterogeneous list, consisting of the given element prepended to this list.
   */
  public abstract <E> HCons<E, A> extend(E e);

  /**
   * The empty list
   */
  public static final class HNil extends HList<HNil> {
    private HNil() {
    }

    public <E> HCons<E, HNil> extend(final E e) {
      return cons(e, this);
    }

  }

  /**
   * The nonempty list
   */
  public static final class HCons<E, L extends HList<L>> extends HList<HCons<E, L>> {
    private E e;
    private L l;

    private HCons(final E e, final L l) {
      this.e = e;
      this.l = l;
    }

    public E head() {
      return e;
    }

    public L tail() {
      return l;
    }

    public <X> HCons<X, HCons<E, L>> extend(final X e) {
      return cons(e, this);
    }
  }

  private static final HNil nil = new HNil();

  /**
   * Returns the empty list.
   *
   * @return the empty list.
   */
  public static HNil nil() {
    return nil;
  }

  /**
   * Returns a heterogeneous list consisting of an element and another list.
   *
   * @param e an element to put in a list.
   * @param l the rest of the list.
   * @return a heterogeneous list consisting of an element and another list.
   */
  public static <E, L extends HList<L>> HCons<E, L> cons(final E e, final L l) {
    return new HCons<E, L>(e, l);
  }

  /**
   * Returns a heterogeneous list consisting of a single element.
   *
   * @param e an element to put in a list
   * @return a heterogeneous list consisting of a single element.
   */
  public static <E> HCons<E, HNil> single(final E e) {
    return cons(e, nil());
  }

  /**
   * The concatenation of two heterogeneous lists.
   *
   * @param <L> The type of the first list.
   * @param <LL> The type of the second list.
   * @param <LLL> The type of the combined list.
   */
  public static final class HAppend<L, LL, LLL> {
    private final F2<L, LL, LLL> append;

    private HAppend(final F2<L, LL, LLL> f) {
      append = f;
    }

    /**
     * Append a given heterogeneous list to another.
     *
     * @param l1 a heterogeneous list to be appended to.
     * @param l2 a heterogeneous list to append to another.
     * @return a new heterogeneous list consisting of the second argument appended to the first.
     */
    public LLL append(final L l1, final LL l2) {
      return append.f(l1, l2);
    }

    /**
     * Returns a method for concatenating lists to the empty list.
     *
     * @return a method for concatenating lists to the empty list.
     */
    public static <L extends HList<L>> HAppend<HNil, L, L> append() {
      return new HAppend<HNil, L, L>(new F2<HNil, L, L>() {
        public L f(final HNil hNil, final L l) {
          return l;
        }
      });
    }

    /**
     * Returns a method for appending lists to a nonempty heterogeneous list.
     *
     * @param h a method for appending lists to the tail of the given nonempty list.
     * @return a method for appending lists to a nonempty heterogeneous list.
     */
    public static <X, L extends HList<L>, LL, LLL extends HList<LLL>, H extends HAppend<L, LL, LLL>>
    HAppend<HCons<X, L>, LL, HCons<X, LLL>> append(final H h) {
      return new HAppend<HCons<X, L>, LL, HCons<X, LLL>>(new F2<HCons<X, L>, LL, HCons<X, LLL>>() {
        public HCons<X, LLL> f(final HCons<X, L> c, final LL l) {
          return cons(c.head(), h.append(c.tail(), l));
        }
      });
    }
  }

  /**
   * Type-level function application operators.
   *
   * @param <F$> The type of the function to apply.
   * @param <A> The domain of the function.
   * @param <R> The function's codomain.
   */
  public abstract static class Apply<F$, A, R> {
    public abstract R apply(F$ f, A a);

    /**
     * Function application operator.
     *
     * @return an operator that applies a given function to a given argument.
     */
    public static <X, Y> Apply<F<X, Y>, X, Y> f() {
      return new Apply<F<X, Y>, X, Y>() {
        public Y apply(final F<X, Y> f, final X x) {
          return f.f(x);
        }
      };
    }

    /**
     * Identity operator
     *
     * @return An operator that returns its second argument no matter which function is being applied.
     */
    public static <X> Apply<Unit, X, X> id() {
      return new Apply<Unit, X, X>() {
        public X apply(final Unit f, final X x) {
          return x;
        }
      };
    }

    /**
     * A function application operator for function composition.
     *
     * @param <X> The domain.
     * @param <Y> The type through which to compose.
     * @param <Z> The codomain.
     * @return an operator that composes functions.
     */
    public static <X, Y, Z> Apply<Unit, P2<F<X, Y>, F<Y, Z>>, F<X, Z>> comp() {
      return new Apply<Unit, P2<F<X, Y>, F<Y, Z>>, F<X, Z>>() {
        public F<X, Z> apply(final Unit f, final P2<F<X, Y>, F<Y, Z>> fs) {
          return compose(fs._2(), fs._1());
        }
      };
    }
  }

  /**
   * The catamorphism over heterogeneous lists.
   *
   * @param <F> The type of the function with which to fold.
   * @param <V> The type of the value to be substituted for the empty list.
   * @param <L> The type of the heterogeneous list to be folded.
   * @param <R> The return type of the fold.
   */
  public static class HFoldr<F, V, L, R> {

    private final F3<F, V, L, R> foldRight;

    private HFoldr(final F3<F, V, L, R> foldRight) {
      this.foldRight = foldRight;
    }

    /**
     * A fold instance for the empty list.
     *
     * @param <F$> The type of the function with which to fold.
     * @param <V>  The type of value that this fold returns.
     * @return a fold instance for the empty list.
     */
    public static <F$, V> HFoldr<F$, V, HNil, V> hFoldr() {
      return new HFoldr<F$, V, HNil, V>(new F3<F$, V, HNil, V>() {
        public V f(final F$ f, final V v, final HNil hNil) {
          return v;
        }
      });
    }

    /**
     * A fold instance for a non-empty heterogeneous list
     *
     * @param p    An operator that applies a function on the head of the list and the fold of its tail.
     * @param h    A fold instance for the tail of the list.
     * @param <E>  The type of the head of the list.
     * @param <F>  The type of function to apply to the head of the list and the fold of its tail.
     * @param <V>  The type of value to substitute for the empty list.
     * @param <L>  The type of the tail of the list.
     * @param <R>  The type of the fold of the tail of the list.
     * @param <RR> The return type of the fold.
     * @param <H>  The type of the fold instance for the tail of the list.
     * @param <PP> The type of the given function application operator.
     * @return A fold instance for a non-empty heterogeneous list.
     */
    public static <E, F, V, L extends HList<L>, R, RR,
      H extends HFoldr<F, V, L, R>,
      PP extends Apply<F, P2<E, R>, RR>>
    HFoldr<F, V, HCons<E, L>, RR> hFoldr(final PP p, final H h) {
      return new HFoldr<F, V, HCons<E, L>, RR>(new F3<F, V, HCons<E, L>, RR>() {
        public RR f(final F f, final V v, final HCons<E, L> c) {
          return p.apply(f, P.p(c.head(), h.foldRight(f, v, c.tail())));
        }
      });
    }

    /**
     * Folds a non-empty heterogeneous list.
     *
     * @param f A function with which to fold.
     * @param v The value to substitute for the empty list.
     * @param l The heterogeneous list to be folded.
     * @return a value obtained by folding the given list with the given function.
     */
    public R foldRight(final F f, final V v, final L l) {
      return foldRight.f(f, v, l);
    }

  }

}
