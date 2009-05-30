package fj.test;

import static fj.Function.curry;
import static fj.P.p;
import static fj.data.Array.array;
import static fj.data.List.fromString;
import static fj.data.List.nil;
import static fj.test.Variant.variant;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import fj.F;
import fj.F2;
import fj.F3;
import fj.F4;
import fj.F5;
import fj.F6;
import fj.F7;
import fj.F8;
import fj.P1;
import fj.P2;
import fj.P3;
import fj.P4;
import fj.P5;
import fj.P6;
import fj.P7;
import fj.P8;
import fj.data.Array;
import fj.data.Either;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;

/**
 * Transforms a type and a generator to produce a new generator. This function is used to generate
 * {@link Arbitrary arbitrary} functions.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          <li>$LastChangedBy$</li>
 *          </ul>
 */
public abstract class Coarbitrary<A> {
  /**
   * Transforms the given value and generator to a new generator with a high probability of being
   * independent.
   *
   * @param a The value to produce the generator from.
   * @param g The generator to produce the new generator from.
   * @return A new generator with a high probability of being independent.
   */
  public abstract <B> Gen<B> coarbitrary(A a, Gen<B> g);

  /**
   * A curried version of {@link #coarbitrary(Object, Gen)}.
   *
   * @param a The value to produce the generator from.
   * @return A curried version of {@link #coarbitrary(Object, Gen)}.
   */
  public <B> F<Gen<B>, Gen<B>> coarbitrary(final A a) {
    return new F<Gen<B>, Gen<B>>() {
      public Gen<B> f(final Gen<B> g) {
        return coarbitrary(a, g);
      }
    };
  }

  /**
   * Composes the given function with this coarbitrary to produce a new coarbitrary.
   *
   * @param f The function to compose.
   * @return A new coarbitrary composed with the given function.
   */
  public <B> Coarbitrary<B> compose(final F<B, A> f) {
    return new Coarbitrary<B>() {
      public <X> Gen<X> coarbitrary(final B b, final Gen<X> g) {
        return Coarbitrary.this.coarbitrary(f.f(b), g);
      }
    };
  }

  /**
   * Co-maps this coarbitrary using the given function.
   *
   * @param f The function to co-map with.
   * @return A co-mapped coarbitrary.
   */
  public <B> Coarbitrary<B> comap(final F<B, A> f) {
    return new Coarbitrary<B>() {
      public <X> Gen<X> coarbitrary(final B b, final Gen<X> g) {
        return Coarbitrary.this.coarbitrary(f.f(b), g);
      }
    };
  }

  /**
   * A coarbitrary for a function.
   *
   * @param a An arbitrary for the domain of the function.
   * @param c A coarbitrary for the codomain of the function.
   * @return A coarbitrary for a function.
   */
  public static <A, B> Coarbitrary<F<A, B>> coarbF(final Arbitrary<A> a, final Coarbitrary<B> c) {
    return new Coarbitrary<F<A, B>>() {
      public <X> Gen<X> coarbitrary(final F<A, B> f, final Gen<X> g) {
        return a.gen.bind(new F<A, Gen<X>>() {
          public Gen<X> f(final A a) {
            return c.coarbitrary(f.f(a), g);
          }
        });
      }
    };
  }

  /**
   * A coarbitrary for a function-2.
   *
   * @param aa An arbitrary for part of the domain of the function.
   * @param ab An arbitrary for part of the domain of the function.
   * @param c  A coarbitrary for the codomain of the function.
   * @return A coarbitrary for a function-2.
   */
  public static <A, B, C> Coarbitrary<F2<A, B, C>> coarbF2(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                           final Coarbitrary<C> c) {
    return new Coarbitrary<F2<A, B, C>>() {
      public <X> Gen<X> coarbitrary(final F2<A, B, C> f, final Gen<X> g) {
        return coarbF(aa, coarbF(ab, c)).coarbitrary(curry(f), g);
      }
    };
  }

  /**
   * A coarbitrary for a function-3.
   *
   * @param aa An arbitrary for part of the domain of the function.
   * @param ab An arbitrary for part of the domain of the function.
   * @param ac An arbitrary for part of the domain of the function.
   * @param c  A coarbitrary for the codomain of the function.
   * @return A coarbitrary for a function-3.
   */
  public static <A, B, C, D> Coarbitrary<F3<A, B, C, D>> coarbF3(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                                 final Arbitrary<C> ac, final Coarbitrary<D> c) {
    return new Coarbitrary<F3<A, B, C, D>>() {
      public <X> Gen<X> coarbitrary(final F3<A, B, C, D> f, final Gen<X> g) {
        return coarbF(aa, coarbF(ab, coarbF(ac, c))).coarbitrary(curry(f), g);
      }
    };
  }

