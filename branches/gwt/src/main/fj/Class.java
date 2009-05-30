package fj;

import fj.data.List;
import static fj.data.List.unfold;
import fj.data.Option;
import static fj.data.Option.none;
import static fj.data.Option.some;
import fj.data.Tree;

/**
 * A wrapper for a {@link java.lang.Class} that provides additional methods.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public final class Class<T> {
  private final java.lang.Class<T> c;

  private Class(final java.lang.Class<T> c) {
    this.c = c;
  }

  /**
   * Returns the inheritance hierarchy of this class.
   *
   * @return The inheritance hierarchy of this class.
   */
  public List<Class<? super T>> inheritance() {
    return unfold(
        new F<java.lang.Class<? super T>, Option<P2<java.lang.Class<? super T>, java.lang.Class<? super T>>>>() {
          public Option<P2<java.lang.Class<? super T>, java.lang.Class<? super T>>> f(
              final java.lang.Class<? super T> c) {
            if (c == null)
              return none();
            else {
              final P2<java.lang.Class<? super T>, java.lang.Class<? super T>> p =
                  new P2<java.lang.Class<? super T>, java.lang.Class<? super T>>() {
                    public java.lang.Class<? super T> _1() {
                      return c;
                    }

                    @SuppressWarnings({"unchecked"})
                    public java.lang.Class<? super T> _2() {
                      return c.getSuperclass();
                    }
                  };
              return some(p);
            }
          }
        }, c).map(new F<java.lang.Class<? super T>, Class<? super T>>() {
      public Class<? super T> f(final java.lang.Class<? super T> c) {
        return clas(c);
      }
    });
  }

  /**
   * Returns the underlying class.
   *
   * @return The underlying class.
   */
  public java.lang.Class<T> clas() {
    return c;
  }

  /**
   * Constructs a class from the given argument.
   *
   * @param c The argument to construct this class with.
   * @return A class from the given argument.
   */
  public static <T> Class<T> clas(final java.lang.Class<T> c) {
    return new Class<T>(c);
  }
}
