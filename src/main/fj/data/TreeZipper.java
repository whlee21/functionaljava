package fj.data;

import fj.*;
import static fj.data.Option.some;
import static fj.data.Option.none;
import static fj.data.Tree.node;
import static fj.data.List.nil;
import static fj.Function.curry;
import static fj.Function.flip;
import fj.pre.Equal;
import fj.pre.Show;
import static fj.pre.Show.*;
import static fj.pre.Equal.*;

/**
 * Provides a zipper structure for rose trees, which is a Tree supplied with a location within that tree.
 * Provides navigation, insertion, deletion, and memorization of visited locations within a tree.
 */
public class TreeZipper<A> {
  private final Tree<A> tree;
  private final List<Tree<A>> lefts;
  private final List<Tree<A>> rights;
  private final List<P3<List<Tree<A>>, A, List<Tree<A>>>> parents;

  private TreeZipper(Tree<A> tree, List<Tree<A>> lefts, List<Tree<A>> rights, List<P3<List<Tree<A>>, A, List<Tree<A>>>> parents) {
    this.tree = tree;
    this.lefts = lefts;
    this.rights = rights;
    this.parents = parents;
  }

  /**
   * Creates a new tree zipper given a currently selected tree, a forest on the left, a forest on the right,
   * and a list of parent contexts.
   *
   * @param tree    The currently selected tree.
   * @param lefts   The selected tree's left siblings, closest first.
   * @param rights  The selected tree's right siblings, closest first.
   * @param parents The parent of the selected tree, and the parent's siblings.
   * @return A new zipper with the given tree selected, and the given forests on the left and right.
   */
  public static <A> TreeZipper<A> treeZipper(Tree<A> tree, List<Tree<A>> lefts, List<Tree<A>> rights, List<P3<List<Tree<A>>, A, List<Tree<A>>>> parents) {
    return new TreeZipper<A>(tree, lefts, rights, parents);
  }

  /**
   * First-class constructor for tree zippers.
   *
   * @return A function that returns a new tree zipper, given a selected tree, left and right siblings,
   *         and a parent context.
   */
  public static <A>
  F<Tree<A>, F<List<Tree<A>>, F<List<Tree<A>>, F<List<P3<List<Tree<A>>, A, List<Tree<A>>>>, TreeZipper<A>>>>>
  treeZipper() {
    return curry(
        new F4<Tree<A>, List<Tree<A>>, List<Tree<A>>, List<P3<List<Tree<A>>, A, List<Tree<A>>>>, TreeZipper<A>>() {
          public TreeZipper<A> f(final Tree<A> tree,
                                 final List<Tree<A>> lefts,
                                 final List<Tree<A>> rights,
                                 List<P3<List<Tree<A>>, A, List<Tree<A>>>> parents) {
            return treeZipper(tree, lefts, rights, parents);
          }
        });
  }

  /**
   * Returns the product-4 representation of this zipper.
   *
   * @return the product-4 representation of this zipper.
   */
  public P4<Tree<A>, List<Tree<A>>, List<Tree<A>>, List<P3<List<Tree<A>>, A, List<Tree<A>>>>> p() {
    return P.p(tree, lefts, rights, parents);
  }

  /**
   * A first-class function that returns the product-4 representation of a given zipper.
   *
   * @return a function that converts a given zipper to its product-4 representation.
   */
  public static <A>
  F<TreeZipper<A>, P4<Tree<A>, List<Tree<A>>, List<Tree<A>>, List<P3<List<Tree<A>>, A, List<Tree<A>>>>>> p_() {
    return new F<
        TreeZipper<A>,
        P4<Tree<A>,
            List<Tree<A>>,
            List<Tree<A>>,
            List<P3<List<Tree<A>>, A, List<Tree<A>>>>>>() {
      public P4<
          Tree<A>,
          List<Tree<A>>,
          List<Tree<A>>,
          List<P3<List<Tree<A>>, A, List<Tree<A>>>>> f(final TreeZipper<A> a) {
        return a.p();
      }
    };
  }

