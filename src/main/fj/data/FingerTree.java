package fj.data;

import fj.F;
import fj.F2;
import fj.F3;
import fj.P3;
import fj.P4;
import static fj.Function.curry;
import static fj.Function.flip;
import static fj.data.vector.V.v;
import fj.data.vector.V2;
import fj.data.vector.V3;
import fj.data.vector.V4;
import fj.pre.Monoid;

/**
 * Provides 2-3 finger trees, a functional representation of persistent sequences supporting access to the ends in
 * amortized constant time, and concatenation and splitting in time logarithmic in the size of the smaller piece.
 * A general purpose data structure that can serve as a sequence, priority queue, search tree, priority search queue
 * and more.
 * <p/>
 * Based on "Finger trees: a simple general-purpose data structure", by Ralf Hinze and Ross Paterson.
 *
 * @param <V> The monoidal type with which to annotate nodes.
 * @param <A> The type of the tree's elements.
 */
public abstract class FingerTree<V, A> {
  private final Measured<V, A> m;

  /**
   * Folds the tree to the right with the given function and the given initial element.
   *
   * @param f A function with which to fold the tree.
   * @param z An initial element to apply to the fold.
   * @return A reduction of this tree by applying the given function, associating to the right.
   */
  public abstract <B> B foldRight(F<A, F<B, B>> f, B z);

  /**
   * Returns the sum of this tree's annotations.
   *
   * @return the sum of this tree's annotations.
   */
  public abstract V measure();

  /**
   * Indicates whether this tree is empty.
   *
   * @return true if this tree is the empty tree, otherwise false.
   */
  public boolean isEmpty() {
    return this instanceof Empty;
  }

  protected Measured<V, A> measured() {
    return m;
  }

  /**
   * Provides pattern matching on trees. This is the Church encoding of the FingerTree datatype.
   *
   * @param p A triplet of functions to match the different possible tree constructors.
   * @return The result of the function that matches this tree structurally, applied to this tree.
   */
  public abstract <B> B match(P3<F<Empty<V, A>, B>, F<Single<V, A>, B>, F<Deep<V, A>, B>> p);

  private FingerTree(final Measured<V, A> m) {
    this.m = m;
  }

  /**
   * Determines how the elements of a tree are measured and how measures are summed. Consists of a monoid and a
   * measuring function. Different instances of this class will result in different behaviours for the tree.
   */
  public static final class Measured<V, A> {
    private final Monoid<V> m;
    private final F<A, V> measure;

    private Measured(final Monoid<V> m, final F<A, V> measure) {
      this.m = m;
      this.measure = measure;
    }

    public Monoid<V> monoid() {
      return m;
    }

    public F<A, V> measure() {
      return measure;
    }

    public V measure(final A a) {
      return measure.f(a);
    }

    public V sum(final V a, final V b) {
      return m.sum(a, b);
    }

    public V zero() {
      return m.zero();
    }
  }

  /**
   * Constructs a Measured instance for the element type, given a monoid and a measuring function.
   *
   * @param monoid  A monoid for the measures.
   * @param measure A function with which to measure element values.
   * @return A Measured instance for the given element type, that uses the given monoid and measuring function.
   */
  public static <V, A> Measured<V, A> measured(final Monoid<V> monoid, final F<A, V> measure) {
    return new Measured<V, A>(monoid, measure);
  }

  /**
   * Returns a builder of trees and tree components that annotates them using the given Measured instance.
   *
   * @param m A Measured instance with which to annotate trees, digits, and nodes.
   * @return A builder of trees and tree components that annotates them using the given Measured instance.
   */
  public static <V, A> MakeTree<V, A> mkTree(final Measured<V, A> m) {
    return new MakeTree<V, A>(m);
  }

  /**
   * A builder of trees and tree components, supplied with a particular monoid and measuring function.
   */
  public static final class MakeTree<V, A> {
    private final Measured<V, A> m;

    private MakeTree(final Measured<V, A> m) {
      this.m = m;
    }

    // Tree constructors

