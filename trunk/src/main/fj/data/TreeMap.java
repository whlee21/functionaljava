package fj.data;

import fj.P;
import fj.P2;
import static fj.data.IterableW.wrap;
import static fj.data.IterableW.join;
import static fj.Function.compose;
import fj.pre.Ord;

import java.util.Iterator;

/**
 * An immutable, in-memory map, backed by a red-black tree.
 */
public class TreeMap<K, V> implements Iterable<P2<K, V>> {
  private final Set<P2<K, Option<V>>> tree;

  private TreeMap(final Set<P2<K, Option<V>>> tree) {
    this.tree = tree;
  }

  private static <K, V> Ord<P2<K, V>> ord(final Ord<K> keyOrd) {
    return keyOrd.comap(P2.<K, V>__1());
  }

  /**
   * Constructs an empty tree map.
   *
   * @param keyOrd An order for the keys of the tree map.
   * @return an empty TreeMap with the given key order.
   */
  public static <K, V> TreeMap<K, V> empty(final Ord<K> keyOrd) {
    return new TreeMap<K, V>(Set.empty(TreeMap.<K, Option<V>>ord(keyOrd)));
  }

  /**
   * Returns a potential value that the given key maps to.
   *
   * @param k The key to look up in the tree map.
   * @return A potential value for the given key.
   */
  public Option<V> get(final K k) {
    final Option<P2<K, Option<V>>> x = tree.split(P.p(k, Option.<V>none()))._2();
    return x.bind(P2.<K, Option<V>>__2());
  }

  /**
   * Inserts the given key and value association into the tree map.
   *
   * @param k The key to insert.
   * @param v The value to insert.
   * @return A new tree map with the given value mapped to the given key.
   */
  public TreeMap<K, V> set(final K k, final V v) {
    return new TreeMap<K, V>(tree.insert(P.p(k, Option.some(v))));
  }

  /**
   * Returns an iterator for this map's key-value pairs.
   * This method exists to permit the use in a <code>for</code>-each loop.
   *
   * @return A iterator for this map's key-value pairs.
   */
  public Iterator<P2<K, V>> iterator() {
    return join(wrap(tree).map(P2.<K, Option<V>, IterableW<V>>map2_(IterableW.<V, Option<V>>wrap())
    ).map(P2.tuple(compose(IterableW.<V, P2<K, V>>map(), P.<K, V>p2())))).iterator();
  }

}
