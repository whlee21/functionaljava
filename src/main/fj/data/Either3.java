package fj.data;

import fj.F;
import fj.Function;

// todo (WIP)
public abstract class Either3<A, B, C> {
  public abstract <X> X either3(F<A, X> thiss, F<B, X> that, F<C, X> other);

  public final boolean isThis() {
    return either3(Function.<A, Boolean>constant(true),
                   Function.<B, Boolean>constant(false),
                   Function.<C, Boolean>constant(false));
  }

  public final boolean isThat() {
    return either3(Function.<A, Boolean>constant(false),
                   Function.<B, Boolean>constant(true),
                   Function.<C, Boolean>constant(false));
  }

  public final boolean isOther() {
    return either3(Function.<A, Boolean>constant(false),
                   Function.<B, Boolean>constant(false),
                   Function.<C, Boolean>constant(true));
  }

  public static <A, B, C> Either3<A, B, C> thiss(final A a) {
    return new Either3<A, B, C>() {
      public <X> X either3(final F<A, X> thiss, final F<B, X> that, final F<C, X> other) {
        return thiss.f(a);
      }
    };
  }

  public static <A, B, C> Either3<A, B, C> that(final B b) {
    return new Either3<A, B, C>() {
      public <X> X either3(final F<A, X> thiss, final F<B, X> that, final F<C, X> other) {
        return that.f(b);
      }
    };
  }

  public static <A, B, C> Either3<A, B, C> other(final C c) {
    return new Either3<A, B, C>() {
      public <X> X either3(final F<A, X> thiss, final F<B, X> that, final F<C, X> other) {
        return other.f(c);
      }
    };
  }
}
