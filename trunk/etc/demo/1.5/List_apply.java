import fj.F;
import fj.data.List;
import static fj.data.List.list;
import static fj.function.Integers.*;
import static fj.pre.Show.intShow;
import static fj.pre.Show.listShow;

public class List_apply {
  public static void main(final String[] a) {
    final List<F<Integer, Integer>> fs = list(add.f(2), multiply.f(2), subtract.f(2));
    final List<Integer> three = list(3);
    listShow(intShow).println(three.apply(fs)); // Prints out: <5,6,-1>
  }
}