  /**
   * A coarbitrary for a function-4.
   *
   * @param aa An arbitrary for part of the domain of the function.
   * @param ab An arbitrary for part of the domain of the function.
   * @param ac An arbitrary for part of the domain of the function.
   * @param ad An arbitrary for part of the domain of the function.
   * @param c  A coarbitrary for the codomain of the function.
   * @return A coarbitrary for a function-4.
   */
  public static <A, B, C, D, E> Coarbitrary<F4<A, B, C, D, E>> coarbF4(final Arbitrary<A> aa, final Arbitrary<B> ab,
                                                                       final Arbitrary<C> ac, final Arbitrary<D> ad,
                                                                       final Coarbitrary<E> c) {
    return new Coarbitrary<F4<A, B, C, D, E>>() {
      public <X> Gen<X> coarbitrary(final F4<A, B, C, D, E> f, final Gen<X> g) {
        return coarbF(aa, coarbF(ab, coarbF(ac, coarbF(ad, c)))).coarbitrary(curry(f), g);
      }
    };
  }

  /**
   * A coarbitrary for a function-5.
   *
   * @param aa An arbitrary for part of the domain of the function.
   * @param ab An arbitrary for part of the domain of the function.
   * @param ac An arbitrary for part of the domain of the function.
   * @param ad An arbitrary for part of the domain of the function.
   * @param ae An arbitrary for part of the domain of the function.
   * @param c  A coarbitrary for the codomain of the function.
   * @return A coarbitrary for a function-5.
   */
  public static <A, B, C, D, E, F$> Coarbitrary<F5<A, B, C, D, E, F$>> coarbF5(final Arbitrary<A> aa,
                                                                               final Arbitrary<B> ab,
                                                                               final Arbitrary<C> ac,
                                                                               final Arbitrary<D> ad,
                                                                               final Arbitrary<E> ae,
                                                                               final Coarbitrary<F$> c) {
    return new Coarbitrary<F5<A, B, C, D, E, F$>>() {
      public <X> Gen<X> coarbitrary(final F5<A, B, C, D, E, F$> f, final Gen<X> g) {
        return coarbF(aa, coarbF(ab, coarbF(ac, coarbF(ad, coarbF(ae, c))))).coarbitrary(curry(f), g);
      }
    };
  }

  /**
   * A coarbitrary for a function-6.
   *
   * @param aa An arbitrary for part of the domain of the function.
   * @param ab An arbitrary for part of the domain of the function.
   * @param ac An arbitrary for part of the domain of the function.
   * @param ad An arbitrary for part of the domain of the function.
   * @param ae An arbitrary for part of the domain of the function.
   * @param af An arbitrary for part of the domain of the function.
   * @param c  A coarbitrary for the codomain of the function.
   * @return A coarbitrary for a function-6.
   */
  public static <A, B, C, D, E, F$, G> Coarbitrary<F6<A, B, C, D, E, F$, G>> coarbF6(final Arbitrary<A> aa,
                                                                                     final Arbitrary<B> ab,
                                                                                     final Arbitrary<C> ac,
                                                                                     final Arbitrary<D> ad,
                                                                                     final Arbitrary<E> ae,
                                                                                     final Arbitrary<F$> af,
                                                                                     final Coarbitrary<G> c) {
    return new Coarbitrary<F6<A, B, C, D, E, F$, G>>() {
      public <X> Gen<X> coarbitrary(final F6<A, B, C, D, E, F$, G> f, final Gen<X> g) {
        return coarbF(aa, coarbF(ab, coarbF(ac, coarbF(ad, coarbF(ae, coarbF(af, c)))))).coarbitrary(curry(f), g);
      }
    };
  }

  /**
   * A coarbitrary for a function-7.
   *
   * @param aa An arbitrary for part of the domain of the function.
   * @param ab An arbitrary for part of the domain of the function.
   * @param ac An arbitrary for part of the domain of the function.
   * @param ad An arbitrary for part of the domain of the function.
   * @param ae An arbitrary for part of the domain of the function.
   * @param af An arbitrary for part of the domain of the function.
   * @param ag An arbitrary for part of the domain of the function.
   * @param c  A coarbitrary for the codomain of the function.
   * @return A coarbitrary for a function-7.
   */
  public static <A, B, C, D, E, F$, G, H> Coarbitrary<F7<A, B, C, D, E, F$, G, H>> coarbF7(final Arbitrary<A> aa,
                                                                                           final Arbitrary<B> ab,
                                                                                           final Arbitrary<C> ac,
                                                                                           final Arbitrary<D> ad,
                                                                                           final Arbitrary<E> ae,
                                                                                           final Arbitrary<F$> af,
                                                                                           final Arbitrary<G> ag,
                                                                                           final Coarbitrary<H> c) {
    return new Coarbitrary<F7<A, B, C, D, E, F$, G, H>>() {
      public <X> Gen<X> coarbitrary(final F7<A, B, C, D, E, F$, G, H> f, final Gen<X> g) {
        return coarbF(aa, coarbF(ab, coarbF(ac, coarbF(ad, coarbF(ae, coarbF(af, coarbF(ag, c)))))))
            .coarbitrary(curry(f), g);
      }
    };
  }

