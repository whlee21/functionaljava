import fj.data.Stream;
import fj.data.Zipper;
import static fj.data.List.asString;
import static fj.data.List.fromString;
import static fj.data.Stream.join;
import static fj.data.Stream.single;
import static fj.data.Zipper.fromStream;
import fj.F;
import fj.P;
import static fj.Function.compose;

/**
 * Example of using a Zipper comonad to get all the permutations of a String.
 */

public class Comonad_example {
  public static void main(final String[] args) {
    final String s = "abc";
    for (final Stream<Character> p : perms.f(fromString(s).toStream())) {
      System.out.println(asString(p.toList()));
    }
  }

  public static final F<Stream<Character>, Stream<Stream<Character>>> perms
      = new F<Stream<Character>, Stream<Stream<Character>>>() {
    public Stream<Stream<Character>> f(final Stream<Character> s) {
      Stream<Stream<Character>> r = single(Stream.<Character>nil());
      final F<Stream<Character>, Stream<Stream<Character>>> f = this;
      for (final Zipper<Character> z : fromStream(s)) {
        r = join(z.cobind(new F<Zipper<Character>, Stream<Stream<Character>>>() {
          public Stream<Stream<Character>> f(final Zipper<Character> zp) {
              return f.f(zp.lefts().reverse().append(zp.rights())).map(compose(
                  Stream.<Character>cons().f(zp.focus()),
                  P.<Stream<Character>>p1()));
            }
          }).toStream());
      }
      return r;
    }
  };
}
