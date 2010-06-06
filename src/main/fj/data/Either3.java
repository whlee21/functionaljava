package fj.data;

import fj.F;
import fj.Function;

import static fj.data.Either.left;
import static fj.data.Either.right;

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

  public final Either<A, Either<B, C>> eEither() {
    return either3(new F<A, Either<A, Either<B, C>>>() {
      public Either<A, Either<B, C>> f(final A a) {
        return left(a);
      }
    }, new F<B, Either<A, Either<B, C>>>() {
      public Either<A, Either<B, C>> f(final B b) {
        return right(Either.<B, C>left(b));
      }
    }, new F<C, Either<A, Either<B, C>>>() {
      public Either<A, Either<B, C>> f(final C c) {
        return right(Either.<B, C>right(c));
      }
    });
  }

  public final Either<Either<A, B>, C> eitherE() {
    return either3(new F<A, Either<Either<A, B>, C>>() {
      public Either<Either<A, B>, C> f(final A a) {
        return left(Either.<A, B>left(a));
      }
    }, new F<B, Either<Either<A, B>, C>>() {
      public Either<Either<A, B>, C> f(final B b) {
        return left(Either.<A, B>right(b));
      }
    }, new F<C, Either<Either<A, B>, C>>() {
      public Either<Either<A, B>, C> f(final C c) {
        return right(c);
      }
    });
  }

  public final Either3<B, A, C> swapBAC() {
    return new Either3<B, A, C>() {
      public <X> X either3(final F<B, X> thiss, final F<A, X> that, final F<C, X> other) {
        return Either3.this.either3(that, thiss, other);
      }
    };
  }

  public final Either3<C, B, A> swapCBA() {
    return new Either3<C, B, A>() {
      public <X> X either3(final F<C, X> thiss, final F<B, X> that, final F<A, X> other) {
        return Either3.this.either3(other, that, thiss);
      }
    };
  }

  public final Either3<A, C, B> swapACB() {
    return new Either3<A, C, B>() {
      public <X> X either3(final F<A, X> thiss, final F<C, X> that, final F<B, X> other) {
        return Either3.this.either3(thiss, other, that);
      }
    };
  }

  public final Either3<B, C, A> swapBCA() {
    return new Either3<B, C, A>() {
      public <X> X either3(final F<B, X> thiss, final F<C, X> that, final F<A, X> other) {
        return Either3.this.either3(other, thiss, that);
      }
    };
  }

  public final Either3<C, A, B> swapCAB() {
    return new Either3<C, A, B>() {
      public <X> X either3(final F<C, X> thiss, final F<A, X> that, final F<B, X> other) {
        return Either3.this.either3(that, other, thiss);
      }
    };
  }

  public static <A, B, C> Either3<A, B, C> thiss(final A a) {
    return new Either3<A, B, C>() {
      public <X> X either3(final F<A, X> thiss, final F<B, X> that, final F<C, X> other) {
        return thiss.f(a);
      }
    };
  }

  public static <A, B, C> F<A, Either3<A, B, C>> thiss() {
    return new F<A, Either3<A, B, C>>() {
      public Either3<A, B, C> f(final A a) {
        return thiss(a);
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

  public static <A, B, C> F<B, Either3<A, B, C>> that() {
    return new F<B, Either3<A, B, C>>() {
      public Either3<A, B, C> f(final B b) {
        return that(b);
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

  public static <A, B, C> F<C, Either3<A, B, C>> other() {
    return new F<C, Either3<A, B, C>>() {
      public Either3<A, B, C> f(final C c) {
        return other(c);
      }
    };
  }
}
