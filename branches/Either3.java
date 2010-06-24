package fj.data;

import fj.Effect;
import fj.F;
import fj.Function;
import fj.P1;
import fj.Unit;

import java.util.Collection;
import java.util.Iterator;

import static fj.P.p;
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

  public final ThisProjection<A, B, C> thiss() {
    return new ThisProjection<A, B, C>(this);
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

  public final class ThisProjection<A, B, C> implements Iterable<A> {
    private final Either3<A, B, C> e;
    private final Either<A, Either<B, C>> eE;

    private ThisProjection(final Either3<A, B, C> e) {
      this.e = e;
      eE = e.eEither();
    }

    public Iterator<A> iterator() {
      return eE.left().iterator();
    }

    public A valueE(final P1<String> err) {
      return eE.left().valueE(err);
    }

    public A valueE(final String err) {
      return eE.left().valueE(err);
    }

    public A value() {
      return valueE(p("this.value on that or other"));
    }

    public A orValue(final P1<A> a) {
      return eE.left().orValue(a);
    }

    public A orValue(final A a) {
      return eE.left().orValue(a);
    }

    public A on(final F<Either<B, C>, A> f) {
      return eE.left().on(f);
    }

    public Unit foreach(final F<A, Unit> f) {
      return eE.left().foreach(f);
    }

    public void foreach(final Effect<A> f) {
      eE.left().foreach(f);
    }

    public <X> Either3<X, B, C> map(final F<A, X> f) {
      return e.either3(f.andThen(Either3.<X, B, C>thiss_()),
          Either3.<X, B, C>that_(),
          Either3.<X, B, C>other_());
    }

    public <X> Either3<X, B, C> bind(final F<A, Either3<X, B, C>> f) {
      return fromEitherE(eE.left().bind(f.andThen(new F<Either3<X, B, C>, Either<X, Either<B, C>>>() {
        public Either<X, Either<B, C>> f(final Either3<X, B, C> k) {
          return k.eEither();
        }
      })));
    }

    public <X> Either3<X, B, C> bind(final Either3<X, B, C> e) {
      return bind(Function.<A, Either3<X, B, C>>constant(e));
    }

    public <Y, Z> Option<Either3<A, Y, Z>> filter(final F<A, Boolean> f) {
      return eE.isLeft() && f.f(eE.left().value()) ?
          Option.some(Either3.<A, Y, Z>thiss(value())) :
          Option.<Either3<A, Y, Z>>none();
    }

    public <X> Either3<X, B, C> apply(final Either3<F<A, X>, B, C> f) {
      return f.thiss().bind(new F<F<A, X>, Either3<X, B, C>>() {
        public Either3<X, B, C> f(final F<A, X> k) {
          return map(k);
        }
      });
    }

    public boolean forall(final F<A, Boolean> f) {
      return eE.left().forall(f);
    }

    public boolean exists(final F<A, Boolean> f) {
      return eE.left().exists(f);
    }

    public List<A> toList() {
      return eE.left().toList();
    }

    public Option<A> toOption() {
      return eE.left().toOption();
    }

    public Stream<A> toStream() {
      return eE.left().toStream();
    }

    public Collection<A> toCollection() {
      return eE.left().toCollection();
    }
  }

  public final class ThatProjection<A, B, C> implements Iterable<B> {
    private final Either3<A, B, C> e;
    private final Either<B, Either<A, C>> eE;

    private ThatProjection(final Either3<A, B, C> e) {
      this.e = e;
      eE = e.swapBAC().eEither();
    }

    public Iterator<B> iterator() {
      return eE.left().iterator();
    }

    public B valueE(final P1<String> err) {
      return eE.left().valueE(err);
    }

    public B valueE(final String err) {
      return eE.left().valueE(err);
    }

    public B value() {
      return valueE(p("that.value on this or other"));
    }

    public B orValue(final P1<B> a) {
      return eE.left().orValue(a);
    }

    public B orValue(final B a) {
      return eE.left().orValue(a);
    }

    public B on(final F<Either<A, C>, B> f) {
      return eE.left().on(f);
    }

    public Unit foreach(final F<B, Unit> f) {
      return eE.left().foreach(f);
    }

    public void foreach(final Effect<B> f) {
      eE.left().foreach(f);
    }

    public <X> Either3<A, X, C> map(final F<B, X> f) {
      return e.either3(Either3.<A, X, C>thiss_(),
          f.andThen(Either3.<A, X, C>that_()),
          Either3.<A, X, C>other_());
    }

    /*
    public <X> Either3<A, X, C> bind(final F<B, Either3<A, X, C>> f) {
      return fromEitherE(eE.left().bind(f.andThen(new F<Either3<A, X, C>, Either<X, Either<A, C>>>() {
        public Either<X, Either<B, C>> f(final Either3<X, B, C> k) {
          return k.eEither();
        }
      })));
    }

    public <X> Either3<X, B, C> bind(final Either3<X, B, C> e) {
      return bind(Function.<A, Either3<X, B, C>>constant(e));
    }

    public <Y, Z> Option<Either3<A, Y, Z>> filter(final F<A, Boolean> f) {
      return eE.isLeft() && f.f(eE.left().value()) ?
          Option.some(Either3.<A, Y, Z>thiss(value())) :
          Option.<Either3<A, Y, Z>>none();
    }

    public <X> Either3<X, B, C> apply(final Either3<F<A, X>, B, C> f) {
      return f.thiss().bind(new F<F<A, X>, Either3<X, B, C>>() {
        public Either3<X, B, C> f(final F<A, X> k) {
          return map(k);
        }
      });
    }

    public boolean forall(final F<A, Boolean> f) {
      return eE.left().forall(f);
    }

    public boolean exists(final F<A, Boolean> f) {
      return eE.left().exists(f);
    }

    public List<A> toList() {
      return eE.left().toList();
    }

    public Option<A> toOption() {
      return eE.left().toOption();
    }

    public Stream<A> toStream() {
      return eE.left().toStream();
    }

    public Collection<A> toCollection() {
      return eE.left().toCollection();
    }
    */
  }
  
  public static <A, B, C> F<A, Either3<A, B, C>> thiss_() {
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

  public static <A, B, C> F<B, Either3<A, B, C>> that_() {
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

  public static <A, B, C> F<C, Either3<A, B, C>> other_() {
    return new F<C, Either3<A, B, C>>() {
      public Either3<A, B, C> f(final C c) {
        return other(c);
      }
    };
  }

  public static <A, B, C> Either3<A, B, C> fromEitherE(final Either<A, Either<B, C>> e) {
    return e.isLeft() ?
        Either3.<A, B, C>thiss(e.left().value()) :
        e.right().value().isLeft() ?
          Either3.<A, B, C>that(e.right().value().left().value()) :
          Either3.<A, B, C>other(e.right().value().right().value());
  }

  public static <A, B, C> Either3<A, B, C> fromEEither(final Either<Either<A, B>, C> e) {
    return e.isRight() ?
        Either3.<A, B, C>other(e.right().value()) :
        e.left().value().isRight() ?
          Either3.<A, B, C>that(e.left().value().right().value()) :
          Either3.<A, B, C>thiss(e.left().value().left().value());
  }
}