  /**
   * An Equal instance for tree zippers.
   *
   * @param e An Equal instance for tree elements.
   * @return An Equal instance for tree zippers.
   */
  public static <A> Equal<TreeZipper<A>> eq(final Equal<A> e) {
    return p4Equal(
        treeEqual(e),
        listEqual(treeEqual(e)),
        listEqual(treeEqual(e)),
        listEqual(p3Equal(listEqual(treeEqual(e)), e, listEqual(treeEqual(e))))).comap(TreeZipper.<A>p_());
  }

  /**
   * A Show instance for tree zippers.
   *
   * @param s A Show instance for tree elements.
   * @return A Show instance for tree zippers.
   */
  public static <A> Show<TreeZipper<A>> eq(final Show<A> s) {
    return p4Show(
        treeShow(s),
        listShow(treeShow(s)),
        listShow(treeShow(s)),
        listShow(p3Show(listShow(treeShow(s)), s, listShow(treeShow(s))))).comap(TreeZipper.<A>p_());
  }

  private static <A> List<Tree<A>> combChildren(final List<Tree<A>> ls,
                                                final Tree<A> t,
                                                final List<Tree<A>> rs) {
    return ls.foldLeft(flip(List.<Tree<A>>cons()), List.cons(t, rs));
  }

  /**
   * Navigates to the parent of the current location.
   *
   * @return A new tree zipper focused on the parent node of the current node,
   *         or none if the current node is the root node.
   */
  public Option<TreeZipper<A>> parent() {
    if (parents.isEmpty())
      return none();
    else {
      final P3<List<Tree<A>>, A, List<Tree<A>>> p = parents.head();
      return some(treeZipper(node(p._2(), combChildren(lefts, tree, rights)), p._1(), p._3(), parents.tail()));
    }
  }

  /**
   * Navigates to the top-most parent of the current location.
   *
   * @return A new tree zipper focused on the top-most parent of the current node.
   */
  public TreeZipper<A> root() {
    return parent().option(this, TreeZipper.<A>root_());
  }

  /**
   * A first-class version of the root function.
   *
   * @return A function that returns a new tree-zipper focused on the root of the given tree zipper's tree.
   */
  public static <A> F<TreeZipper<A>, TreeZipper<A>> root_() {
    return new F<TreeZipper<A>, TreeZipper<A>>() {
      public TreeZipper<A> f(final TreeZipper<A> a) {
        return a.root();
      }
    };
  }

  /**
   * Navigates to the left sibling of the current location.
   *
   * @return A new tree zipper focused on the left sibling of the current node,
   *         or none if there are no siblings on the left.
   */
  public Option<TreeZipper<A>> left() {
    return lefts.isEmpty() ? Option.<TreeZipper<A>>none()
        : some(treeZipper(lefts.head(), lefts.tail(), rights.cons(tree), parents));
  }

  /**
   * Navigates to the right sibling of the current location.
   *
   * @return A new tree zipper focused on the right sibling of the current node,
   *         or none if there are no siblings on the right.
   */
  public Option<TreeZipper<A>> right() {
    return rights.isEmpty() ? Option.<TreeZipper<A>>none()
        : some(treeZipper(rights.head(), lefts.cons(tree), rights.tail(), parents));
  }

  /**
   * Navigtes to the first child of the current location.
   *
   * @return A new tree zipper focused on the first child of the current node, or none if the node has no children.
   */
  public Option<TreeZipper<A>> firstChild() {
    final List<Tree<A>> ts = tree.subForest();
    return ts.isEmpty() ? Option.<TreeZipper<A>>none()
        : some(treeZipper(ts.head(), List.<Tree<A>>nil(), ts.tail(), downParents()));
  }

  /**
   * Navigtes to the last child of the current location.
   *
   * @return A new tree zipper focused on the last child of the current node, or none if the node has no children.
   */
  public Option<TreeZipper<A>> lastChild() {
    final List<Tree<A>> ts = tree.subForest().reverse();
    return ts.isEmpty() ? Option.<TreeZipper<A>>none()
        : some(treeZipper(ts.head(), ts.tail(), List.<Tree<A>>nil(), downParents()));
  }

  /**
   * Navigates to the given child of the current location, starting at index 0.
   *
   * @param n The index of the child to which to navigate.
   * @return An optional tree zipper focused on the child node at the given index, or none if there is no such child.
   */
  public Option<TreeZipper<A>> getChild(final int n) {
    Option<TreeZipper<A>> r = none();
    for (P2<List<Tree<A>>, List<Tree<A>>> lr : splitChildren(List.<Tree<A>>nil(), tree.subForest(), n)) {
      r = some(treeZipper(lr._1().head(), lr._1().tail(), lr._2(), downParents()));
    }
    return r;
  }