  /**
   * A coarbitrary for a function-8.
   *
   * @param aa An arbitrary for part of the domain of the function.
   * @param ab An arbitrary for part of the domain of the function.
   * @param ac An arbitrary for part of the domain of the function.
   * @param ad An arbitrary for part of the domain of the function.
   * @param ae An arbitrary for part of the domain of the function.
   * @param af An arbitrary for part of the domain of the function.
   * @param ag An arbitrary for part of the domain of the function.
   * @param ah An arbitrary for part of the domain of the function.
   * @param c  A coarbitrary for the codomain of the function.
   * @return A coarbitrary for a function-8.
   */
  public static <A, B, C, D, E, F$, G, H, I> Coarbitrary<F8<A, B, C, D, E, F$, G, H, I>> coarbF8(final Arbitrary<A> aa,
                                                                                                 final Arbitrary<B> ab,
                                                                                                 final Arbitrary<C> ac,
                                                                                                 final Arbitrary<D> ad,
                                                                                                 final Arbitrary<E> ae,
                                                                                                 final Arbitrary<F$> af,
                                                                                                 final Arbitrary<G> ag,
                                                                                                 final Arbitrary<H> ah,
                                                                                                 final Coarbitrary<I> c) {
    return new Coarbitrary<F8<A, B, C, D, E, F$, G, H, I>>() {
      public <X> Gen<X> coarbitrary(final F8<A, B, C, D, E, F$, G, H, I> f, final Gen<X> g) {
        return coarbF(aa, coarbF(ab, coarbF(ac, coarbF(ad, coarbF(ae, coarbF(af, coarbF(ag, coarbF(ah, c))))))))
            .coarbitrary(curry(f), g);
      }
    };
  }

  /**
   * A coarbitrary for booleans.
   */
  public static final Coarbitrary<Boolean> coarbBoolean = new Coarbitrary<Boolean>() {
    public <B> Gen<B> coarbitrary(final Boolean b, final Gen<B> g) {
      return variant(b ? 0 : 1, g);
    }
  };

  /**
   * A coarbitrary for integers.
   */
  public static final Coarbitrary<Integer> coarbInteger = new Coarbitrary<Integer>() {
    public <B> Gen<B> coarbitrary(final Integer i, final Gen<B> g) {
      return variant(i >= 0 ? 2 * i : -2 * i + 1, g);
    }
  };

  /**
   * A coarbitrary for bytes.
   */
  public static final Coarbitrary<Byte> coarbByte = new Coarbitrary<Byte>() {
    public <B> Gen<B> coarbitrary(final Byte b, final Gen<B> g) {
      return variant(b >= 0 ? 2 * b : -2 * b + 1, g);
    }
  };

  /**
   * A coarbitrary for shorts.
   */
  public static final Coarbitrary<Short> coarbShort = new Coarbitrary<Short>() {
    public <B> Gen<B> coarbitrary(final Short s, final Gen<B> g) {
      return variant(s >= 0 ? 2 * s : -2 * s + 1, g);
    }
  };

  /**
   * A coarbitrary for longs.
   */
  public static final Coarbitrary<Long> coarbLong = new Coarbitrary<Long>() {
    public <B> Gen<B> coarbitrary(final Long l, final Gen<B> g) {
      return variant(l >= 0L ? 2L * l : -2L * l + 1L, g);
    }
  };

  /**
   * A coarbitrary for characters.
   */
  public static final Coarbitrary<Character> coarbCharacter = new Coarbitrary<Character>() {
    public <B> Gen<B> coarbitrary(final Character c, final Gen<B> g) {
      return variant(c << 1, g);
    }
  };

  /**
   * A coarbitrary for the optional value.
   *
   * @param ca A coarbitrary for the type of the optional value.
   * @return A coarbitrary for the optional value.
   */
  public static <A> Coarbitrary<Option<A>> coarbOption(final Coarbitrary<A> ca) {
    return new Coarbitrary<Option<A>>() {
      public <B> Gen<B> coarbitrary(final Option<A> o, final Gen<B> g) {
        return o.isNone() ? variant(0, g) : variant(1, ca.coarbitrary(o.some(), g));
      }
    };
  }