    /**
     * Constructs an empty tree.
     *
     * @return The empty tree.
     */
    public FingerTree<V, A> empty() {
      return new Empty<V, A>(m);
    }

    /**
     * Constructs a singleton tree.
     *
     * @param a A single element for the tree.
     * @return A tree with the given value as the single element.
     */
    public FingerTree<V, A> single(final A a) {
      return new Single<V, A>(m, a);
    }

    /**
     * Constructs a deep tree. This structure consists of two digits, of 1 to 4 elements each, on the left and right,
     * with the rest of the tree in the middle.
     *
     * @param prefix The leftmost elements of the tree.
     * @param middle The subtree, which is a Finger Tree of 2-3 nodes.
     * @param suffix The rightmost elements of the tree.
     * @return A new finger tree with the given prefix, suffix, and middle.
     */
    public FingerTree<V, A> deep(final Digit<V, A> prefix, final FingerTree<V, Node<V, A>> middle,
                                 final Digit<V, A> suffix) {
      return deep(m.sum(prefix.measure(), m.sum(middle.measure(), suffix.measure())), prefix, middle, suffix);
    }

    /**
     * Constructs a deep tree with the given annotation value.
     *
     * @param v      The value with which to annotate this tree.
     * @param prefix The leftmost elements of the tree.
     * @param middle The subtree, which is a Finger Tree of 2-3 nodes.
     * @param suffix The rightmost elements of the tree.
     * @return A new finger tree with the given prefix, suffix, and middle, and annotated with the given value.
     */
    public FingerTree<V, A> deep(final V v, final Digit<V, A> prefix, final FingerTree<V, Node<V, A>> middle,
                                 final Digit<V, A> suffix) {
      return new Deep<V, A>(m, v, prefix, middle, suffix);
    }

    // Digit constructors

    /**
     * A digit of one element.
     *
     * @param a The element of the digit.
     * @return A digit of the given element.
     */
    public One<V, A> one(final A a) {
      return new One<V, A>(m, a);
    }

    /**
     * A digit of two elements.
     *
     * @param a The first element of the digit.
     * @param b The second element of the digit.
     * @return A digit of the given elements.
     */
    public Two<V, A> two(final A a, final A b) {
      return new Two<V, A>(m, v(a, b));
    }

    /**
     * A digit of three elements.
     *
     * @param a The first element of the digit.
     * @param b The second element of the digit.
     * @param c The third element of the digit.
     * @return A digit of the given elements.
     */
    public Three<V, A> three(final A a, final A b, final A c) {
      return new Three<V, A>(m, v(a, b, c));
    }

    /**
     * A digit of four elements.
     *
     * @param a The first element of the digit.
     * @param b The second element of the digit.
     * @param c The third element of the digit.
     * @param d The fifth element of the digit.
     * @return A digit of the given elements.
     */
    public Four<V, A> four(final A a, final A b, final A c, final A d) {
      return new Four<V, A>(m, v(a, b, c, d));
    }

    // Node constructors

    /**
     * A binary tree node.
     *
     * @param a The left child of the node.
     * @param b The right child of the node.
     * @return A new binary tree node.
     */
    public Node2<V, A> node2(final A a, final A b) {
      return new Node2<V, A>(m, v(a, b));
    }

    /**
     * A trinary tree node.
     *
     * @param a The left child of the node.
     * @param b The middle child of the node.
     * @param c The right child of the node.
     * @return A new trinary tree node.
     */
    public Node3<V, A> node3(final A a, final A b, final A c) {
      return new Node3<V, A>(m, v(a, b, c));
    }

  }

  /**
   * The empty tree.
   */
  public static final class Empty<V, A> extends FingerTree<V, A> {
    private Empty(final Measured<V, A> m) {
      super(m);
    }

    /**
     * Returns zero.
     *
     * @return Zero.
     */
    public V measure() {
      return measured().zero();
    }

