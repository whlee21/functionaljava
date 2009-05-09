package fj.data;

import fj.F;
import static fj.P.p;
import fj.P2;
import static fj.data.Option.none;
import static fj.data.Option.some;
import fj.pre.Equal;
import static fj.pre.Equal.streamEqual;
import static fj.pre.Equal.charEqual;

import java.util.regex.Pattern;

public final class LazyString implements CharSequence {
  private final Stream<Character> s;

  private LazyString(final Stream<Character> s) {
    this.s = s;
  }

  public static LazyString str(final String s) {
    return new LazyString(Stream.unfold(new F<P2<String, Integer>, Option<P2<Character, P2<String, Integer>>>>() {
      public Option<P2<Character, P2<String, Integer>>> f(final P2<String, Integer> o) {
        final String s = o._1();
        final int n = o._2();
        final Option<P2<Character, P2<String, Integer>>> none = none();
        return s.length() <= n ? none : some(p(s.charAt(n), p(s, n + 1)));
      }
    }, p(s, 0)));
  }

  public static final LazyString empty = str("");

  public static LazyString fromStream(final Stream<Character> s) {
    return new LazyString(s);
  }

  public Stream<Character> toStream() {
    return s;
  }

  public int length() {
    return s.length();
  }

  public char charAt(final int index) {
    return s.index(index);
  }

  public CharSequence subSequence(final int start, final int end) {
    return fromStream(s.drop(start).take(end));
  }

  public String toString() {
    return new StringBuilder(this).toString();
  }

  public LazyString append(final LazyString cs) {
    return fromStream(s.append(cs.s));
  }

  public LazyString append(final String s) {
    return append(str(s));
  }

  public boolean contains(final LazyString cs) {
    return s.substreams().exists(eqS.eq(cs.toStream()));
  }

  public boolean endsWith(final LazyString cs) {
    return s.tails().exists(eqS.eq(cs.toStream()));
  }

  public boolean startsWith(final LazyString cs) {
    return s.inits().exists(eqS.eq(cs.toStream()));
  }

  public Option<Integer> indexOf(final char c) {
    return s.indexOf(Equal.charEqual.eq(c));
  }

  public Option<Integer> indexOf(final LazyString cs) {
    return s.substreams().indexOf(eqS.eq(cs.s));
  }

  public boolean matches(final String regex) {
    return Pattern.matches(regex, this);
  }

  public static final F<LazyString, Stream<Character>> toStream =
      new F<LazyString, Stream<Character>>() {
        public Stream<Character> f(final LazyString string) {
          return string.toStream();
        }
      };

  public static final F<Stream<Character>, LazyString> fromStream =
      new F<Stream<Character>, LazyString>() {
        public LazyString f(final Stream<Character> s) {
          return fromStream(s);
        }
      };

  private static final Equal<Stream<Character>> eqS = streamEqual(charEqual);

}
