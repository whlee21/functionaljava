import fj.test.Arbitrary;
import fj.test.Property;
import static fj.test.CheckResult.summary;
import static fj.test.Property.property;
import static fj.test.Property.prop;
import static fj.test.Arbitrary.arbLocale;
import static fj.test.Arbitrary.arbitrary;
import static fj.test.Arbitrary.arbByte;
import fj.F;
import fj.F2;
import static fj.Function.curry;
import java.util.Locale;
 
public class Pass {
  public static void main(String[] args) {
    final Arbitrary<String> as = arbitrary(arbByte.gen.map(new F<Byte, String>() {
      public String f(final Byte b) {
        return String.valueOf((char)b.byteValue());
      }
    }));
 
    final Property p = property(as, arbLocale, curry(new F2<String, Locale, Property>() {
      public Property f(final String s, final Locale l) {
        return prop(s.toLowerCase().equals(s.toLowerCase(l)));
      }
    }));
 
    summary.println(p.check()); // OK, passed 100 tests.
  }
}