  /**
   * Navigates to the first child of the current location, that satisfies the given predicate.
   *
   * @param p A predicate to be satisfied by the child node.
   * @return An optional tree zipper focused on the first child node that satisfies the given predicate,
   *         or none if there is no such child.
   */
  public Option<TreeZipper<A>> findChild(final F<Tree<A>, Boolean> p) {
    Option<TreeZipper<A>> r = none();

    F2<List<Tree<A>>, List<Tree<A>>, Option<P3<List<Tree<A>>, Tree<A>, List<Tree<A>>>>> split =
        new F2<List<Tree<A>>, List<Tree<A>>, Option<P3<List<Tree<A>>, Tree<A>, List<Tree<A>>>>>() {
          public Option<P3<List<Tree<A>>, Tree<A>, List<Tree<A>>>> f(final List<Tree<A>> acc, final List<Tree<A>> xs) {
            return p.f(xs.head()) ? some(P.p(acc, xs.head(), xs.tail()))
                : xs.isNotEmpty() ? this.f(acc.cons(xs.head()), xs.tail())
                : Option.<P3<List<Tree<A>>, Tree<A>, List<Tree<A>>>>none();
          }
        };
    for (P3<List<Tree<A>>, Tree<A>, List<Tree<A>>> ltr : split.f(List.<Tree<A>>nil(), tree.subForest())) {
      r = some(treeZipper(ltr._2(), ltr._1(), ltr._3(), downParents()));
    }
    return r;
  }

  private List<P3<List<Tree<A>>, A, List<Tree<A>>>> downParents() {
    return parents.cons(P.p(lefts, tree.root(), rights));
  }

  private static <A> Option<P2<List<A>, List<A>>> splitChildren(final List<A> acc, final List<A> xs, final int n) {
    return n == 0 ? some(P.p(acc, xs))
        : xs.isNotEmpty() ? splitChildren(acc.cons(xs.head()), xs.tail(), n - 1)
        : Option.<P2<List<A>, List<A>>>none();
  }

  private static <A> List<P3<List<Tree<A>>, A, List<Tree<A>>>> lp3nil() {
    return nil();
  }

  /**
   * Creates a new tree zipper focused on the root of the given tree.
   *
   * @param t A tree over which to create a new zipper.
   * @return a new tree zipper focused on the root of the given tree.
   */
  public static <A> TreeZipper<A> fromTree(Tree<A> t) {
    return treeZipper(t, List.<Tree<A>>nil(), List.<Tree<A>>nil(), TreeZipper.<A>lp3nil());
  }

  /**
   * Creates a new tree zipper focused on the first element of the given forest.
   *
   * @param ts A forest over which to create a new zipper.
   * @return a new tree zipper focused on the first element of the given forest.
   */
  public static <A> Option<TreeZipper<A>> fromForest(List<Tree<A>> ts) {
    return ts.isNotEmpty()
        ? some(treeZipper(ts.head(), List.<Tree<A>>nil(), ts.tail(), TreeZipper.<A>lp3nil()))
        : Option.<TreeZipper<A>>none();
  }

  /**
   * Returns the tree containing this location.
   *
   * @return the tree containing this location.
   */
  public Tree<A> toTree() {
    return root().tree;
  }

  /**
   * Returns the forest containing this location.
   *
   * @return the forest containing this location.
   */
  public List<Tree<A>> toForest() {
    final TreeZipper<A> r = root();
    return combChildren(r.lefts, r.tree, r.rights);
  }

  /**
   * Returns the tree at the currently focused node.
   *
   * @return the tree at the currently focused node.
   */
  public Tree<A> tree() {
    return tree;
  }

  /**
   * Returns the left siblings of the currently focused node.
   *
   * @return the left siblings of the currently focused node.
   */
  public List<Tree<A>> lefts() {
    return lefts;
  }

  /**
   * Returns the right siblings of the currently focused node.
   *
   * @return the right siblings of the currently focused node.
   */
  public List<Tree<A>> rights() {
    return rights;
  }

}
