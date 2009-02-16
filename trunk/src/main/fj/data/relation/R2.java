package fj.data.relation;

import fj.F;
import fj.F2;
import static fj.Function.compose2;
import static fj.Function.flip;
import fj.P;
import fj.P2;
import fj.data.Set;
import fj.data.TreeMap;
import static fj.data.relation.R1.r1$;
import fj.pre.Monoid;
import fj.pre.Ord;
import static fj.pre.Ord.p2Ord;
import fj.pre.Semigroup;

import java.util.Iterator;

/**
 * A position-indexed binary relation, or a bi-directional map.
 * Represents the extension of a binary predicate.
 */
public class R2<A, B> implements Iterable<P2<A, B>> {

  private final Set<P2<A, B>> body;
  private final TreeMap<A, Set<B>> map1;
  private final TreeMap<B, Set<A>> map2;
  private final Ord<A> orda;
  private final Ord<B> ordb;

  private R2(final Set<P2<A, B>> s, final Ord<A> oa, final Ord<B> ob) {
    body = s;
    orda = oa;
    ordb = ob;
    TreeMap<A, Set<B>> map1 = TreeMap.empty(orda);
    TreeMap<B, Set<A>> map2 = TreeMap.empty(ordb);

    for (final P2<A, B> e : body) {
      map1 = map1.set(e._1(), map1.get(e._1()).orSome(Set.single(ordb, e._2())));
      map2 = map2.set(e._2(), map2.get(e._2()).orSome(Set.single(orda, e._1())));
    }

    this.map1 = map1;
    this.map2 = map2;
  }

  private R2<A, B> r2(final Set<P2<A, B>> s) {
    return r2(s, orda, ordb);
  }

  public static <A, B> R2<A, B> r2(final Set<P2<A, B>> s, final Ord<A> oa, final Ord<B> ob) {
    return new R2<A, B>(s, oa, ob);
  }

  public R1<A> project1() {
    return r1$(body.map(orda, P2.<A, B>__1()));
  }

  public R1<B> project2() {
    return r1$(body.map(ordb, P2.<A, B>__2()));
  }

  public R2<A, B> select(final F<P2<A, B>, Boolean> p) {
    return r2(body.filter(p));
  }

  public R2<A, B> select(final F2<A, B, Boolean> p) {
    return r2(body.filter(P2.tuple(p)));
  }

  public R2<A, B> selectBy1(final A a) {
    return r2(map1.get(a).orSome(Set.<B>empty(ordb)).map(body.ord(), P.<A, B>p2().f(a)));
  }

  public R2<A, B> selectBy2(final B b) {
    return r2(map2.get(b).orSome(Set.<A>empty(orda)).map(body.ord(), flip(P.<A, B>p2()).f(b)));
  }

  public R2<A, B> union(final R2<A, B> r) {
    return r2(body.union(r.body));
  }

  public static <A, B> F2<R2<A, B>, R2<A, B>, R2<A, B>> union() {
    return new F2<R2<A, B>, R2<A, B>, R2<A, B>>() {
      public R2<A, B> f(final R2<A, B> r1, final R2<A, B> r2) {
        return r1.union(r2);
      }
    };
  }

  public static <A, B> R2<A, B> empty(final Ord<A> oa, final Ord<B> ob) {
    return r2(Set.empty(p2Ord(oa, ob)), oa, ob);
  }

  public static <A, B> Semigroup<R2<A, B>> r2Semigroup() {
    return Semigroup.semigroup(R2.<A, B>union());
  }

  public static <A, B> Monoid<R2<A, B>> r2Monoid(final Ord<A> oa, final Ord<B> ob) {
    return Monoid.monoid(R2.<A, B>r2Semigroup(), empty(oa, ob));
  }

  public Set<P2<A, B>> toSet() {
    return body;
  }

  public Iterator<P2<A, B>> iterator() {
    return body.iterator();
  }

  public F<A, F<B, Boolean>> toPredicate() {
    return compose2(Set.<P2<A, B>>member().f(body), P.<A, B>p2());
  }

  /**
   * Inserts the given values, as a tuple, to this relation.
   *
   * @param a The first value of the tuple.
   * @param b The second value of the tuple.
   * @return a new relation with the addition of the given tuple.
   */
  public R2<A, B> insert(final A a, final B b) {
    return union(r2(Set.single(Ord.p2Ord(orda, ordb), P.p(a, b))));
  }

}
