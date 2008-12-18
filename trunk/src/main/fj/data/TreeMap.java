package fj.data;

import static fj.Function.compose;
import fj.P;
import fj.P2;
import fj.P3;
import static fj.data.IterableW.join;
import static fj.data.List.iterableList;
import fj.pre.Ord;

import java.util.Iterator;
import java.util.Map;

/**
 * An immutable, in-memory map, backed by a red-black tree.
 */
public final class TreeMap<K, V> implements Iterable<P2<K, V>> {
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
   * If the given key is already mapped to a value, the old value is replaced with the given one.
   *
   * @param k The key to insert.
   * @param v The value to insert.
   * @return A new tree map with the given value mapped to the given key.
   */
  public TreeMap<K, V> set(final K k, final V v) {
    final P3<Set<P2<K, Option<V>>>, Option<P2<K, Option<V>>>, Set<P2<K, Option<V>>>> x
        = tree.split(P.p(k, Option.<V>none()));
    return new TreeMap<K, V>(x._1().union(x._3().insert(P.p(k, Option.some(v)))));
  }

  /**
   * Deletes the entry in the tree map that corresponds to the given key.
   *
   * @param k The key to delete from this tree map.
   * @return A new tree map with the entry corresponding to the given key removed.
   */
  public TreeMap<K, V> delete(final K k) {
    return new TreeMap<K, V>(tree.delete(P.p(k, Option.<V>none())));
  }

  /**
   * Returns the number of entries in this tree map.
   *
   * @return The number of entries in this tree map.
   */
  public int size() {
    return tree.size();
  }

  /**
   * Determines if this tree map has any entries.
   *
   * @return <code>true</code> if this tree map has no entries, <code>false</code> otherwise.
   */
  public boolean isEmpty() {
    return tree.isEmpty();
  }

  /**
   * Returns all values in this tree map.
   *
   * @return All values in this tree map.
   */
  public List<V> values() {
    return iterableList(join(tree.toList().map(compose(IterableW.<V, Option<V>>wrap(), P2.<K, Option<V>>__2()))));
  }

  /**
   * Returns all keys in this tree map.
   *
   * @return All keys in this tree map.
   */
  public List<K> keys() {
    return tree.toList().map(P2.<K, Option<V>>__1());
  }

  /**
   * Determines if the given key value exists in this tree map.
   *
   * @param k The key value to look for in this tree map.
   * @return <code>true</code> if this tree map contains the given key, <code>false</code> otherwise.
   */
  public boolean contains(final K k) {
    return tree.member(P.p(k, Option.<V>none()));
  }

  /**
   * Returns an iterator for this map's key-value pairs.
   * This method exists to permit the use in a <code>for</code>-each loop.
   *
   * @return A iterator for this map's key-value pairs.
   */
  public Iterator<P2<K, V>> iterator() {
    return join(tree.toStream().map(P2.<K, Option<V>, IterableW<V>>map2_(IterableW.<V, Option<V>>wrap())
    ).map(P2.tuple(compose(IterableW.<V, P2<K, V>>map(), P.<K, V>p2())))).iterator();
  }

  /**
   * A mutable map projection of this tree map.
   *
   * @return A new mutable map isomorphic to this tree map.
   */
  public Map<K, V> toMutableMap() {
    final Map<K, V> m = new java.util.TreeMap<K, V>();
    for (final P2<K, V> e : this) {
      m.put(e._1(), e._2());
    }
    return m;
  }

  /**
   * An immutable projection of the given mutable map.
   *
   * @param ord An order for the map's keys.
   * @param m   A mutable map to project to an immutable one.
   * @return A new immutable tree map isomorphic to the given mutable map.
   */
  public static <K, V> TreeMap<K, V> fromMutableMap(final Ord<K> ord, final Map<K, V> m) {
    TreeMap<K, V> t = empty(ord);
    for (final Map.Entry<K, V> e : m.entrySet()) {
      t = t.set(e.getKey(), e.getValue());
    }
    return t;
  }

}