  /**
   * A coarbitrary for the disjoint union.
   *
   * @param ca A coarbitrary for one side of the disjoint union.
   * @param cb A coarbitrary for one side of the disjoint union.
   * @return A coarbitrary for the disjoint union.
   */
  public static <A, B> Coarbitrary<Either<A, B>> coarbEither(final Coarbitrary<A> ca, final Coarbitrary<B> cb) {
    return new Coarbitrary<Either<A, B>>() {
      public <X> Gen<X> coarbitrary(final Either<A, B> e, final Gen<X> g) {
        return e.isLeft() ?
            variant(0, ca.coarbitrary(e.left().value(), g)) :
            variant(1, cb.coarbitrary(e.right().value(), g));
      }
    };
  }

  /**
   * A coarbitrary for lists.
   *
   * @param ca A coarbitrary for the elements of the list.
   * @return A coarbitrary for lists.
   */
  public static <A> Coarbitrary<List<A>> coarbList(final Coarbitrary<A> ca) {
    return new Coarbitrary<List<A>>() {
      public <B> Gen<B> coarbitrary(final List<A> as, final Gen<B> g) {
        return as.isEmpty() ?
            variant(0, g) :
            variant(1, ca.coarbitrary(as.head(), coarbitrary(as.tail(), g)));
      }
    };
  }

  /**
   * A coarbitrary for strings.
   */
  public static final Coarbitrary<String> coarbString = new Coarbitrary<String>() {
    public <B> Gen<B> coarbitrary(final String s, final Gen<B> g) {
      return coarbList(coarbCharacter).coarbitrary(fromString(s), g);
    }
  };

  /**
   * A coarbitrary for string buffers.
   */
  public static final Coarbitrary<StringBuffer> coarbStringBuffer = new Coarbitrary<StringBuffer>() {
    public <B> Gen<B> coarbitrary(final StringBuffer s, final Gen<B> g) {
      return coarbString.coarbitrary(s.toString(), g);
    }
  };

  /**
   * A coarbitrary for string builders.
   */
  public static final Coarbitrary<StringBuilder> coarbStringBuilder = new Coarbitrary<StringBuilder>() {
    public <B> Gen<B> coarbitrary(final StringBuilder s, final Gen<B> g) {
      return coarbString.coarbitrary(s.toString(), g);
    }
  };

  /**
   * A coarbitrary for streams.
   *
   * @param ca A coarbitrary for the elements of the stream.
   * @return A coarbitrary for streams.
   */
  public static <A> Coarbitrary<Stream<A>> coarbStream(final Coarbitrary<A> ca) {
    return new Coarbitrary<Stream<A>>() {
      public <B> Gen<B> coarbitrary(final Stream<A> as, final Gen<B> g) {
        return as.isEmpty() ?
            variant(0, g) :
            variant(1, ca.coarbitrary(as.head(), coarbitrary(as.tail()._1(), g)));
      }
    };
  }

  /**
   * A coarbitrary for arrays.
   *
   * @param ca A coarbitrary for the elements of the array.
   * @return A coarbitrary for arrays.
   */
  public static <A> Coarbitrary<Array<A>> coarbArray(final Coarbitrary<A> ca) {
    return new Coarbitrary<Array<A>>() {
      public <B> Gen<B> coarbitrary(final Array<A> as, final Gen<B> g) {
        return coarbList(ca).coarbitrary(as.toList(), g);
      }
    };
  }

  /**
   * A coarbitrary for throwables.
   *
   * @param cs A coarbitrary for the throwable message.
   * @return A coarbitrary for throwables.
   */
  public static Coarbitrary<Throwable> coarbThrowable(final Coarbitrary<String> cs) {
    return cs.comap(new F<Throwable, String>() {
      public String f(final Throwable t) {
        return t.getMessage();
      }
    });
  }

  /**
   * A coarbitrary for throwables.
   */
  public static final Coarbitrary<Throwable> coarbThrowable =
      coarbThrowable(coarbString);

  // BEGIN java.util

  /**
   * A coarbitrary for array lists.
   *
   * @param ca A coarbitrary for the elements of the array list.
   * @return A coarbitrary for array lists.
   */
  public static <A> Coarbitrary<ArrayList<A>> coarbArrayList(final Coarbitrary<A> ca) {
    return new Coarbitrary<ArrayList<A>>() {
      @SuppressWarnings({"unchecked"})
      public <B> Gen<B> coarbitrary(final ArrayList<A> as, final Gen<B> g) {
        return coarbArray(ca).coarbitrary(array(as.toArray((A[]) new Object[as.size()])), g);
      }
    };
  }

  /**
   * A coarbitrary for dates.
   */
  public static final Coarbitrary<Date> coarbDate = new Coarbitrary<Date>() {
    public <B> Gen<B> coarbitrary(final Date d, final Gen<B> g) {
      return coarbLong.coarbitrary(d.getTime(), g);
    }
  };

