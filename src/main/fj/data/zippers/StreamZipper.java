package fj.data.zippers;

import fj.data.Stream;
import fj.data.Option;
import static fj.data.Option.some;
import static fj.data.Option.none;
import fj.pre.Ord;
import fj.pre.Equal;
import fj.pre.Show;
import fj.*;
import static fj.Function.flip;
import static fj.Function.compose;

public class StreamZipper<A> {
  private final Stream<A> left;
  private final A a;
  private final Stream<A> right;

  private StreamZipper(final Stream<A> left, final A hole, final Stream<A> right) {
    this.left = left;
    a = hole;
    this.right = right;
  }

  public static <A> StreamZipper<A> streamZipper(final Stream<A> left, final A hole, final Stream<A> right) {
    return new StreamZipper<A>(left, hole, right);
  }

  public static <A> StreamZipper<A> streamZipper(final Stream<A> left, final P1<A> hole, final Stream<A> right) {
    return new StreamZipper<A>(left, hole._1(), right);
  }

  public P3<Stream<A>, A, Stream<A>> p() {
    return P.p(left, a, right);
  }

  public static <A> F<StreamZipper<A>, P3<Stream<A>, A, Stream<A>>> p_() {
    return new F<StreamZipper<A>, P3<Stream<A>, A, Stream<A>>>() {
      public P3<Stream<A>, A, Stream<A>> f(final StreamZipper<A> a) {
        return a.p();
      }
    };
  }

  public static <A> Ord<StreamZipper<A>> ord(final Ord<A> o) {
    final Ord<Stream<A>> so = Ord.streamOrd(o);
    return Ord.p3Ord(so, o, so).comap(StreamZipper.<A>p_());
  }

  public static <A> Equal<StreamZipper<A>> eq(final Equal<A> e) {
    final Equal<Stream<A>> se = Equal.streamEqual(e);
    return Equal.p3Equal(se, e, se).comap(StreamZipper.<A>p_());
  }

  public static <A> Show<StreamZipper<A>> show(final Show<A> s) {
    final Show<Stream<A>> ss = Show.streamShow(s);
    return Show.p3Show(ss, s, ss).comap(StreamZipper.<A>p_());
  }

  public <B> StreamZipper<B> map(final F<A, B> f) {
    return streamZipper(left.map(f), f.f(a), right.map(f));
  }

  public <B> B foldRight(final F<A, F<B, B>> f, final B z) {
    return left.foldLeft(flip(f),
        right.cons(a).foldRight(compose(
            Function.<P1<B>, B, B>andThen().f(P1.<B>__1()), f), z));
  }

  public static <A> StreamZipper<A> single(final A a) {
    return streamZipper(Stream.<A>nil(), a, Stream.<A>nil());
  }

  public static <A> Option<StreamZipper<A>> fromStream(final Stream<A> a) {
    if (a.isEmpty())
      return none();
    else
      return some(streamZipper(Stream.<A>nil(), a.head(), a.tail()._1()));
  }

  public static <A> Option<StreamZipper<A>> fromStreamEnd(final Stream<A> a) {
    if (a.isEmpty())
      return none();
    else
      return some(streamZipper(a.tail()._1(), a.head(), Stream.<A>nil()));
  }

  public A focus() {
    return a;
  }

  public Option<StreamZipper<A>> next() {
    if (right.isEmpty())
      return none();
    else
      return some(tryNext());
  }

  public StreamZipper<A> tryNext() {
    if (right.isEmpty())
      throw new Error("Tried next at the end of a zipper.");
    else
      return streamZipper(left.cons(a), right.head(), right.tail()._1());
  }

  public Option<StreamZipper<A>> previous() {
    if (right.isEmpty())
      return none();
    else
      return some(tryPrevious());
  }

  public StreamZipper<A> tryPrevious() {
    if (left.isEmpty())
      throw new Error("Tried previous at the beginning of a zipper.");
    else
      return streamZipper(left.tail()._1(), left.head(), right.cons(a));
  }

}
