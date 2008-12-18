package fj.data.relation;

import fj.P1;
import fj.P;
import fj.F;
import static fj.Function.compose;
import fj.pre.Ord;
import fj.data.Set;
import fj.data.IterableW;

import java.util.Iterator;

/**
 * A position-indexed unary relation.
 */
public class R1<A> implements Iterable<A> {

  private Set<P1<A>> body;

  private R1(final Set<P1<A>> s) {
    body = s;
  }

  public static <A> R1<A> r1(final Set<P1<A>> s) {
    return new R1<A>(s);
  }

  public static <A> R1<A> r1$(final Set<A> s) {
    return new R1<A>(s.map(Ord.p1Ord(s.ord()), P.<A>p1()));
  }

  public R1<A> project1() {
    return this;
  }

  public R1<A> select(final F<A, Boolean> p) {
    return new R1<A>(body.filter(compose(p, P1.<A>__1())));
  }

  public Iterator<A> iterator() {
    return IterableW.wrap(body).map(P1.<A>__1()).iterator();
  }

  public Set<A> toSet() {
    return body.map(body.ord().comap(P.<A>p1()), P1.<A>__1());
  }

  public Set<P1<A>> body() {
    return body;
  }
  
  public F<A, Boolean> toPredicate() {
    return compose(Set.<P1<A>>member().f(body), P.<A>p1());
  }

}
