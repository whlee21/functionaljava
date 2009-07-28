import fj.test.Gen;
import fj.test.Arbitrary;
import fj.test.Rand;
import fj.P2;
import fj.P;
import fj.Unit;
import fj.control.parallel.Strategy;
import static fj.pre.Ord.*;
import static fj.data.Array.array;
import fj.data.Stream;
import fj.data.List;
import fj.data.Enumerator;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Scratch {

  public static void quicksort(float[] main, int[] index) {
    quicksort(main, index, 0, index.length - 1);
  }

  // quicksort a[left] to a[right]
  public static void quicksort(float[] a, int[] index, int left, int right) {
    if (right <= left) return;
    int i = partition(a, index, left, right);
    quicksort(a, index, left, i - 1);
    quicksort(a, index, i + 1, right);
  }

  // partition a[left] to a[right], assumes left < right
  private static int partition(float[] a, int[] index,
                               int left, int right) {
    int i = left - 1;
    int j = right;
    while (true) {
      while (less(a[++i], a[right]))      // find item on left to swap
        ;                               // a[right] acts as sentinel
      while (less(a[right], a[--j]))      // find item on right to swap
        if (j == left) break;           // don't go out-of-bounds
      if (i >= j) break;                  // check if pointers cross
      exch(a, index, i, j);               // swap two elements into place
    }
    exch(a, index, i, right);               // swap with partition element
    return i;
  }

  // is x < y ?
  private static boolean less(float x, float y) {
    return (x < y);
  }

  // exchange a[i] and a[j]
  private static void exch(float[] a, int[] index, int i, int j) {
    float swap = a[i];
    a[i] = a[j];
    a[j] = swap;
    int b = index[i];
    index[i] = index[j];
    index[j] = b;
  }

  public static void main(String[] args) {
    final Gen<Float> fg = Arbitrary.arbFloat.gen;
    final float[] d1 = new float[10000];
    final Float[] d2 = new Float[10000];
    for (int i = 0; i < d1.length; i++) {
      final float f = fg.gen(10000, Rand.standard);
      d1[i] = f;
      d2[i] = f;
    }

    final Date start = new Date();

    final int[] ints = new int[10000];
    quicksort(d1, ints);
    System.out.println(new Date().getTime() - start.getTime());

    final Date start1 = new Date();
    final ExecutorService pool = Executors.newCachedThreadPool();
    final Strategy<Unit> s = Strategy.executorStrategy(pool);
    final Stream<Float> fs = array(d2).toStream();
    System.out.println(new Date().getTime() - start1.getTime());
    final Stream<P2<Float, Integer>> fs2 = fs.zipIndex();
    System.out.println(new Date().getTime() - start1.getTime());
    final Stream<P2<Float, Integer>> fs3 = fs2.qsort(p2Ord(floatOrd, intOrd), s);
    System.out.println(new Date().getTime() - start1.getTime());
    fs3.map(P2.<Float, Integer>__2()).toArray();
    System.out.println(new Date().getTime() - start1.getTime());

    /* Stream<P2<String, Integer>> x = Stream.range(1, 1000).map(P.<String, Integer>p2().f("g"));
System.out.println(new Date().getTime() - start.getTime());
x.qsort(p2Ord(stringOrd, intOrd));
System.out.println(new Date().getTime() - start.getTime()); */
  }

}
