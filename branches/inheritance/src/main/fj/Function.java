package fj;

/**
 * Transformations on functions.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public final class Function {
  private Function() {
    throw new UnsupportedOperationException();
  }

  /**
   * Function composition.
   *
   * @return A function that composes two functions to produce a new function.
   */
  public static <A, B, C> F<F<B, C>, F<F<A, B>, F<A, C>>> compose() {
    return new F<F<B, C>, F<F<A, B>, F<A, C>>>() {
      public F<F<A, B>, F<A, C>> f(final F<B, C> f) {
        return new F<F<A, B>, F<A, C>>() {
          public F<A, C> f(final F<A, B> g) {
            return compose(f, g);
          }
        };
      }
    };
  }

  /**
   * Function composition.
   *
   * @param f A function to compose with another.
   * @param g A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  public static <A, B, C> F<A, C> compose(final F<B, C> f, final F<A, B> g) {
    return new F<A, C>() {
      public C f(final A a) {
        return f.f(g.f(a));
      }
    };
  }

  /**
   * Function composition flipped.
   *
   * @return A function that composes two functions to produce a new function.
   */
  public static <A, B, C> F<F<A, B>, F<F<B, C>, F<A, C>>> andThen() {
    return new F<F<A, B>, F<F<B, C>, F<A, C>>>() {
      public F<F<B, C>, F<A, C>> f(final F<A, B> g) {
        return new F<F<B, C>, F<A, C>>() {
          public F<A, C> f(final F<B, C> f) {
            return andThen(g, f);
          }
        };
      }
    };
  }

  /**
   * Function composition flipped.
   *
   * @param g A function to compose with another.
   * @param f A function to compose with another.
   * @return A function that is the composition of the given arguments.
   */
  public static <A, B, C> F<A, C> andThen(final F<A, B> g, final F<B, C> f) {
    return new F<A, C>() {
      public C f(final A a) {
        return f.f(g.f(a));
      }
    };
  }

  /**
   * The identity transformation.
   *
   * @return The identity transformation.
   */
  public static <A> F<A, A> identity() {
    return new F<A, A>() {
      public A f(final A a) {
        return a;
      }
    };
  }

  /**
   * Returns a function that given an argument, returns a function that ignores its argument.
   *
   * @return A function that given an argument, returns a function that ignores its argument.
   */
  public static <A, B> F<B, F<A, B>> constant() {
    return new F<B, F<A, B>>() {
      public F<A, B> f(final B b) {
        return constant(b);
      }
    };
  }

  /**
   * Returns a function that ignores its argument to constantly produce the given value.
   *
   * @param b The value to return when the returned function is applied.
   * @return A function that ignores its argument to constantly produce the given value.
   */
  public static <A, B> F<A, B> constant(final B b) {
    return new F<A, B>() {
      public B f(final A a) {
        return b;
      }
    };
  }

  /**
   * Function argument flipping.
   *
   * @return A function that takes a function and flips its arguments.
   */
  public static <A, B, C> F<F<A, F<B, C>>, F<B, F<A, C>>> flip() {
    return new F<F<A, F<B, C>>, F<B, F<A, C>>>() {
      public F<B, F<A, C>> f(final F<A, F<B, C>> f) {
        return flip(f);
      }
    };
  }

  /**
   * Function argument flipping.
   *
   * @param f The function to flip.
   * @return The given function flipped.
   */
  public static <A, B, C> F<B, F<A, C>> flip(final F<A, F<B, C>> f) {
    return new F2<B, A, C>() {
      public C f(final B b, final A a) {
        return f.f(a).f(b);
      }
    };
  }

  /**
   * Uncurry a function of arity-2.
   *
   * @return An uncurried function.
   */
  public static <A, B, C> F<F<A, F<B, C>>, F2<A, B, C>> uncurryF2() {
    return new F<F<A, F<B, C>>, F2<A, B, C>>() {
      public F2<A, B, C> f(final F<A, F<B, C>> f) {
        return uncurryF2(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-2.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C> F2<A, B, C> uncurryF2(final F<A, F<B, C>> f) {
    return new F2<A, B, C>() {
      public C f(final A a, final B b) {
        return f.f(a).f(b);
      }
    };
  }


  /**
   * Uncurry a function of arity-3.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D> F<F<A, F<B, F<C, D>>>, F3<A, B, C, D>> uncurryF3() {
    return new F<F<A, F<B, F<C, D>>>, F3<A, B, C, D>>() {
      public F3<A, B, C, D> f(final F<A, F<B, F<C, D>>> f) {
        return uncurryF3(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-3.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D> F3<A, B, C, D> uncurryF3(final F<A, F<B, F<C, D>>> f) {
    return new F3<A, B, C, D>() {
      public D f(final A a, final B b, final C c) {
        return f.f(a).f(b).f(c);
      }
    };
  }

  /**
   * Uncurry a function of arity-4.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E> F<F<A, F<B, F<C, F<D, E>>>>, F4<A, B, C, D, E>> uncurryF4() {
    return new F<F<A, F<B, F<C, F<D, E>>>>, F4<A, B, C, D, E>>() {
      public F4<A, B, C, D, E> f(final F<A, F<B, F<C, F<D, E>>>> f) {
        return uncurryF4(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-4.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E> F4<A, B, C, D, E> uncurryF4(final F<A, F<B, F<C, F<D, E>>>> f) {
    return new F4<A, B, C, D, E>() {
      public E f(final A a, final B b, final C c, final D d) {
        return f.f(a).f(b).f(c).f(d);
      }
    };
  }

  /**
   * Uncurry a function of arity-5.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$> F<F<A, F<B, F<C, F<D, F<E, F$>>>>>, F5<A, B, C, D, E, F$>> uncurryF5() {
    return new F<F<A, F<B, F<C, F<D, F<E, F$>>>>>, F5<A, B, C, D, E, F$>>() {
      public F5<A, B, C, D, E, F$> f(final F<A, F<B, F<C, F<D, F<E, F$>>>>> f) {
        return uncurryF5(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-6.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$> F5<A, B, C, D, E, F$> uncurryF5(final F<A, F<B, F<C, F<D, F<E, F$>>>>> f) {
    return new F5<A, B, C, D, E, F$>() {
      public F$ f(final A a, final B b, final C c, final D d, final E e) {
        return f.f(a).f(b).f(c).f(d).f(e);
      }
    };
  }


  /**
   * Uncurry a function of arity-6.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G> F<F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>>, F6<A, B, C, D, E, F$, G>> uncurryF6() {
    return new F<F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>>, F6<A, B, C, D, E, F$, G>>() {
      public F6<A, B, C, D, E, F$, G> f(final F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f) {
        return uncurryF6(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-6.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G> F6<A, B, C, D, E, F$, G> uncurryF6(final F<A, F<B, F<C, F<D, F<E, F<F$, G>>>>>> f) {
    return new F6<A, B, C, D, E, F$, G>() {
      public G f(final A a, final B b, final C c, final D d, final E e, final F$ f$) {
        return f.f(a).f(b).f(c).f(d).f(e).f(f$);
      }
    };
  }


  /**
   * Uncurry a function of arity-7.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H> F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>>, F7<A, B, C, D, E, F$, G, H>> uncurryF7() {
    return new F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>>, F7<A, B, C, D, E, F$, G, H>>() {
      public F7<A, B, C, D, E, F$, G, H> f(final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
        return uncurryF7(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-7.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H> F7<A, B, C, D, E, F$, G, H> uncurryF7(final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, H>>>>>>> f) {
    return new F7<A, B, C, D, E, F$, G, H>() {
      public H f(final A a, final B b, final C c, final D d, final E e, final F$ f$, final G g) {
        return f.f(a).f(b).f(c).f(d).f(e).f(f$).f(g);
      }
    };
  }


  /**
   * Uncurry a function of arity-8.
   *
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>>, F8<A, B, C, D, E, F$, G, H, I>> uncurryF8() {
    return new F<F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>>, F8<A, B, C, D, E, F$, G, H, I>>() {
      public F8<A, B, C, D, E, F$, G, H, I> f(final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f) {
        return uncurryF8(f);
      }
    };
  }

  /**
   * Uncurry a function of arity-8.
   *
   * @param f The function to uncurry.
   * @return An uncurried function.
   */
  public static <A, B, C, D, E, F$, G, H, I> F8<A, B, C, D, E, F$, G, H, I> uncurryF8(final F<A, F<B, F<C, F<D, F<E, F<F$, F<G, F<H, I>>>>>>>> f) {
    return new F8<A, B, C, D, E, F$, G, H, I>() {
      public I f(final A a, final B b, final C c, final D d, final E e, final F$ f$, final G g, final H h) {
        return f.f(a).f(b).f(c).f(d).f(e).f(f$).f(g).f(h);
      }
    };
  }
}