  /**
   * A coarbitrary for enum maps.
   *
   * @param ck A coarbitrary for the map keys.
   * @param cv A coarbitrary for the map values.
   * @return A coarbitrary for enum maps.
   */
  public static <K extends Enum<K>, V> Coarbitrary<EnumMap<K, V>> coarbEnumMap(final Coarbitrary<K> ck,
                                                                               final Coarbitrary<V> cv) {
    return new Coarbitrary<EnumMap<K, V>>() {
      @SuppressWarnings({"UseOfObsoleteCollectionType"})
      public <B> Gen<B> coarbitrary(final EnumMap<K, V> m, final Gen<B> g) {
        return coarbHashMap(ck, cv).coarbitrary(new HashMap<K, V>(m), g);
      }
    };
  }

  /**
   * A coarbitrary for enum sets.
   *
   * @param c A coarbitrary for the elements of the enum set.
   * @return A coarbitrary for enum sets.
   */
  public static <A extends Enum<A>> Coarbitrary<EnumSet<A>> coarbEnumSet(final Coarbitrary<A> c) {
    return new Coarbitrary<EnumSet<A>>() {
      @SuppressWarnings({"unchecked"})
      public <B> Gen<B> coarbitrary(final EnumSet<A> as, final Gen<B> g) {
        return coarbHashSet(c).coarbitrary(new HashSet<A>(as), g);
      }
    };
  }

  /**
   * A coarbitrary for hash maps.
   *
   * @param ck A coarbitrary for the map keys.
   * @param cv A coarbitrary for the map values.
   * @return A coarbitrary for hash maps.
   */
  public static <K, V> Coarbitrary<HashMap<K, V>> coarbHashMap(final Coarbitrary<K> ck, final Coarbitrary<V> cv) {
    return new Coarbitrary<HashMap<K, V>>() {
      public <B> Gen<B> coarbitrary(final HashMap<K, V> h, final Gen<B> g) {
        List<P2<K, V>> x = nil();

        for (final K k : h.keySet()) {
          x = x.snoc(p(k, h.get(k)));
        }

        return coarbList(coarbP2(ck, cv)).coarbitrary(x, g);
      }
    };
  }

  /**
   * A coarbitrary for hash sets.
   *
   * @param c A coarbitrary for the elements of the hash set.
   * @return A coarbitrary for hash sets.
   */
  public static <A> Coarbitrary<HashSet<A>> coarbHashSet(final Coarbitrary<A> c) {
    return new Coarbitrary<HashSet<A>>() {
      @SuppressWarnings({"unchecked"})
      public <B> Gen<B> coarbitrary(final HashSet<A> as, final Gen<B> g) {
        return coarbArray(c).coarbitrary(array(as.toArray((A[]) new Object[as.size()])), g);
      }
    };
  }

  /**
   * A coarbitrary for identity hash maps.
   *
   * @param ck A coarbitrary for the map keys.
   * @param cv A coarbitrary for the map values.
   * @return A coarbitrary for identity hash maps.
   */
  public static <K, V> Coarbitrary<IdentityHashMap<K, V>> coarbIdentityHashMap(final Coarbitrary<K> ck,
                                                                               final Coarbitrary<V> cv) {
    return new Coarbitrary<IdentityHashMap<K, V>>() {
      @SuppressWarnings({"UseOfObsoleteCollectionType"})
      public <B> Gen<B> coarbitrary(final IdentityHashMap<K, V> m, final Gen<B> g) {
        return coarbHashMap(ck, cv).coarbitrary(new HashMap<K, V>(m), g);
      }
    };
  }

  /**
   * A coarbitrary for linked hash maps.
   *
   * @param ck A coarbitrary for the map keys.
   * @param cv A coarbitrary for the map values.
   * @return A coarbitrary for linked hash maps.
   */
  public static <K, V> Coarbitrary<LinkedHashMap<K, V>> coarbLinkedHashMap(final Coarbitrary<K> ck,
                                                                           final Coarbitrary<V> cv) {
    return new Coarbitrary<LinkedHashMap<K, V>>() {
      @SuppressWarnings({"UseOfObsoleteCollectionType"})
      public <B> Gen<B> coarbitrary(final LinkedHashMap<K, V> m, final Gen<B> g) {
        return coarbHashMap(ck, cv).coarbitrary(new HashMap<K, V>(m), g);
      }
    };
  }

  /**
   * A coarbitrary for linked hash sets.
   *
   * @param c A coarbitrary for the elements of the linked hash set.
   * @return A coarbitrary for linked hash sets.
   */
  public static <A> Coarbitrary<LinkedHashSet<A>> coarbLinkedHashSet(final Coarbitrary<A> c) {
    return new Coarbitrary<LinkedHashSet<A>>() {
      @SuppressWarnings({"unchecked"})
      public <B> Gen<B> coarbitrary(final LinkedHashSet<A> as, final Gen<B> g) {
        return coarbHashSet(c).coarbitrary(new HashSet<A>(as), g);
      }
    };
  }

