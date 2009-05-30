package fj.data;

import static fj.data.List.list;
import static java.util.Arrays.asList;
import static java.util.EnumSet.copyOf;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import fj.F;

/**
 * Functions that convert between types from the core Java API.
 *
 * @version %build.number%<br>
 *          <ul>
 *          <li>$LastChangedRevision$</li>
 *          <li>$LastChangedDate$</li>
 *          </ul>
 */
public final class Java {
  private Java() {
    throw new UnsupportedOperationException();
  }

  // BEGIN List ->

  /**
   * A function that converts lists to array lists.
   *
   * @return A function that converts lists to array lists.
   */
  public static <A> F<List<A>, ArrayList<A>> List_ArrayList() {
    return new F<List<A>, ArrayList<A>>() {
      public ArrayList<A> f(final List<A> as) {
        return new ArrayList<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts lists to array enum sets.
   *
   * @return A function that converts lists to enum sets.
   */
  public static <A extends Enum<A>> F<List<A>, EnumSet<A>> List_EnumSet() {
    return new F<List<A>, EnumSet<A>>() {
      public EnumSet<A> f(final List<A> as) {
        return copyOf(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts lists to hash sets.
   *
   * @return A function that converts lists to hash sets.
   */
  public static <A> F<List<A>, HashSet<A>> List_HashSet() {
    return new F<List<A>, HashSet<A>>() {
      public HashSet<A> f(final List<A> as) {
        return new HashSet<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts lists to linked hash sets.
   *
   * @return A function that converts lists to linked hash sets.
   */
  public static <A> F<List<A>, LinkedHashSet<A>> List_LinkedHashSet() {
    return new F<List<A>, LinkedHashSet<A>>() {
      public LinkedHashSet<A> f(final List<A> as) {
        return new LinkedHashSet<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts lists to linked lists.
   *
   * @return A function that converts lists to linked lists.
   */
  public static <A> F<List<A>, LinkedList<A>> List_LinkedList() {
    return new F<List<A>, LinkedList<A>>() {
      public LinkedList<A> f(final List<A> as) {
        return new LinkedList<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts lists to priority queues.
   *
   * @return A function that converts lists to priority queues.
   */
  public static <A> F<List<A>, PriorityQueue<A>> List_PriorityQueue() {
    return new F<List<A>, PriorityQueue<A>>() {
      public PriorityQueue<A> f(final List<A> as) {
        return new PriorityQueue<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts lists to stacks.
   *
   * @return A function that converts lists to stacks.
   */
  public static <A> F<List<A>, Stack<A>> List_Stack() {
    return new F<List<A>, Stack<A>>() {
      public Stack<A> f(final List<A> as) {
        final Stack<A> s = new Stack<A>();
        s.addAll(asList(as.toArray().array()));
        return s;
      }
    };
  }

  /**
   * A function that converts lists to stacks.
   *
   * @return A function that converts lists to stacks.
   */
  public static <A> F<List<A>, TreeSet<A>> List_TreeSet() {
    return new F<List<A>, TreeSet<A>>() {
      public TreeSet<A> f(final List<A> as) {
        return new TreeSet<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts lists to vectors.
   *
   * @return A function that converts lists to vectors.
   */
  public static <A> F<List<A>, Vector<A>> List_Vector() {
    return new F<List<A>, Vector<A>>() {
      @SuppressWarnings({"UseOfObsoleteCollectionType"})
      public Vector<A> f(final List<A> as) {
        return new Vector<A>(asList(as.toArray().array()));
      }
    };
  }

  // END List ->

  // BEGIN Array ->

  /**
   * A function that converts arrays to array lists.
   *
   * @return A function that converts arrays to array lists.
   */
  public static <A> F<Array<A>, ArrayList<A>> Array_ArrayList() {
    return new F<Array<A>, ArrayList<A>>() {
      public ArrayList<A> f(final Array<A> as) {
        return new ArrayList<A>(asList(as.array()));
      }
    };
  }

  /**
   * A function that converts arrays to enum sets.
   *
   * @return A function that converts arrays to enum sets.
   */
  public static <A extends Enum<A>> F<Array<A>, EnumSet<A>> Array_EnumSet() {
    return new F<Array<A>, EnumSet<A>>() {
      public EnumSet<A> f(final Array<A> as) {
        return copyOf(asList(as.array()));
      }
    };
  }

  /**
   * A function that converts arrays to hash sets.
   *
   * @return A function that converts arrays to hash sets.
   */
  public static <A> F<Array<A>, HashSet<A>> Array_HashSet() {
    return new F<Array<A>, HashSet<A>>() {
      public HashSet<A> f(final Array<A> as) {
        return new HashSet<A>(asList(as.array()));
      }
    };
  }

  /**
   * A function that converts arrays to linked hash sets.
   *
   * @return A function that converts arrays to linked hash sets.
   */
  public static <A> F<Array<A>, LinkedHashSet<A>> Array_LinkedHashSet() {
    return new F<Array<A>, LinkedHashSet<A>>() {
      public LinkedHashSet<A> f(final Array<A> as) {
        return new LinkedHashSet<A>(asList(as.array()));
      }
    };
  }

  /**
   * A function that converts arrays to linked lists.
   *
   * @return A function that converts arrays to linked lists.
   */
  public static <A> F<Array<A>, LinkedList<A>> Array_LinkedList() {
    return new F<Array<A>, LinkedList<A>>() {
      public LinkedList<A> f(final Array<A> as) {
        return new LinkedList<A>(asList(as.array()));
      }
    };
  }

  /**
   * A function that converts arrays to priority queues.
   *
   * @return A function that converts arrays to priority queues.
   */
  public static <A> F<Array<A>, PriorityQueue<A>> Array_PriorityQueue() {
    return new F<Array<A>, PriorityQueue<A>>() {
      public PriorityQueue<A> f(final Array<A> as) {
        return new PriorityQueue<A>(asList(as.array()));
      }
    };
  }

  /**
   * A function that converts arrays to stacks.
   *
   * @return A function that converts arrays to stacks.
   */
  public static <A> F<Array<A>, Stack<A>> Array_Stack() {
    return new F<Array<A>, Stack<A>>() {
      public Stack<A> f(final Array<A> as) {
        final Stack<A> s = new Stack<A>();
        s.addAll(asList(as.array()));
        return s;
      }
    };
  }

  /**
   * A function that converts arrays to tree sets.
   *
   * @return A function that converts arrays to tree sets.
   */
  public static <A> F<Array<A>, TreeSet<A>> Array_TreeSet() {
    return new F<Array<A>, TreeSet<A>>() {
      public TreeSet<A> f(final Array<A> as) {
        return new TreeSet<A>(asList(as.array()));
      }
    };
  }

  /**
   * A function that converts arrays to vectors.
   *
   * @return A function that converts arrays to vectors.
   */
  public static <A> F<Array<A>, Vector<A>> Array_Vector() {
    return new F<Array<A>, Vector<A>>() {
      @SuppressWarnings({"UseOfObsoleteCollectionType"})
      public Vector<A> f(final Array<A> as) {
        return new Vector<A>(asList(as.array()));
      }
    };
  }

  // END Array ->

  // BEGIN Stream ->

  /**
   * A function that converts streams to iterable.
   *
   * @return A function that converts streams to iterable.
   */
  public static <A> F<Stream<A>, Iterable<A>> Stream_Iterable() {
    return new F<Stream<A>, Iterable<A>>() {
      public Iterable<A> f(final Stream<A> as) {
        return new Iterable<A>() {
          public Iterator<A> iterator() {
            return new Iterator<A>() {
              private Stream<A> x = as;

              public boolean hasNext() {
                return x.isNotEmpty();
              }

              public A next() {
                if (x.isEmpty())
                  throw new NoSuchElementException("Empty iterator");
                else {
                  final A a = x.head();
                  x = x.tail()._1();
                  return a;
                }
              }

              public void remove() {
                throw new UnsupportedOperationException();
              }
            };
          }
        };
      }
    };
  }

  /**
   * A function that converts streams to array lists.
   *
   * @return A function that converts streams to array lists.
   */
  public static <A> F<Stream<A>, ArrayList<A>> Stream_ArrayList() {
    return new F<Stream<A>, ArrayList<A>>() {
      public ArrayList<A> f(final Stream<A> as) {
        return new ArrayList<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts streams to enum sets.
   *
   * @return A function that converts streams to enum sets.
   */
  public static <A extends Enum<A>> F<Stream<A>, EnumSet<A>> Stream_EnumSet() {
    return new F<Stream<A>, EnumSet<A>>() {
      public EnumSet<A> f(final Stream<A> as) {
        return copyOf(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts streams to hash sets.
   *
   * @return A function that converts streams to hash sets.
   */
  public static <A> F<Stream<A>, HashSet<A>> Stream_HashSet() {
    return new F<Stream<A>, HashSet<A>>() {
      public HashSet<A> f(final Stream<A> as) {
        return new HashSet<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts streams to linked hash sets.
   *
   * @return A function that converts streams to linked hash sets.
   */
  public static <A> F<Stream<A>, LinkedHashSet<A>> Stream_LinkedHashSet() {
    return new F<Stream<A>, LinkedHashSet<A>>() {
      public LinkedHashSet<A> f(final Stream<A> as) {
        return new LinkedHashSet<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts streams to linked lists.
   *
   * @return A function that converts streams to linked lists.
   */
  public static <A> F<Stream<A>, LinkedList<A>> Stream_LinkedList() {
    return new F<Stream<A>, LinkedList<A>>() {
      public LinkedList<A> f(final Stream<A> as) {
        return new LinkedList<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts streams to priority queues.
   *
   * @return A function that converts streams to priority queues.
   */
  public static <A> F<Stream<A>, PriorityQueue<A>> Stream_PriorityQueue() {
    return new F<Stream<A>, PriorityQueue<A>>() {
      public PriorityQueue<A> f(final Stream<A> as) {
        return new PriorityQueue<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts streams to stacks.
   *
   * @return A function that converts streams to stacks.
   */
  public static <A> F<Stream<A>, Stack<A>> Stream_Stack() {
    return new F<Stream<A>, Stack<A>>() {
      public Stack<A> f(final Stream<A> as) {
        final Stack<A> s = new Stack<A>();
        s.addAll(asList(as.toArray().array()));
        return s;
      }
    };
  }

  /**
   * A function that converts streams to tree sets.
   *
   * @return A function that converts streams to tree sets.
   */
  public static <A> F<Stream<A>, TreeSet<A>> Stream_TreeSet() {
    return new F<Stream<A>, TreeSet<A>>() {
      public TreeSet<A> f(final Stream<A> as) {
        return new TreeSet<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts streams to vectors.
   *
   * @return A function that converts streams to vectors.
   */
  public static <A> F<Stream<A>, Vector<A>> Stream_Vector() {
    return new F<Stream<A>, Vector<A>>() {
      @SuppressWarnings({"UseOfObsoleteCollectionType"})
      public Vector<A> f(final Stream<A> as) {
        return new Vector<A>(asList(as.toArray().array()));
      }
    };
  }

  // END Stream ->

  // BEGIN Option ->

  /**
   * A function that converts options to array lists.
   *
   * @return A function that converts options to array lists.
   */
  public static <A> F<Option<A>, ArrayList<A>> Option_ArrayList() {
    return new F<Option<A>, ArrayList<A>>() {
      public ArrayList<A> f(final Option<A> as) {
        return new ArrayList<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts options to enum sets.
   *
   * @return A function that converts options to enum sets.
   */
  public static <A extends Enum<A>> F<Option<A>, EnumSet<A>> Option_EnumSet() {
    return new F<Option<A>, EnumSet<A>>() {
      public EnumSet<A> f(final Option<A> as) {
        return copyOf(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts options to hash sets.
   *
   * @return A function that converts options to hash sets.
   */
  public static <A> F<Option<A>, HashSet<A>> Option_HashSet() {
    return new F<Option<A>, HashSet<A>>() {
      public HashSet<A> f(final Option<A> as) {
        return new HashSet<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts options to linked hash sets.
   *
   * @return A function that converts options to linked hash sets.
   */
  public static <A> F<Option<A>, LinkedHashSet<A>> Option_LinkedHashSet() {
    return new F<Option<A>, LinkedHashSet<A>>() {
      public LinkedHashSet<A> f(final Option<A> as) {
        return new LinkedHashSet<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts options to linked lists.
   *
   * @return A function that converts options to linked lists.
   */
  public static <A> F<Option<A>, LinkedList<A>> Option_LinkedList() {
    return new F<Option<A>, LinkedList<A>>() {
      public LinkedList<A> f(final Option<A> as) {
        return new LinkedList<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts options to priority queues.
   *
   * @return A function that converts options to priority queues.
   */
  public static <A> F<Option<A>, PriorityQueue<A>> Option_PriorityQueue() {
    return new F<Option<A>, PriorityQueue<A>>() {
      public PriorityQueue<A> f(final Option<A> as) {
        return new PriorityQueue<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts options to stacks.
   *
   * @return A function that converts options to stacks.
   */
  public static <A> F<Option<A>, Stack<A>> Option_Stack() {
    return new F<Option<A>, Stack<A>>() {
      public Stack<A> f(final Option<A> as) {
        final Stack<A> s = new Stack<A>();
        s.addAll(asList(as.toArray().array()));
        return s;
      }
    };
  }

  /**
   * A function that converts options to tree sets.
   *
   * @return A function that converts options to tree sets.
   */
  public static <A> F<Option<A>, TreeSet<A>> Option_TreeSet() {
    return new F<Option<A>, TreeSet<A>>() {
      public TreeSet<A> f(final Option<A> as) {
        return new TreeSet<A>(asList(as.toArray().array()));
      }
    };
  }

  /**
   * A function that converts options to vectors.
   *
   * @return A function that converts options to vectors.
   */
  public static <A> F<Option<A>, Vector<A>> Option_Vector() {
    return new F<Option<A>, Vector<A>>() {
      @SuppressWarnings({"UseOfObsoleteCollectionType"})
      public Vector<A> f(final Option<A> as) {
        return new Vector<A>(asList(as.toArray().array()));
      }
    };
  }

  // END Option ->

  // BEGIN Either ->

  /**
   * A function that converts eithers to array lists.
   *
   * @return A function that converts eithers to array lists.
   */
  public static <A, B> F<Either<A, B>, ArrayList<A>> Either_ArrayListA() {
    return fj.Function.compose(Java.<A>Option_ArrayList(), Conversions.<A, B>Either_OptionA());
  }

  /**
   * A function that converts eithers to array lists.
   *
   * @return A function that converts eithers to array lists.
   */
  public static <A, B> F<Either<A, B>, ArrayList<B>> Either_ArrayListB() {
    return fj.Function.compose(Java.<B>Option_ArrayList(), Conversions.<A, B>Either_OptionB());
  }

  /**
   * A function that converts eithers to enum sets.
   *
   * @return A function that converts eithers to enum sets.
   */
  public static <A extends Enum<A>, B> F<Either<A, B>, EnumSet<A>> Either_EnumSetA() {
    return fj.Function.compose(Java.<A>Option_EnumSet(), Conversions.<A, B>Either_OptionA());
  }

  /**
   * A function that converts eithers to enum sets.
   *
   * @return A function that converts eithers to enum sets.
   */
  public static <A, B extends Enum<B>> F<Either<A, B>, EnumSet<B>> Either_EnumSetB() {
    return fj.Function.compose(Java.<B>Option_EnumSet(), Conversions.<A, B>Either_OptionB());
  }

  /**
   * A function that converts eithers to hash sets.
   *
   * @return A function that converts eithers to hash sets.
   */
  public static <A, B> F<Either<A, B>, HashSet<A>> Either_HashSetA() {
    return fj.Function.compose(Java.<A>Option_HashSet(), Conversions.<A, B>Either_OptionA());
  }

  /**
   * A function that converts eithers to hash sets.
   *
   * @return A function that converts eithers to hash sets.
   */
  public static <A, B> F<Either<A, B>, HashSet<B>> Either_HashSetB() {
    return fj.Function.compose(Java.<B>Option_HashSet(), Conversions.<A, B>Either_OptionB());
  }

  /**
   * A function that converts eithers to linked hash sets.
   *
   * @return A function that converts eithers to linked hash sets.
   */
  public static <A, B> F<Either<A, B>, LinkedHashSet<A>> Either_LinkedHashSetA() {
    return fj.Function.compose(Java.<A>Option_LinkedHashSet(), Conversions.<A, B>Either_OptionA());
  }

  /**
   * A function that converts eithers to linked hash sets.
   *
   * @return A function that converts eithers to linked hash sets.
   */
  public static <A, B> F<Either<A, B>, LinkedHashSet<B>> Either_LinkedHashSetB() {
    return fj.Function.compose(Java.<B>Option_LinkedHashSet(), Conversions.<A, B>Either_OptionB());
  }

  /**
   * A function that converts eithers to linked lists.
   *
   * @return A function that converts eithers to linked lists.
   */
  public static <A, B> F<Either<A, B>, LinkedList<A>> Either_LinkedListA() {
    return fj.Function.compose(Java.<A>Option_LinkedList(), Conversions.<A, B>Either_OptionA());
  }

  /**
   * A function that converts eithers to priority queues.
   *
   * @return A function that eithers options to priority queues.
   */
  public static <A, B> F<Either<A, B>, PriorityQueue<A>> Option_PriorityQueueA() {
    return fj.Function.compose(Java.<A>Option_PriorityQueue(), Conversions.<A, B>Either_OptionA());
  }

  /**
   * A function that converts eithers to priority queues.
   *
   * @return A function that eithers options to priority queues.
   */
  public static <A, B> F<Either<A, B>, PriorityQueue<B>> Option_PriorityQueueB() {
    return fj.Function.compose(Java.<B>Option_PriorityQueue(), Conversions.<A, B>Either_OptionB());
  }

  /**
   * A function that converts eithers to linked lists.
   *
   * @return A function that converts eithers to linked lists.
   */
  public static <A, B> F<Either<A, B>, LinkedList<B>> Either_LinkedListB() {
    return fj.Function.compose(Java.<B>Option_LinkedList(), Conversions.<A, B>Either_OptionB());
  }

  /**
   * A function that converts eithers to stacks.
   *
   * @return A function that converts eithers to stacks.
   */
  public static <A, B> F<Either<A, B>, Stack<A>> Either_StackA() {
    return fj.Function.compose(Java.<A>Option_Stack(), Conversions.<A, B>Either_OptionA());
  }

  /**
   * A function that converts eithers to stacks.
   *
   * @return A function that converts eithers to stacks.
   */
  public static <A, B> F<Either<A, B>, Stack<B>> Either_StackB() {
    return fj.Function.compose(Java.<B>Option_Stack(), Conversions.<A, B>Either_OptionB());
  }

  /**
   * A function that converts eithers to tree sets.
   *
   * @return A function that converts eithers to tree sets.
   */
  public static <A, B> F<Either<A, B>, TreeSet<A>> Either_TreeSetA() {
    return fj.Function.compose(Java.<A>Option_TreeSet(), Conversions.<A, B>Either_OptionA());
  }

  /**
   * A function that converts eithers to tree sets.
   *
   * @return A function that converts eithers to tree sets.
   */
  public static <A, B> F<Either<A, B>, TreeSet<B>> Either_TreeSetB() {
    return fj.Function.compose(Java.<B>Option_TreeSet(), Conversions.<A, B>Either_OptionB());
  }

  /**
   * A function that converts eithers to vectors.
   *
   * @return A function that converts eithers to vectors.
   */
  public static <A, B> F<Either<A, B>, Vector<A>> Either_VectorA() {
    return fj.Function.compose(Java.<A>Option_Vector(), Conversions.<A, B>Either_OptionA());
  }

  /**
   * A function that converts eithers to vectors.
   *
   * @return A function that converts eithers to vectors.
   */
  public static <A, B> F<Either<A, B>, Vector<B>> Either_VectorB() {
    return fj.Function.compose(Java.<B>Option_Vector(), Conversions.<A, B>Either_OptionB());
  }

  // END Either ->

  // BEGIN String ->

  /**
   * A function that converts strings to array lists.
   */
  public static final F<String, ArrayList<Character>> String_ArrayList =
      fj.Function.compose(Java.<Character>List_ArrayList(), Conversions.String_List);

  /**
   * A function that converts strings to hash sets.
   */
  public static final F<String, HashSet<Character>> String_HashSet =
      fj.Function.compose(Java.<Character>List_HashSet(), Conversions.String_List);

  /**
   * A function that converts strings to linked hash sets.
   */
  public static final F<String, LinkedHashSet<Character>> String_LinkedHashSet =
      fj.Function.compose(Java.<Character>List_LinkedHashSet(), Conversions.String_List);

  /**
   * A function that converts strings to linked lists.
   */
  public static final F<String, LinkedList<Character>> String_LinkedList =
      fj.Function.compose(Java.<Character>List_LinkedList(), Conversions.String_List);

  /**
   * A function that converts strings to priority queues.
   */
  public static final F<String, PriorityQueue<Character>> String_PriorityQueue =
      fj.Function.compose(Java.<Character>List_PriorityQueue(), Conversions.String_List);

  /**
   * A function that converts strings to stacks.
   */
  public static final F<String, Stack<Character>> String_Stack =
      fj.Function.compose(Java.<Character>List_Stack(), Conversions.String_List);

  /**
   * A function that converts strings to tree sets.
   */
  public static final F<String, TreeSet<Character>> String_TreeSet =
      fj.Function.compose(Java.<Character>List_TreeSet(), Conversions.String_List);

  /**
   * A function that converts strings to vectors.
   */
  public static final F<String, Vector<Character>> String_Vector =
      fj.Function.compose(Java.<Character>List_Vector(), Conversions.String_List);

  // END String ->

  // BEGIN StringBuffer ->

  /**
   * A function that converts string buffers to array lists.
   */
  public static final F<StringBuffer, ArrayList<Character>> StringBuffer_ArrayList =
      fj.Function.compose(Java.<Character>List_ArrayList(), Conversions.StringBuffer_List);

  /**
   * A function that converts string buffers to hash sets.
   */
  public static final F<StringBuffer, HashSet<Character>> StringBuffer_HashSet =
      fj.Function.compose(Java.<Character>List_HashSet(), Conversions.StringBuffer_List);

  /**
   * A function that converts string buffers to linked hash sets.
   */
  public static final F<StringBuffer, LinkedHashSet<Character>> StringBuffer_LinkedHashSet =
      fj.Function.compose(Java.<Character>List_LinkedHashSet(), Conversions.StringBuffer_List);

  /**
   * A function that converts string buffers to linked lists.
   */
  public static final F<StringBuffer, LinkedList<Character>> StringBuffer_LinkedList =
      fj.Function.compose(Java.<Character>List_LinkedList(), Conversions.StringBuffer_List);

  /**
   * A function that converts string buffers to priority queues.
   */
  public static final F<StringBuffer, PriorityQueue<Character>> StringBuffer_PriorityQueue =
      fj.Function.compose(Java.<Character>List_PriorityQueue(), Conversions.StringBuffer_List);

  /**
   * A function that converts string buffers to stacks.
   */
  public static final F<StringBuffer, Stack<Character>> StringBuffer_Stack =
      fj.Function.compose(Java.<Character>List_Stack(), Conversions.StringBuffer_List);

  /**
   * A function that converts string buffers to tree sets.
   */
  public static final F<StringBuffer, TreeSet<Character>> StringBuffer_TreeSet =
      fj.Function.compose(Java.<Character>List_TreeSet(), Conversions.StringBuffer_List);

  /**
   * A function that converts string buffers to vectors.
   */
  public static final F<StringBuffer, Vector<Character>> StringBuffer_Vector =
      fj.Function.compose(Java.<Character>List_Vector(), Conversions.StringBuffer_List);

  // END StringBuffer ->

  // BEGIN StringBuilder ->

  /**
   * A function that converts string builders to array lists.
   */
  public static final F<StringBuilder, ArrayList<Character>> StringBuilder_ArrayList =
      fj.Function.compose(Java.<Character>List_ArrayList(), Conversions.StringBuilder_List);

  /**
   * A function that converts string builders to hash sets.
   */
  public static final F<StringBuilder, HashSet<Character>> StringBuilder_HashSet =
      fj.Function.compose(Java.<Character>List_HashSet(), Conversions.StringBuilder_List);

  /**
   * A function that converts string builders to linked hash sets.
   */
  public static final F<StringBuilder, LinkedHashSet<Character>> StringBuilder_LinkedHashSet =
      fj.Function.compose(Java.<Character>List_LinkedHashSet(), Conversions.StringBuilder_List);

  /**
   * A function that converts string builders to linked lists.
   */
  public static final F<StringBuilder, LinkedList<Character>> StringBuilder_LinkedList =
      fj.Function.compose(Java.<Character>List_LinkedList(), Conversions.StringBuilder_List);

  /**
   * A function that converts string builders to priority queues.
   */
  public static final F<StringBuilder, PriorityQueue<Character>> StringBuilder_PriorityQueue =
      fj.Function.compose(Java.<Character>List_PriorityQueue(), Conversions.StringBuilder_List);

  /**
   * A function that converts string builders to stacks.
   */
  public static final F<StringBuilder, Stack<Character>> StringBuilder_Stack =
      fj.Function.compose(Java.<Character>List_Stack(), Conversions.StringBuilder_List);

  /**
   * A function that converts string builders to tree sets.
   */
  public static final F<StringBuilder, TreeSet<Character>> StringBuilder_TreeSet =
      fj.Function.compose(Java.<Character>List_TreeSet(), Conversions.StringBuilder_List);

  /**
   * A function that converts string builders to vectors.
   */
  public static final F<StringBuilder, Vector<Character>> StringBuilder_Vector =
      fj.Function.compose(Java.<Character>List_Vector(), Conversions.StringBuilder_List);

  // END StringBuffer ->

  // BEGIN ArrayList ->

  /**
   * A function that converts array lists to lists.
   *
   * @return A function that converts array lists to lists.
   */
  public static <A> F<ArrayList<A>, List<A>> ArrayList_List() {
    return new F<ArrayList<A>, List<A>>() {
      @SuppressWarnings({"unchecked"})
      public List<A> f(final ArrayList<A> as) {
        return list(as.toArray((A[]) new Object[as.size()]));
      }
    };
  }

  // todo

  // END ArrayList ->

  // BEGIN EnumSet ->

  /**
   * A function that converts enum sets to lists.
   *
   * @return A function that converts enum sets to lists.
   */
  public static <A extends Enum<A>> F<EnumSet<A>, List<A>> EnumSet_List() {
    return new F<EnumSet<A>, List<A>>() {
      @SuppressWarnings({"unchecked"})
      public List<A> f(final EnumSet<A> as) {
        return list(as.toArray((A[]) new Object[as.size()]));
      }
    };
  }

  // todo

  // END EnumSet ->

  // BEGIN HashSet ->

  /**
   * A function that converts hash sets to lists.
   *
   * @return A function that converts hash sets to lists.
   */
  public static <A> F<HashSet<A>, List<A>> HashSet_List() {
    return new F<HashSet<A>, List<A>>() {
      @SuppressWarnings({"unchecked"})
      public List<A> f(final HashSet<A> as) {
        return list(as.toArray((A[]) new Object[as.size()]));
      }
    };
  }

  // todo

  // END HashSet ->

  // BEGIN LinkedHashSet ->

  /**
   * A function that converts linked hash sets to lists.
   *
   * @return A function that converts linked hash sets to lists.
   */
  public static <A> F<LinkedHashSet<A>, List<A>> LinkedHashSet_List() {
    return new F<LinkedHashSet<A>, List<A>>() {
      @SuppressWarnings({"unchecked"})
      public List<A> f(final LinkedHashSet<A> as) {
        return list(as.toArray((A[]) new Object[as.size()]));
      }
    };
  }

  // todo

  // END LinkedHashSet ->

  // BEGIN Linked List ->

  /**
   * A function that converts linked lists to lists.
   *
   * @return A function that converts linked lists to lists.
   */
  public static <A> F<LinkedList<A>, List<A>> LinkedList_List() {
    return new F<LinkedList<A>, List<A>>() {
      @SuppressWarnings({"unchecked"})
      public List<A> f(final LinkedList<A> as) {
        return list(as.toArray((A[]) new Object[as.size()]));
      }
    };
  }

  // todo

  // END Linked List ->

  // BEGIN PriorityQueue ->

  /**
   * A function that converts priority queues to lists.
   *
   * @return A function that converts priority queues to lists.
   */
  public static <A> F<PriorityQueue<A>, List<A>> PriorityQueue_List() {
    return new F<PriorityQueue<A>, List<A>>() {
      @SuppressWarnings({"unchecked"})
      public List<A> f(final PriorityQueue<A> as) {
        return list(as.toArray((A[]) new Object[as.size()]));
      }
    };
  }

  // todo

  // END PriorityQueue ->

  // BEGIN Stack ->

  /**
   * A function that converts stacks to lists.
   *
   * @return A function that converts stacks to lists.
   */
  public static <A> F<Stack<A>, List<A>> Stack_List() {
    return new F<Stack<A>, List<A>>() {
      @SuppressWarnings({"unchecked"})
      public List<A> f(final Stack<A> as) {
        return list(as.toArray((A[]) new Object[as.size()]));
      }
    };
  }

  // todo

  // END Stack ->

  // BEGIN TreeSet ->

  /**
   * A function that converts tree sets to lists.
   *
   * @return A function that converts tree sets to lists.
   */
  public static <A> F<TreeSet<A>, List<A>> TreeSet_List() {
    return new F<TreeSet<A>, List<A>>() {
      @SuppressWarnings({"unchecked"})
      public List<A> f(final TreeSet<A> as) {
        return list(as.toArray((A[]) new Object[as.size()]));
      }
    };
  }

  // todo

  // END TreeSet ->

  // BEGIN Vector ->

  /**
   * A function that converts vectors to lists.
   *
   * @return A function that converts vectors to lists.
   */
  public static <A> F<Vector<A>, List<A>> Vector_List() {
    return new F<Vector<A>, List<A>>() {
      @SuppressWarnings({"unchecked", "UseOfObsoleteCollectionType"})
      public List<A> f(final Vector<A> as) {
        return list(as.toArray((A[]) new Object[as.size()]));
      }
    };
  }

  // todo

  // END Vector ->

}
