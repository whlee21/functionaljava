import fj.F;
import static fj.Function.identity;
import fj.P2;
import fj.Unit;
import static fj.Unit.unit;
import static java.lang.System.out;
import static fj.data.hlist.HList.HNil;
import static fj.data.hlist.HList.HCons;
import static fj.data.hlist.HList.single;
import static fj.data.hlist.HList.Apply;
import static fj.data.hlist.HList.HFoldr;
import static fj.function.Integers.multiply;
import static fj.function.Integers.add;
import static fj.function.Strings.length;

public class HList_foldRight {

  public static void main(final String[] args) {

    // A heterogeneous list of functions to compose
    final HCons<F<String, Integer>, HCons<F<Integer, Integer>, HCons<F<Integer, Integer>, HNil>>> functions =
      single(add.f(1)).extend(multiply.f(2)).extend(length);

    // A lot of type handwaving to convince Java that this is all going to work
    final Apply<Unit, P2<F<String, Integer>, F<Integer, Integer>>, F<String, Integer>>
      comp1 = Apply.comp();
    final Apply<Unit, P2<F<Integer, Integer>, F<Integer, Integer>>, F<Integer, Integer>>
      comp0 = Apply.comp();
    final HFoldr<Unit, F<Integer, Integer>, HNil, F<Integer, Integer>>
      fold0 = HFoldr.hFoldr();
    final HFoldr<Unit, F<Integer, Integer>, HCons<F<String, Integer>,
      HCons<F<Integer, Integer>, HCons<F<Integer, Integer>, HNil>>>, F<String, Integer>>
      fold2 = HFoldr.hFoldr(comp1, HFoldr.hFoldr(comp0, HFoldr.hFoldr(comp0, fold0)));
    final F<Integer, Integer> id = identity();

    // Compose the list and apply the resulting function to a value.
    // Unit is used because composition has only one possible implementation.
    out.println(fold2.foldRight(unit(), id, functions).f("abc")); // 7
  }
}