  /**
   * A coarbitrary for linked lists.
   *
   * @param c A coarbitrary for the elements of the linked list.
   * @return A coarbitrary for linked lists.
   */
  public static <A> Coarbitrary<LinkedList<A>> coarbLinkedList(final Coarbitrary<A> c) {
    return new Coarbitrary<LinkedList<A>>() {
      @SuppressWarnings({"unchecked"})
      public <B> Gen<B> coarbitrary(final LinkedList<A> as, final Gen<B> g) {
        return coarbArray(c).coarbitrary(array(as.toArray((A[]) new Object[as.size()])), g);
      }
    };
  }

  /**
   * A coarbitrary for priority queues.
   *
   * @param c A coarbitrary for the elements of the priority queue.
   * @return A coarbitrary for priority queues.
   */
  public static <A> Coarbitrary<PriorityQueue<A>> coarbPriorityQueue(final Coarbitrary<A> c) {
    return new Coarbitrary<PriorityQueue<A>>() {
      @SuppressWarnings({"unchecked"})
      public <B> Gen<B> coarbitrary(final PriorityQueue<A> as, final Gen<B> g) {
        return coarbArray(c).coarbitrary(array(as.toArray((A[]) new Object[as.size()])), g);
      }
    };
  }

  /**
   * A coarbitrary for stacks.
   *
   * @param c A coarbitrary for the elements of the stack.
   * @return A coarbitrary for stacks.
   */
  public static <A> Coarbitrary<Stack<A>> coarbStack(final Coarbitrary<A> c) {
    return new Coarbitrary<Stack<A>>() {
      @SuppressWarnings({"unchecked"})
      public <B> Gen<B> coarbitrary(final Stack<A> as, final Gen<B> g) {
        return coarbArray(c).coarbitrary(array(as.toArray((A[]) new Object[as.size()])), g);
      }
    };
  }

  /**
   * A coarbitrary for tree maps.
   *
   * @param ck A coarbitrary for the map keys.
   * @param cv A coarbitrary for the map values.
   * @return A coarbitrary for tree maps.
   */
  public static <K, V> Coarbitrary<TreeMap<K, V>> coarbTreeMap(final Coarbitrary<K> ck, final Coarbitrary<V> cv) {
    return new Coarbitrary<TreeMap<K, V>>() {
      @SuppressWarnings({"UseOfObsoleteCollectionType"})
      public <B> Gen<B> coarbitrary(final TreeMap<K, V> m, final Gen<B> g) {
        return coarbHashMap(ck, cv).coarbitrary(new HashMap<K, V>(m), g);
      }
    };
  }

  /**
   * A coarbitrary for tree sets.
   *
   * @param c A coarbitrary for the elements of the tree set.
   * @return A coarbitrary for tree sets.
   */
  public static <A> Coarbitrary<TreeSet<A>> coarbTreeSet(final Coarbitrary<A> c) {
    return new Coarbitrary<TreeSet<A>>() {
      @SuppressWarnings({"unchecked"})
      public <B> Gen<B> coarbitrary(final TreeSet<A> as, final Gen<B> g) {
        return coarbHashSet(c).coarbitrary(new HashSet<A>(as), g);
      }
    };
  }

  /**
   * A coarbitrary for vectors.
   *
   * @param c A coarbitrary for the elements of the vector.
   * @return A coarbitrary for vectors.
   */
  public static <A> Coarbitrary<Vector<A>> coarbVector(final Coarbitrary<A> c) {
    return new Coarbitrary<Vector<A>>() {
      @SuppressWarnings({"unchecked", "UseOfObsoleteCollectionType"})
      public <B> Gen<B> coarbitrary(final Vector<A> as, final Gen<B> g) {
        return coarbArray(c).coarbitrary(array(as.toArray((A[]) new Object[as.size()])), g);
      }
    };
  }

  // END java.util

  // BEGIN java.sql

  public static final Coarbitrary<java.sql.Date> coarbSQLDate = new Coarbitrary<java.sql.Date>() {
    public <B> Gen<B> coarbitrary(final java.sql.Date d, final Gen<B> g) {
      return coarbLong.coarbitrary(d.getTime(), g);
    }
  };

  public static final Coarbitrary<Timestamp> coarbTimestamp = new Coarbitrary<Timestamp>() {
    public <B> Gen<B> coarbitrary(final Timestamp t, final Gen<B> g) {
      return coarbLong.coarbitrary(t.getTime(), g);
    }
  };

