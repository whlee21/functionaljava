import fj.test.Arbitrary;
import fj.test.Property;
import static fj.test.CheckResult.summary;
import static fj.test.Property.property;
import static fj.test.Property.prop;
import static fj.test.Arbitrary.arbLocale;
import static fj.test.Arbitrary.arbitrary;
import static fj.test.Arbitrary.arbCharacter;
import fj.F;
import fj.F2;
import static fj.Function.curry;
import java.util.Locale;
 
public class Fail {
  public static void main(String[] args) {
    final Arbitrary<String> as = arbitrary(arbCharacter.gen.map(new F<Character, String>() {
      public String f(final Character c) {
        return String.valueOf(c);
      }
    }));
 
    final Property p = property(as, arbLocale, curry(new F2<String, Locale, Property>() {
      public Property f(final String s, final Locale l) {
        return prop(s.toLowerCase().equals(s.toLowerCase(l)));
      }
    }));
 
    summary.println(p.check(1000000, 0, 0, 100)); // Falsified after 180306 passed tests with arguments: [&#296;,lt]
  }
}
