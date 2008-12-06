package fj.pre;

import fj.F;
import fj.data.Array;
import fj.data.Either;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import static fj.pre.Equal.booleanEqual;
import static fj.pre.Equal.byteEqual;
import static fj.pre.Equal.charEqual;
import static fj.pre.Equal.doubleEqual;
import static fj.pre.Equal.equal;
import static fj.pre.Equal.floatEqual;
import static fj.pre.Equal.intEqual;
import static fj.pre.Equal.longEqual;
import static fj.pre.Equal.shortEqual;
import static fj.pre.Equal.stringEqual;
import static fj.pre.Ordering.EQ;
import static fj.pre.Ordering.GT;
import static fj.pre.Ordering.LT;

/**
 * Tests for ordering between two objects.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public final class Ord<A> {
  private final Equal<A> eq;
  private final F<A, F<A, Ordering>> f;

  private Ord(final Equal<A> eq, final F<A, F<A, Ordering>> f) {
    this.eq = eq;
    this.f = f;
  }

  /**
   * Returns an ordering for the given arguments.
   * 
   * @param a1 An instance to compare for ordering to another.
   * @param a2 An instance to compare for ordering to another.
   * @return An ordering for the given arguments.
   */
  public Ordering compare(final A a1, final A a2) {
    return f.f(a1).f(a2);
  }

  /**
   * Returns <code>true</code> if the given arguments are equal, <code>false</code> otherwise.
   *
   * @param a1 An instance to compare for equality to another.
   * @param a2 An instance to compare for equality to another.
   * @return <code>true</code> if the given arguments are equal, <code>false</code> otherwise.
   */
  public boolean eq(final A a1, final A a2) {
    return eq.eq(a1, a2);
  }

  /**
   * Returns <code>true</code> if the first given argument is less than the second given argument,
   * <code>false</code> otherwise.
   *
   * @param a1 An instance to compare for ordering to another.
   * @param a2 An instance to compare for ordering to another.
   * @return <code>true</code> if the first given argument is less than the second given argument,
   * <code>false</code> otherwise.
   */
  public boolean isLessThan(final A a1, final A a2) {
    return compare(a1, a2) == LT;
  }

  /**
   * Returns <code>true</code> if the first given argument is greater than the second given
   * argument, <code>false</code> otherwise.
   *
   * @param a1 An instance to compare for ordering to another.
   * @param a2 An instance to compare for ordering to another.
   * @return <code>true</code> if the first given argument is greater than the second given
   * argument, <code>false</code> otherwise.
   */
  public boolean isGreaterThan(final A a1, final A a2) {
    return compare(a1, a2) == GT;
  }

  /**
   * Returns an order instance that uses the given equality test and ordering function.
   *
   * @param eq The equality instance.
   * @param f The order function.
   * @return An order instance.
   */
  public static <A> Ord<A> ord(final Equal<A> eq, final F<A, F<A, Ordering>> f) {
    return new Ord<A>(eq, f);
  }

  /**
   * Returns an order instance that uses the given equality function and ordering function.
   *
   * @param eq The equality function.
   * @param f The order function.
   * @return An order instance that uses the given equality function and ordering function.
   */
  public static <A> Ord<A> ord(final F<A, F<A, Boolean>> eq, final F<A, F<A, Ordering>> f) {
    return ord(equal(eq), f);
  }

  /**
   * An order instance for the <code>boolean</code> type.
   */
  public static final Ord<Boolean> booleanOrd = new Ord<Boolean>(booleanEqual,
      new F<Boolean, F<Boolean, Ordering>>() {
    public F<Boolean, Ordering> f(final Boolean a1) {
      return new F<Boolean, Ordering>() {
        public Ordering f(final Boolean a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? LT : x == 0 ? EQ : GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>byte</code> type.
   */
  public static final Ord<Byte> byteOrd = new Ord<Byte>(byteEqual,
      new F<Byte, F<Byte, Ordering>>() {
    public F<Byte, Ordering> f(final Byte a1) {
      return new F<Byte, Ordering>() {
        public Ordering f(final Byte a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? LT : x == 0 ? EQ : GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>char</code> type.
   */
  public static final Ord<Character> charOrd = new Ord<Character>(charEqual,
      new F<Character, F<Character, Ordering>>() {
    public F<Character, Ordering> f(final Character a1) {
      return new F<Character, Ordering>() {
        public Ordering f(final Character a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? LT : x == 0 ? EQ : GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>double</code> type.
   */
  public static final Ord<Double> doubleOrd = new Ord<Double>(doubleEqual,
      new F<Double, F<Double, Ordering>>() {
    public F<Double, Ordering> f(final Double a1) {
      return new F<Double, Ordering>() {
        public Ordering f(final Double a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? LT : x == 0 ? EQ : GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>float</code> type.
   */
  public static final Ord<Float> floatOrd = new Ord<Float>(floatEqual,
      new F<Float, F<Float, Ordering>>() {
    public F<Float, Ordering> f(final Float a1) {
      return new F<Float, Ordering>() {
        public Ordering f(final Float a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? LT : x == 0 ? EQ : GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>int</code> type.
   */
  public static final Ord<Integer> intOrd = new Ord<Integer>(intEqual,
      new F<Integer, F<Integer, Ordering>>() {
    public F<Integer, Ordering> f(final Integer a1) {
      return new F<Integer, Ordering>() {
        public Ordering f(final Integer a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? LT : x == 0 ? EQ : GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>long</code> type.
   */
  public static final Ord<Long> longOrd = new Ord<Long>(longEqual,
      new F<Long, F<Long, Ordering>>() {
    public F<Long, Ordering> f(final Long a1) {
      return new F<Long, Ordering>() {
        public Ordering f(final Long a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? LT : x == 0 ? EQ : GT;
        }
      };
    }
  });

  /**
   * An order instance for the <code>short</code> type.
   */
  public static final Ord<Short> shortOrd = new Ord<Short>(shortEqual,
      new F<Short, F<Short, Ordering>>() {
    public F<Short, Ordering> f(final Short a1) {
      return new F<Short, Ordering>() {
        public Ordering f(final Short a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? LT : x == 0 ? EQ : GT;
        }
      };
    }
  });

  /**
   * An order instance for the {@link String} type.
   */
  public static final Ord<String> stringOrd = new Ord<String>(stringEqual,
      new F<String, F<String, Ordering>>() {
    public F<String, Ordering> f(final String a1) {
      return new F<String, Ordering>() {
        public Ordering f(final String a2) {
          final int x = a1.compareTo(a2);
          return x < 0 ? LT : x == 0 ? EQ : GT;
        }
      };
    }
  });

  /**
   * An order instance for the {@link Option} type.
   * 
   * @param oa Order across the element of the option.
   * @param eq Equality across the element of the option.
   * @return An order instance for the {@link Option} type.
   */
  public static <A> Ord<Option<A>> optionOrd(final Ord<A> oa, final Equal<Option<A>> eq) {
    return new Ord<Option<A>>(eq, new F<Option<A>, F<Option<A>, Ordering>>() {
      public F<Option<A>, Ordering> f(final Option<A> o1) {
        return new F<Option<A>, Ordering>() {
          public Ordering f(final Option<A> o2) {
            return o1.isNone() ?
                o2.isNone() ?
                    EQ :
                    LT :
                o2.isNone() ?
                    GT :
                    oa.f.f(o1.some()).f(o2.some());
          }
        };
      }
    });
  }

  /**
   * An order instance for the {@link Either} type.
   *
   * @param oa Order across the left side of {@link Either}.
   * @param ob Order across the right side of {@link Either}.
   * @param eq Equality across the {@link Either}.
   * @return An order instance for the {@link Either} type.
   */
  public static <A, B> Ord<Either<A, B>> eitherOrd(final Ord<A> oa, final Ord<B> ob, final Equal<Either<A, B>> eq) {
    return new Ord<Either<A, B>>(eq, new F<Either<A, B>, F<Either<A, B>, Ordering>>() {
      public F<Either<A, B>, Ordering> f(final Either<A, B> e1) {
        return new F<Either<A, B>, Ordering>() {
          public Ordering f(final Either<A, B> e2) {
            return e1.isLeft() ?
                e2.isLeft() ?
                    oa.f.f(e1.left().value()).f(e2.left().value()) :
                    LT :
                e2.isLeft() ?
                    GT :
                    ob.f.f(e1.right().value()).f(e2.right().value());
          }
        };
      }
    });
  }

  /**
   * An order instance for the {@link List} type.
   *
   * @param oa Order across the elements of the list.
   * @param eq Equality across the elements of the list.
   * @return An order instance for the {@link List} type.
   */
  public static <A> Ord<List<A>> listOrd(final Ord<A> oa, final Equal<List<A>> eq) {
    return new Ord<List<A>>(eq, new F<List<A>, F<List<A>, Ordering>>() {
      public F<List<A>, Ordering> f(final List<A> l1) {
        return new F<List<A>, Ordering>() {
          public Ordering f(final List<A> l2) {
            if(l1.isEmpty())
              return l2.isEmpty() ? EQ : LT;
            else if(l2.isEmpty())
              return l1.isEmpty() ? EQ : GT;
            else {
              final Ordering c = oa.compare(l1.head(), l2.head());
              return c == EQ ? listOrd(oa, eq).f.f(l1.tail()).f(l2.tail()) : c;
            }
          }
        };
      }
    });
  }

  /**
   * An order instance for the {@link Stream} type.
   *
   * @param oa Order across the elements of the stream.
   * @param eq Equality across the elements of the stream.
   * @return An order instance for the {@link Stream} type.
   */
  public static <A> Ord<Stream<A>> streamOrd(final Ord<A> oa, final Equal<Stream<A>> eq) {
    return new Ord<Stream<A>>(eq, new F<Stream<A>, F<Stream<A>, Ordering>>() {
      public F<Stream<A>, Ordering> f(final Stream<A> s1) {
        return new F<Stream<A>, Ordering>() {
          public Ordering f(final Stream<A> s2) {
            if(s1.isEmpty())
              return s2.isEmpty() ? EQ : LT;
            else if(s2.isEmpty())
              return s1.isEmpty() ? EQ : GT;
            else {
              final Ordering c = oa.compare(s1.head(), s2.head());
              return c == EQ ? streamOrd(oa, eq).f.f(s1.tail()._1()).f(s2.tail()._1()) : c;
            }
          }
        };
      }
    });
  }

  /**
   * An order instance for the {@link Array} type.
   *
   * @param oa Order across the elements of the array.
   * @param eq Equality across the elements of the array.
   * @return An order instance for the {@link Array} type.
   */
  public static <A> Ord<Array<A>> arrayOrd(final Ord<A> oa, final Equal<Array<A>> eq) {
    return new Ord<Array<A>>(eq, new F<Array<A>, F<Array<A>, Ordering>>() {
      public F<Array<A>, Ordering> f(final Array<A> a1) {
        return new F<Array<A>, Ordering>() {
          public Ordering f(final Array<A> a2) {
            int i = 0;
            //noinspection ForLoopWithMissingComponent
            for(; i < a1.length() && i < a2.length(); i++) {
              final Ordering c = oa.compare(a1.get(i), a2.get(i));
              if(c == GT || c == LT)
                return c;
            }
            return i == a1.length() ?
                i == a2.length() ?
                    EQ :
                    LT :
                i == a1.length() ?
                    EQ :
                    GT;
          }
        };
      }
    });
  }
}
