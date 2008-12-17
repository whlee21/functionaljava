package fj.data.relation;

import fj.F;
import fj.F2;
import static fj.Function.flip;
import fj.P;
import fj.P2;
import fj.data.Set;
import fj.data.TreeMap;
import static fj.data.relation.R1.r1$;
import fj.pre.Ord;

import java.util.Iterator;

/**
 * A position-indexed binary relation, or a bi-directional map.
 * Represents the extension of a binary predicate.
 */
public class R2<A, B> implements Iterable<P2<A, B>> {

  private final Set<P2<A, B>> body;
  private final TreeMap<A, Set<B>> map1;
  private final TreeMap<B, Set<A>> map2;

  private R2(final Set<P2<A, B>> s) {
    body = s;
    TreeMap<A, Set<B>> map1 = TreeMap.empty(ord1());
    TreeMap<B, Set<A>> map2 = TreeMap.empty(ord2());

    for (final P2<A, B> e : body) {
      map1 = map1.set(e._1(), map1.get(e._1()).orSome(Set.single(ord2(), e._2())));
      map2 = map2.set(e._2(), map2.get(e._2()).orSome(Set.single(ord1(), e._1())));
    }

    this.map1 = map1;
    this.map2 = map2;
  }

  private Ord<A> ord1() {
    return body.ord().comap(flip(P.<A, B>p2()).f(null));
  }

  private Ord<B> ord2() {
    return body.ord().comap(P.<A, B>p2().f(null));
  }

  public static <A, B> R2<A, B> r2(final Set<P2<A, B>> s) {
    return new R2<A, B>(s);
  }

  public R1<A> project1() {
    return r1$(body.map(ord1(), P2.<A, B>__1()));
  }

  public R1<B> project2() {
    return r1$(body.map(ord2(), P2.<A, B>__2()));
  }

  public R2<A, B> select(final F<P2<A, B>, Boolean> p) {
    return r2(body.filter(p));
  }

  public R2<A, B> select(final F2<A, B, Boolean> p) {
    return r2(body.filter(P2.tuple(p)));
  }

  public R2<A, B> selectBy1(final A a) {
    return r2(map1.get(a).orSome(Set.<B>empty(ord2())).map(body.ord(), P.<A, B>p2().f(a)));
  }

  public R2<A, B> selectBy2(final B b) {
    return r2(map2.get(b).orSome(Set.<A>empty(ord1())).map(body.ord(), flip(P.<A, B>p2()).f(b)));
  }

  public Iterator<P2<A, B>> iterator() {
    return body.iterator();
  }

}
