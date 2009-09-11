package fj.data.fingertrees;

import fj.F;
import fj.Function;
import static fj.Function.flip;

/**
 * A finger tree with 1-4-digits on the left and right, and a finger tree of 2-3-nodes in the middle.
 */
public final class Deep<V, A> extends FingerTree<V, A> {
  private final V v;
  private final Digit<V, A> prefix;
  private final FingerTree<V, Node<V, A>> middle;
  private final Digit<V, A> suffix;

  Deep(final Measured<V, A> m, final V v, final Digit<V, A> prefix,
       final FingerTree<V, Node<V, A>> middle,
       final Digit<V, A> suffix) {
    super(m);
    this.v = v;
    this.prefix = prefix;
    this.middle = middle;
    this.suffix = suffix;
  }

  /**
   * Returns the first few elements of this tree.
   *
   * @return the first few elements of this tree.
   */
  public Digit<V, A> prefix() {
    return prefix;
  }

  /**
   * Returns a finger tree of the inner nodes of this tree.
   *
   * @return a finger tree of the inner nodes of this tree.
   */
  public FingerTree<V, Node<V, A>> middle() {
    return middle;
  }

  /**
   * Returns the last few elements of this tree.
   *
   * @return the last few elements of this tree.
   */
  public Digit<V, A> suffix() {
    return suffix;
  }

  /**
   * @see fj.data.fingertrees.FingerTree#foldRight(fj.F, Object)
   */
  @Override public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
    return prefix.foldRight(aff, middle.foldRight(flip(Node.<V, A, B>foldRight_(aff)), suffix.foldRight(aff, z)));
  }

  /**
   * @see fj.data.fingertrees.FingerTree#reduceRight(fj.F)
   */
  @Override public A reduceRight(final F<A, F<A, A>> aff) {
    return prefix.foldRight(aff, middle.foldRight(flip(Node.<V, A, A>foldRight_(aff)), suffix.reduceRight(aff)));
  }

  @Override public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
    return suffix.foldLeft(bff, middle.foldLeft(Node.<V, A, B>foldLeft_(bff), prefix.foldLeft(bff, z)));
  }

  @Override public A reduceLeft(final F<A, F<A, A>> aff) {
    return suffix.foldLeft(aff, middle.foldLeft(Node.<V, A, A>foldLeft_(aff), prefix.reduceLeft(aff)));
  }

  @Override public <B> FingerTree<V, B> map(final F<A, B> abf, final Measured<V, B> m) {
    return new Deep<V, B>(m, v, prefix.map(abf, m), middle.map(Node.<V, A, B>liftM(abf, m), m.nodeMeasured()),
                          suffix.map(abf, m));
  }

  /**
   * Returns the sum of the measurements of this tree's elements, according to the monoid.
   *
   * @return the sum of the measurements of this tree's elements, according to the monoid.
   */
  public V measure() {
    return v;
  }

  /**
   * Pattern matching on the tree. Matches the function on the Deep tree.
   */
  @Override public <B> B match(final F<Empty<V, A>, B> empty, final F<Single<V, A>, B> single,
                               final F<fj.data.fingertrees.Deep<V, A>, B> deep) {
    return deep.f(this);
  }

  @Override public FingerTree<V, A> cons(final A a) {
    final Measured<V, A> m = measured();
    final V measure = m.sum(m.measure(a), v);
    final MakeTree<V, A> mk = mkTree(m);
    return prefix.match(new F<One<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final One<V, A> one) {
        return new Deep<V, A>(m, measure, mk.two(a, one.value()), middle, suffix);
      }
    }, new F<Two<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Two<V, A> two) {
        return new Deep<V, A>(m, measure, mk.three(a, two.values()._1(), two.values()._2()), middle, suffix);
      }
    }, new F<Three<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Three<V, A> three) {
        return new Deep<V, A>(m, measure, mk.four(a, three.values()._1(), three.values()._2(),
                                                  three.values()._3()), middle, suffix);
      }
    }, new F<Four<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Four<V, A> four) {
        return new Deep<V, A>(m, measure, mk.two(a, four.values()._1()),
                              middle.cons(mk.node3(four.values()._2(), four.values()._3(), four.values()._4())),
                              suffix);
      }
    });
  }

  public FingerTree<V, A> snoc(final A a) {
    final Measured<V, A> m = measured();
    final V measure = m.sum(m.measure(a), v);
    final MakeTree<V, A> mk = mkTree(m);

    return suffix.match(new F<One<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final One<V, A> one) {
        return new Deep<V, A>(m, measure, prefix, middle, mk.two(one.value(), a));
      }
    }, new F<Two<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Two<V, A> two) {
        return new Deep<V, A>(m, measure, prefix, middle, mk.three(two.values()._1(), two.values()._2(), a));
      }
    }, new F<Three<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Three<V, A> three) {
        return new Deep<V, A>(m, measure, prefix, middle, mk.four(three.values()._1(), three.values()._2(),
                                                                  three.values()._3(), a));
      }
    }, new F<Four<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Four<V, A> four) {
        return new Deep<V, A>(m, measure, prefix,
                              middle.snoc(mk.node3(four.values()._1(), four.values()._2(), four.values()._3())),
                              mk.two(four.values()._4(), a));
      }
    });
  }

  @Override public FingerTree<V, A> append(final FingerTree<V, A> t) {
    final Measured<V, A> m = measured();
    return t.match(Function.<Empty<V, A>, FingerTree<V, A>>constant(t), new F<Single<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Single<V, A> single) {
        return t.snoc(single.value());
      }
    }, new F<Deep<V, A>, FingerTree<V, A>>() {
      public FingerTree<V, A> f(final Deep<V, A> deep) {
        return null; // TODO
        /*return new Deep<V, A>(m, m.sum(measure(), deep.measure()), prefix,
                        addDigits0(middle, suffix, deep.prefix, deep.middle), deep.suffix);*/
      }
    });
  }
}