    /**
     * Pattern matching on the structure of this tree. Matches the empty tree.
     *
     * @param p A triplet of functions to match the different possible tree constructors.
     * @return The result of applying the function on the Empty tree to this tree.
     */
    public <B> B match(
        final P3<F<Empty<V, A>, B>, F<Single<V, A>, B>, F<Deep<V, A>, B>> p) {
      return p._1().f(this);
    }

    /**
     * Folds this tree to the right.
     *
     * @param aff A function with which to fold this tree.
     * @param z   An initial element to apply to the fold.
     * @return The second argument, since this is the empty tree.
     */
    public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
      return z;
    }
  }

  // A tree with a single element
  public static final class Single<V, A> extends FingerTree<V, A> {
    private final A a;
    private final V v;

    private Single(final Measured<V, A> m, final A a) {
      super(m);
      this.a = a;
      v = m.measure(a);
    }

    public V measure() {
      return v;
    }

    public <B> B match(
        final P3<F<Empty<V, A>, B>, F<Single<V, A>, B>, F<Deep<V, A>, B>> p) {
      return p._2().f(this);
    }

    public A value() {
      return a;
    }

    public <B> B foldRight(final F<A, F<B, B>> f, final B z) {
      return f.f(a).f(z);
    }
  }

  // A tree with elements at lower levels
  public static final class Deep<V, A> extends FingerTree<V, A> {
    private final V v;
    private final Digit<V, A> prefix;
    private final FingerTree<V, Node<V, A>> middle;
    private final Digit<V, A> suffix;

    private Deep(final Measured<V, A> m, final V v, final Digit<V, A> prefix,
                 final FingerTree<V, Node<V, A>> middle,
                 final Digit<V, A> suffix) {
      super(m);
      this.v = v;
      this.prefix = prefix;
      this.middle = middle;
      this.suffix = suffix;
    }

    public Digit<V, A> prefix() {
      return prefix;
    }

    public FingerTree<V, Node<V, A>> middle() {
      return middle;
    }

    public Digit<V, A> suffix() {
      return suffix;
    }

    public V measure() {
      return v;
    }

    public <B> B match(
        final P3<F<Empty<V, A>, B>, F<Single<V, A>, B>, F<Deep<V, A>, B>> p) {
      return p._3().f(this);
    }

    public <B> B foldRight(final F<A, F<B, B>> f, final B z) {
      return prefix
          .foldRight(f, middle.foldRight(flip(this.<A, B>foldRightNode().f(f)), suffix.foldRight(f, z)));
    }

    private <A, B> F<F<A, F<B, B>>, F<B, F<Node<V, A>, B>>> foldRightNode() {
      return curry(new F3<F<A, F<B, B>>, B, Node<V, A>, B>() {
        public B f(final F<A, F<B, B>> aff, final B b, final Node<V, A> node) {
          return node.foldRight(aff, b);
        }
      });
    }
  }

  // A digit is a vector with 1, 2, 3, or 4 elements.
  public abstract static class Digit<V, A> {
    public abstract <B> B foldRight(final F<A, F<B, B>> f, final B z);

    public abstract <B> B foldLeft(final F<B, F<A, B>> f, final B z);

    public abstract <B> B match(P4<F<One<V, A>, B>, F<Two<V, A>, B>, F<Three<V, A>, B>, F<Four<V, A>, B>> p);

    private final Measured<V, A> m;

    protected Digit(final Measured<V, A> m) {
      this.m = m;
    }

    public V measure() {
      return foldLeft(curry(new F2<V, A, V>() {
        public V f(final V v, final A a) {
          return m.sum(v, m.measure(a));
        }
      }), m.zero());
    }
  }

  public static final class One<V, A> extends Digit<V, A> {
    private final A a;

    private One(final Measured<V, A> m, final A a) {
      super(m);
      this.a = a;
    }

    public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
      return aff.f(a).f(z);
    }

    public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
      return bff.f(z).f(a);
    }

    public <B> B match(
        final P4<F<One<V, A>, B>, F<Two<V, A>, B>, F<Three<V, A>, B>, F<Four<V, A>, B>> p) {
      return p._1().f(this);
    }

    public A value() {
      return a;
    }
  }

  public static final class Two<V, A> extends Digit<V, A> {
    private final V2<A> as;

    private Two(final Measured<V, A> m, final V2<A> as) {
      super(m);
      this.as = as;
    }

    public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
      return aff.f(as._1()).f(aff.f(as._2()).f(z));
    }

    public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
      return as.toStream().foldLeft(bff, z);
    }

    public <B> B match(
        final P4<F<One<V, A>, B>, F<Two<V, A>, B>, F<Three<V, A>, B>, F<Four<V, A>, B>> p) {
      return p._2().f(this);
    }

    public V2<A> values() {
      return as;
    }
  }

  public static final class Three<V, A> extends Digit<V, A> {
    private final V3<A> as;

    private Three(final Measured<V, A> m, final V3<A> as) {
      super(m);
      this.as = as;
    }

    public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
      return aff.f(as._1()).f(aff.f(as._2()).f(aff.f(as._3()).f(z)));
    }

    public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
      return as.toStream().foldLeft(bff, z);
    }

    public <B> B match(
        final P4<F<One<V, A>, B>, F<Two<V, A>, B>, F<Three<V, A>, B>, F<Four<V, A>, B>> p) {
      return p._3().f(this);
    }

    public V3<A> values() {
      return as;
    }
  }

  public static final class Four<V, A> extends Digit<V, A> {
    private final V4<A> as;

    private Four(final Measured<V, A> m, final V4<A> as) {
      super(m);
      this.as = as;
    }

    public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
      return aff.f(as._1()).f(aff.f(as._2()).f(aff.f(as._3()).f(aff.f(as._4()).f(z))));
    }

    public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
      return as.toStream().foldLeft(bff, z);
    }

    public <B> B match(
        final P4<F<One<V, A>, B>, F<Two<V, A>, B>, F<Three<V, A>, B>, F<Four<V, A>, B>> p) {
      return p._4().f(this);
    }

    public V4<A> values() {
      return as;
    }
  }

  // A node in the tree has 2 or 3 children.
  public abstract static class Node<V, A> {
    private final Measured<V, A> m;

    public abstract <B> B foldRight(final F<A, F<B, B>> f, final B z);

    public abstract V measure();

    public abstract Digit<V, A> toDigit();

    protected Node(final Measured<V, A> m) {
      this.m = m;
    }

    protected Measured<V, A> measured() {
      return m;
    }
  }

  public static final class Node2<V, A> extends Node<V, A> {
    private final V measure;
    private final V2<A> as;

    private Node2(final Measured<V, A> m, final V2<A> as) {
      super(m);
      this.as = as;
      measure = m.sum(m.measure(as._1()), m.measure(as._2()));
    }

    public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
      return aff.f(as._1()).f(aff.f(as._2()).f(z));
    }

    public V measure() {
      return measure;
    }

    public Digit<V, A> toDigit() {
      return new Two<V, A>(measured(), as);
    }
  }

  public static final class Node3<V, A> extends Node<V, A> {
    private final V3<A> as;
    private final V measure;

    private Node3(final Measured<V, A> m, final V3<A> as) {
      super(m);
      this.as = as;

      measure = m.sum(m.measure(as._1()), m.sum(m.measure(as._2()), m.measure(as._3())));
    }

    public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
      return aff.f(as._1()).f(aff.f(as._2()).f(aff.f(as._3()).f(z)));
    }

    public V measure() {
      return measure;
    }

    public Digit<V, A> toDigit() {
      return new Three<V, A>(measured(), as);
    }
  }

  private <A> F<Node<V, A>, V> nodeMeasured() {
    return new F<Node<V, A>, V>() {
      public V f(final Node<V, A> n) {
        return n.measure();
      }
    };
  }

  private <A> F<FingerTree<V, A>, V> treeMeasured() {
    return new F<FingerTree<V, A>, V>() {
      public V f(final FingerTree<V, A> n) {
        return n.measure();
      }
    };
  }
}
