package fj.data.relation;

import fj.*;
import static fj.Function.flip;
import fj.data.Set;
import fj.data.TreeMap;
import static fj.data.relation.R1.r1$;
import static fj.data.relation.R2.r2;
import fj.pre.Monoid;
import fj.pre.Ord;
import fj.pre.Semigroup;

import java.util.Iterator;

/**
 * A position-indexed ternary relation, or a tri-directional map.
 * Represents the extension of a ternary predicate.
 */
public final class R3<A, B, C> implements Iterable<P3<A, B, C>> {

  private final Set<P3<A, B, C>> body;
  private final TreeMap<A, R2<B, C>> map1;
  private final TreeMap<B, R2<A, C>> map2;
  private final TreeMap<C, R2<A, B>> map3;

  private R3(final Set<P3<A, B, C>> s) {
    body = s;
    TreeMap<A, R2<B, C>> map1 = TreeMap.empty(orda());
    TreeMap<B, R2<A, C>> map2 = TreeMap.empty(ordb());
    TreeMap<C, R2<A, B>> map3 = TreeMap.empty(ordc());

    for (final P3<A, B, C> e : body) {
      map1 = map1.set(e._1(), map1.get(e._1()).orSome(r2(Set.single(ordbc(), P.<B, C>p(e._2(), e._3())))));
      map2 = map2.set(e._2(), map2.get(e._2()).orSome(r2(Set.single(ordac(), P.<A, C>p(e._1(), e._3())))));
      map3 = map3.set(e._3(), map3.get(e._3()).orSome(r2(Set.single(ordab(), P.<A, B>p(e._1(), e._2())))));
    }

    this.map1 = map1;
    this.map2 = map2;
    this.map3 = map3;
  }

  private Ord<A> orda() {
    return body.ord().comap(flip(flip(P.<A, B, C>p3()).f(null)).f(null));
  }

  private Ord<B> ordb() {
    return body.ord().comap(flip(P.<A, B, C>p3().f(null)).f(null));
  }

  private Ord<C> ordc() {
    return body.ord().comap(P.<A, B, C>p3().f(null).f(null));
  }

  private Ord<P2<A, B>> ordab() {
    return Ord.p2Ord(orda(), ordb());
  }

  private Ord<P2<A, C>> ordac() {
    return Ord.p2Ord(orda(), ordc());
  }

  private Ord<P2<B, C>> ordbc() {
    return Ord.p2Ord(ordb(), ordc());
  }

  public static <A, B, C> R3<A, B, C> r3(final Set<P3<A, B, C>> s) {
    return new R3<A, B, C>(s);
  }

  public R1<A> project1() {
    return r1$(body.map(orda(), P3.<A, B, C>__1()));
  }

  public R1<B> project2() {
    return r1$(body.map(ordb(), P3.<A, B, C>__2()));
  }

  public R1<C> project3() {
    return r1$(body.map(ordc(), P3.<A, B, C>__3()));
  }

  public R2<B, C> projectBut1() {
    return r2(Set.iterableSet(ordbc(), R2.r2Monoid(ordbc()).sumLeft(map1.values())));
  }

  public R2<A, C> projectBut2() {
    return r2(Set.iterableSet(ordac(), R2.r2Monoid(ordac()).sumLeft(map2.values())));
  }

  public R2<A, B> projectBut3() {
    return r2(Set.iterableSet(ordab(), R2.r2Monoid(ordab()).sumLeft(map3.values())));
  }

  public R3<A, B, C> selectBy1(final A a) {
    return r3(map1.get(a).orSome(r2(Set.<P2<B, C>>empty(ordbc()))).toSet().map(
        body.ord(), new F<P2<B, C>, P3<A, B, C>>() {
          public P3<A, B, C> f(final P2<B, C> p) {
            return P.p(a, p._1(), p._2());
          }
        }));
  }

  public R3<A, B, C> selectBy2(final B b) {
    return r3(map2.get(b).orSome(r2(Set.<P2<A, C>>empty(ordac()))).toSet().map(
        body.ord(), new F<P2<A, C>, P3<A, B, C>>() {
          public P3<A, B, C> f(final P2<A, C> p) {
            return P.p(p._1(), b, p._2());
          }
        }));
  }

  public R3<A, B, C> selectBy3(final C c) {
    return r3(map3.get(c).orSome(r2(Set.<P2<A, B>>empty(ordab()))).toSet().map(
        body.ord(), new F<P2<A, B>, P3<A, B, C>>() {
          public P3<A, B, C> f(final P2<A, B> p) {
            return P.p(p._1(), p._2(), c);
          }
        }));
  }

  public R3<A, B, C> union(final R3<A, B, C> r) {
    return r3(body.union(r.body));
  }

  public static <A, B, C> F2<R3<A, B, C>, R3<A, B, C>, R3<A, B, C>> union() {
    return new F2<R3<A, B, C>, R3<A, B, C>, R3<A, B, C>>() {
      public R3<A, B, C> f(final R3<A, B, C> r1, final R3<A, B, C> r2) {
        return r1.union(r2);
      }
    };
  }

  public static <A, B, C> Semigroup<R3<A, B, C>> r3Semigroup() {
    return Semigroup.semigroup(R3.<A, B, C>union());
  }

  public static <A, B, C> Monoid<R3<A, B, C>> r2Monoid(Ord<P3<A, B, C>> o) {
    return Monoid.monoid(R3.<A, B, C>r3Semigroup(), r3(Set.empty(o)));
  }

  public Set<P3<A, B, C>> toSet() {
    return body;
  }

  public Iterator<P3<A, B, C>> iterator() {
    return body.iterator();
  }

}