import static fj.data.TreeMap.empty;
import static fj.function.Integers.add;
import static fj.pre.Ord.stringOrd;
import fj.data.TreeMap;

public class TreeMap_Update {
  public static void main(String[] a) {
    TreeMap<String, Integer> map = empty(stringOrd);
    map = map.update("foo", add.f(1), 2);
    map = map.update("foo", add.f(3))._2();
    System.out.println(map.get("foo").some());
  }
}