  public static final Coarbitrary<Time> coarbTime = new Coarbitrary<Time>() {
    public <B> Gen<B> coarbitrary(final Time t, final Gen<B> g) {
      return coarbLong.coarbitrary(t.getTime(), g);
    }
  };

  // END java.sql

  /**
   * A coarbitrary for product-1 values.
   *
   * @param ca A coarbitrary for one of the types over which the product-1 is defined.
   * @return A coarbitrary for product-1 values.
   */
  public static <A> Coarbitrary<P1<A>> coarbP1(final Coarbitrary<A> ca) {
    return new Coarbitrary<P1<A>>() {
      public <B> Gen<B> coarbitrary(final P1<A> p, final Gen<B> g) {
        return ca.coarbitrary(p._1(), g);
      }
    };
  }

  /**
   * A coarbitrary for product-2 values.
   *
   * @param ca A coarbitrary for one of the types over which the product-2 is defined.
   * @param cb A coarbitrary for one of the types over which the product-2 is defined.
   * @return A coarbitrary for product-2 values.
   */
  public static <A, B> Coarbitrary<P2<A, B>> coarbP2(final Coarbitrary<A> ca, final Coarbitrary<B> cb) {
    return new Coarbitrary<P2<A, B>>() {
      public <X> Gen<X> coarbitrary(final P2<A, B> p, final Gen<X> g) {
        return ca.coarbitrary(p._1(), cb.coarbitrary(p._2(), g));
      }
    };
  }

  /**
   * A coarbitrary for product-3 values.
   *
   * @param ca A coarbitrary for one of the types over which the product-3 is defined.
   * @param cb A coarbitrary for one of the types over which the product-3 is defined.
   * @param cc A coarbitrary for one of the types over which the product-3 is defined.
   * @return A coarbitrary for product-3 values.
   */
  public static <A, B, C> Coarbitrary<P3<A, B, C>> coarbP3(final Coarbitrary<A> ca, final Coarbitrary<B> cb,
                                                           final Coarbitrary<C> cc) {
    return new Coarbitrary<P3<A, B, C>>() {
      public <X> Gen<X> coarbitrary(final P3<A, B, C> p, final Gen<X> g) {
        return ca.coarbitrary(p._1(), cb.coarbitrary(p._2(), cc.coarbitrary(p._3(), g)));
      }
    };
  }

  /**
   * A coarbitrary for product-4 values.
   *
   * @param ca A coarbitrary for one of the types over which the product-4 is defined.
   * @param cb A coarbitrary for one of the types over which the product-4 is defined.
   * @param cc A coarbitrary for one of the types over which the product-4 is defined.
   * @param cd A coarbitrary for one of the types over which the product-4 is defined.
   * @return A coarbitrary for product-4 values.
   */
  public static <A, B, C, D> Coarbitrary<P4<A, B, C, D>> coarbP4(final Coarbitrary<A> ca, final Coarbitrary<B> cb,
                                                                 final Coarbitrary<C> cc, final Coarbitrary<D> cd) {
    return new Coarbitrary<P4<A, B, C, D>>() {
      public <X> Gen<X> coarbitrary(final P4<A, B, C, D> p, final Gen<X> g) {
        return ca.coarbitrary(p._1(), cb.coarbitrary(p._2(), cc.coarbitrary(p._3(), cd.coarbitrary(p._4(), g))));
      }
    };
  }

  /**
   * A coarbitrary for product-5 values.
   *
   * @param ca A coarbitrary for one of the types over which the product-5 is defined.
   * @param cb A coarbitrary for one of the types over which the product-5 is defined.
   * @param cc A coarbitrary for one of the types over which the product-5 is defined.
   * @param cd A coarbitrary for one of the types over which the product-5 is defined.
   * @param ce A coarbitrary for one of the types over which the product-5 is defined.
   * @return A coarbitrary for product-5 values.
   */
  public static <A, B, C, D, E> Coarbitrary<P5<A, B, C, D, E>> coarbP5(final Coarbitrary<A> ca, final Coarbitrary<B> cb,
                                                                       final Coarbitrary<C> cc, final Coarbitrary<D> cd,
                                                                       final Coarbitrary<E> ce) {
    return new Coarbitrary<P5<A, B, C, D, E>>() {
      public <X> Gen<X> coarbitrary(final P5<A, B, C, D, E> p, final Gen<X> g) {
        return ca.coarbitrary(p._1(),
            cb.coarbitrary(p._2(), cc.coarbitrary(p._3(), cd.coarbitrary(p._4(), ce.coarbitrary(p._5(), g)))));
      }
    };
  }

