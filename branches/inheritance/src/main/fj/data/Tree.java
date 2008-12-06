package fj.data;

/**
 * Provides an immutable, non-empty, multi-way tree (a rose tree).
 * Author: runar
 * Date: May 8, 2008 11:20:50 PM
 */
public final class Tree<A> {

  private final A root;
  private final List<Tree<A>> subForest;

  private Tree(final A root, final List<Tree<A>> subForest) {
    this.root = root;
    this.subForest = subForest;
  }

  /**
   * Creates a nullary tree.
   *
   * @param root The root element of the tree.
   * @return A nullary tree with the root element in it.
   */
  public static <A> Tree<A> leaf(final A root) {
    return node(root, List.<Tree<A>>nil());
  }

  /**
   * Creates a new n-ary tree given a root and a subforest of length n.
   *
   * @param root   The root element of the tree.
   * @param forest A list of the tree's subtrees.
   * @return A newly sprouted tree.
   */
  public static <A> Tree<A> node(final A root, final List<Tree<A>> forest) {
    return new Tree<A>(root, forest);
  }

  /**
   * @return The root element of the tree.
   */
  public A root() {
    return root;
  }

  /**
   * @return A list of the tree's subtrees.
   */
  public List<Tree<A>> subForest() {
    return subForest;
  }
}
