package fj.data;

import fj.F;
import fj.Function;
import fj.P;
import static fj.pre.Monoid.intAdditionMonoid;

/**
 * Provides an immutable finite sequence, implemented as a finger tree. This structure gives constant-time access to
 * the head and tail, as well as logarithmic-time random access and concatenation of sequences.
 */
public final class Seq<A> {
  private static <A> FingerTree.MakeTree<Integer, A> mkTree() {
    return FingerTree.mkTree(Seq.<A>elemMeasured());
  }

  private final FingerTree<Integer, A> ftree;

  private Seq(final FingerTree<Integer, A> ftree) {
    this.ftree = ftree;
  }

  private static <A> FingerTree.Measured<Integer, A> elemMeasured() {
    return FingerTree.measured(intAdditionMonoid, Function.<A, Integer>constant(1));
  }

  /**
   * The empty sequence.
   *
   * @return A sequence with no elements.
   */
  public static <A> Seq<A> empty() {
    return new Seq<A>(Seq.<A>mkTree().empty());
  }

  /**
   * A singleton sequence.
   *
   * @param a The single element in the sequence.
   * @return A new sequence with the given element in it.
   */
  public static <A> Seq<A> single(final A a) {
    return new Seq<A>(Seq.<A>mkTree().single(a));
  }

  /**
   * Inserts the given element to the front of this sequence.
   *
   * @param a An element to insert at the front of this sequence.
   * @return A new sequence with the given element at the front.
   */
  public Seq<A> cons(final A a) {
    return new Seq<A>(consTree(a, ftree));
  }

  private static <A> FingerTree<Integer, A> consTree(final A a,
                                                     final FingerTree<Integer, A> as) {
    return as.match(
        P.<F<FingerTree.Empty<Integer, A>, FingerTree<Integer, A>>,
            F<FingerTree.Single<Integer, A>, FingerTree<Integer, A>>,
            F<FingerTree.Deep<Integer, A>, FingerTree<Integer, A>>>p(
            new F<FingerTree.Empty<Integer, A>, FingerTree<Integer, A>>() {
              public FingerTree<Integer, A> f(final FingerTree.Empty<Integer, A> empty) {
                return Seq.<A>mkTree().single(a);
              }
            }, new F<FingerTree.Single<Integer, A>, FingerTree<Integer, A>>() {
              public FingerTree<Integer, A> f(final FingerTree.Single<Integer, A> single) {
                return Seq.<A>mkTree().deep(Seq.<A>mkTree().one(a), Seq.<FingerTree.Node<Integer, A>>mkTree().empty(),
                                            Seq.<A>mkTree().one(single.value()));
              }
            }, new F<FingerTree.Deep<Integer, A>, FingerTree<Integer, A>>() {
              public FingerTree<Integer, A> f(final FingerTree.Deep<Integer, A> deep) {
                final int m = elemMeasured().sum(elemMeasured().measure(a), deep.measure());
                return deep.prefix().match(
                    P.<F<FingerTree.One<Integer, A>, FingerTree<Integer, A>>,
                        F<FingerTree.Two<Integer, A>, FingerTree<Integer, A>>,
                        F<FingerTree.Three<Integer, A>, FingerTree<Integer, A>>,
                        F<FingerTree.Four<Integer, A>, FingerTree<Integer, A>>>p(
                        new F<FingerTree.One<Integer, A>, FingerTree<Integer, A>>() {
                          public FingerTree<Integer, A> f(final FingerTree.One<Integer, A> one) {
                            return Seq.<A>mkTree()
                                .deep(m, Seq.<A>mkTree().two(a, one.value()), deep.middle(), deep.suffix());
                          }
                        }, new F<FingerTree.Two<Integer, A>, FingerTree<Integer, A>>() {
                          public FingerTree<Integer, A> f(final FingerTree.Two<Integer, A> two) {
                            return Seq.<A>mkTree()
                                .deep(m, Seq.<A>mkTree().three(a, two.values()._1(), two.values()._2()), deep.middle(),
                                      deep.suffix());
                          }
                        }, new F<FingerTree.Three<Integer, A>, FingerTree<Integer, A>>() {
                          public FingerTree<Integer, A> f(final FingerTree.Three<Integer, A> three) {
                            return Seq.<A>mkTree()
                                .deep(m, Seq.<A>mkTree().four(a, three.values()._1(), three.values()._2(),
                                                              three.values()._3()),
                                      deep.middle(),
                                      deep.suffix());
                          }
                        }, new F<FingerTree.Four<Integer, A>, FingerTree<Integer, A>>() {
                          public FingerTree<Integer, A> f(final FingerTree.Four<Integer, A> four) {
                            final FingerTree.Two<Integer, A> pre = Seq.<A>mkTree().two(a, four.values()._1());
                            final FingerTree.Node3<Integer, A> n3 =
                                Seq.<A>mkTree()
                                    .node3(four.values()._2(), four.values()._3(), four.values()._4());
                            final FingerTree<Integer, FingerTree.Node<Integer, A>> mid =
                                Seq.consTree(n3, deep.middle());
                            final FingerTree.Digit<Integer, A> suf = deep.suffix();
                            return Seq.<A>mkTree().deep(m, pre, mid, suf);
                          }
                        }));
              }
            }
        ));
  }
}
