import static fj.pre.Show.intShow;
import static fj.pre.Show.unlineShow;
import static fj.data.Stream.range;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.Integer.MAX_VALUE;

public class Stream_Test
  {public static void main(final String[] args)
    {unlineShow(intShow).println(range(MIN_VALUE, MAX_VALUE + 1L));}}
