package fj.data;

import fj.F;
import fj.Function;
import static fj.pre.Monoid.intAdditionMonoid;
import fj.data.fingertrees.One;
import fj.data.fingertrees.Two;
import fj.data.fingertrees.Three;
import fj.data.fingertrees.Four;
import fj.data.fingertrees.Node;
import fj.data.fingertrees.Node3;
import fj.data.fingertrees.Single;
import fj.data.fingertrees.Digit;
import fj.data.fingertrees.Deep;
import static fj.data.fingertrees.FingerTree.measured;
import fj.data.fingertrees.*;

/**
 * Provides an immutable finite sequence, implemented as a finger tree. This structure gives O(1) access to
 * the head and tail, as well as O(log n) random access and concatenation of sequences.
 */
public final class Seq<A> {
  private static <A> MakeTree<Integer, A> mkTree() {
    return FingerTree.mkTree(Seq.<A>elemMeasured());
  }

  private final FingerTree<Integer, A> ftree;

  private Seq(final FingerTree<Integer, A> ftree) {
    this.ftree = ftree;
  }

  private static <A> Measured<Integer, A> elemMeasured() {
    return measured(intAdditionMonoid, Function.<A, Integer>constant(1));
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
   * Inserts the given element at the front of this sequence.
   *
   * @param a An element to insert at the front of this sequence.
   * @return A new sequence with the given element at the front.
   */
  public Seq<A> cons(final A a) {
    return new Seq<A>(ftree.cons(a));
  }

  /**
   * Inserts the given element at the end of this sequence.
   *
   * @param a An element to insert at the end of this sequence.
   * @return A new sequence with the given element at the end.
   */
  public Seq<A> snoc(final A a) {
    return new Seq<A>(ftree.snoc(a));
  }

}