  /**
   * A coarbitrary for product-6 values.
   *
   * @param ca A coarbitrary for one of the types over which the product-6 is defined.
   * @param cb A coarbitrary for one of the types over which the product-6 is defined.
   * @param cc A coarbitrary for one of the types over which the product-6 is defined.
   * @param cd A coarbitrary for one of the types over which the product-6 is defined.
   * @param ce A coarbitrary for one of the types over which the product-6 is defined.
   * @param cf A coarbitrary for one of the types over which the product-6 is defined.
   * @return A coarbitrary for product-6 values.
   */
  public static <A, B, C, D, E, F$> Coarbitrary<P6<A, B, C, D, E, F$>> coarbP6(final Coarbitrary<A> ca,
                                                                               final Coarbitrary<B> cb,
                                                                               final Coarbitrary<C> cc,
                                                                               final Coarbitrary<D> cd,
                                                                               final Coarbitrary<E> ce,
                                                                               final Coarbitrary<F$> cf) {
    return new Coarbitrary<P6<A, B, C, D, E, F$>>() {
      public <X> Gen<X> coarbitrary(final P6<A, B, C, D, E, F$> p, final Gen<X> g) {
        return ca.coarbitrary(p._1(), cb.coarbitrary(p._2(),
            cc.coarbitrary(p._3(), cd.coarbitrary(p._4(), ce.coarbitrary(p._5(), cf.coarbitrary(p._6(), g))))));
      }
    };
  }

  /**
   * A coarbitrary for product-7 values.
   *
   * @param ca A coarbitrary for one of the types over which the product-7 is defined.
   * @param cb A coarbitrary for one of the types over which the product-7 is defined.
   * @param cc A coarbitrary for one of the types over which the product-7 is defined.
   * @param cd A coarbitrary for one of the types over which the product-7 is defined.
   * @param ce A coarbitrary for one of the types over which the product-7 is defined.
   * @param cf A coarbitrary for one of the types over which the product-7 is defined.
   * @param cg A coarbitrary for one of the types over which the product-7 is defined.
   * @return A coarbitrary for product-7 values.
   */
  public static <A, B, C, D, E, F$, G> Coarbitrary<P7<A, B, C, D, E, F$, G>> coarbP7(final Coarbitrary<A> ca,
                                                                                     final Coarbitrary<B> cb,
                                                                                     final Coarbitrary<C> cc,
                                                                                     final Coarbitrary<D> cd,
                                                                                     final Coarbitrary<E> ce,
                                                                                     final Coarbitrary<F$> cf,
                                                                                     final Coarbitrary<G> cg) {
    return new Coarbitrary<P7<A, B, C, D, E, F$, G>>() {
      public <X> Gen<X> coarbitrary(final P7<A, B, C, D, E, F$, G> p, final Gen<X> g) {
        return ca.coarbitrary(p._1(), cb.coarbitrary(p._2(), cc.coarbitrary(p._3(),
            cd.coarbitrary(p._4(), ce.coarbitrary(p._5(), cf.coarbitrary(p._6(), cg.coarbitrary(p._7(), g)))))));
      }
    };
  }

  /**
   * A coarbitrary for product-8 values.
   *
   * @param ca A coarbitrary for one of the types over which the product-8 is defined.
   * @param cb A coarbitrary for one of the types over which the product-8 is defined.
   * @param cc A coarbitrary for one of the types over which the product-8 is defined.
   * @param cd A coarbitrary for one of the types over which the product-8 is defined.
   * @param ce A coarbitrary for one of the types over which the product-8 is defined.
   * @param cf A coarbitrary for one of the types over which the product-8 is defined.
   * @param cg A coarbitrary for one of the types over which the product-8 is defined.
   * @param ch A coarbitrary for one of the types over which the product-8 is defined.
   * @return A coarbitrary for product-8 values.
   */
  public static <A, B, C, D, E, F$, G, H> Coarbitrary<P8<A, B, C, D, E, F$, G, H>> coarbP8(final Coarbitrary<A> ca,
                                                                                           final Coarbitrary<B> cb,
                                                                                           final Coarbitrary<C> cc,
                                                                                           final Coarbitrary<D> cd,
                                                                                           final Coarbitrary<E> ce,
                                                                                           final Coarbitrary<F$> cf,
                                                                                           final Coarbitrary<G> cg,
                                                                                           final Coarbitrary<H> ch) {
    return new Coarbitrary<P8<A, B, C, D, E, F$, G, H>>() {
      public <X> Gen<X> coarbitrary(final P8<A, B, C, D, E, F$, G, H> p, final Gen<X> g) {
        return ca.coarbitrary(p._1(), cb.coarbitrary(p._2(), cc.coarbitrary(p._3(), cd.coarbitrary(p._4(),
            ce.coarbitrary(p._5(), cf.coarbitrary(p._6(), cg.coarbitrary(p._7(), ch.coarbitrary(p._8(), g))))))));
      }
    };
  }
}
