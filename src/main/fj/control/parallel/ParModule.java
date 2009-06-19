package fj.control.parallel;

import fj.Effect;
import fj.F;
import fj.F2;
import fj.Function;
import fj.P1;
import fj.P2;
import fj.Unit;
import static fj.F2W.$$;
import static fj.FW.$;
import static fj.Function.curry;
import static fj.Function.uncurryF2;
import static fj.control.parallel.Promise.liftM2;
import fj.data.Array;
import fj.data.IterableW;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import static fj.data.Option.some;
import static fj.data.Stream.iterableStream;
import fj.pre.Monoid;

/**
 * A module of higher-order concurrency features.
 */
public final class ParModule {
  private final Strategy<Unit> strategy;

  private ParModule(final Strategy<Unit> strategy) {
    this.strategy = strategy;
  }

  /**
   * Constructor method for ParModule
   *
   * @param u A parallel strategy for the module.
   * @return A ParModule that uses the given strategy for parallelism.
   */
  public static ParModule parModule(final Strategy<Unit> u) {
    return new ParModule(u);
  }

  /**
   * Evaluates the given product concurrently and returns a Promise of the result.
   *
   * @param p A product to evaluate concurrently.
   * @return A Promise of the value of the given product, that can be claimed in the future.
   */
  public <A> Promise<A> promise(final P1<A> p) {
    return Promise.promise(strategy, p);
  }

  /**
   * Returns a function that evaluates a given product concurrently and returns a Promise of the result.
   *
   * @return a function that evaluates a given product concurrently and returns a Promise of the result.
   */
  public <A> F<P1<A>, Promise<A>> promise() {
    return new F<P1<A>, Promise<A>>() {
      public Promise<A> f(final P1<A> ap1) {
        return promise(ap1);
      }
    };
  }

  /**
   * Promotes the given function to a concurrent function that returns a Promise.
   *
   * @param f A given function to promote to a concurrent function.
   * @return A function that is applied concurrently when given an argument, yielding a Promise of the result
   *         that can be claimed in the future.
   */
  public <A, B> F<A, Promise<B>> promise(final F<A, B> f) {
    return $(f).promise(strategy);
  }

  /**
   * Returns a function that promotes a given function to a concurrent function that returns a Promise.
   * The pure Kleisli arrow of Promise.
   *
   * @return A higher-order function that takes pure functions to promise-valued functions.
   */
  public <A, B> F<F<A, B>, F<A, Promise<B>>> promisePure() {
    return new F<F<A, B>, F<A, Promise<B>>>() {
      public F<A, Promise<B>> f(final F<A, B> abf) {
        return promise(abf);
      }
    };
  }

  /**
   * Promotes the given function to a concurrent function that returns a Promise.
   *
   * @param f A given function to promote to a concurrent function.
   * @return A function that is applied concurrently when given an argument, yielding a Promise of the result
   *         that can be claimed in the future.
   */
  public <A, B, C> F2<A, B, Promise<C>> promise(final F2<A, B, C> f) {
    return P2.untuple($$(f).tuple().promise(strategy));
  }


  /**
   * Creates a very fast concurrent effect, as an actor that does not guarantee ordering of its messages.
   * Such an actor is not thread-safe unless the given Effect is.
   *
   * @param e The effect that the actor should have on its messages.
   * @return A concurrent actor that does not guarantee ordering of its messages.
   */
  public <A> Actor<A> effect(final Effect<A> e) {
    return Actor.actor(strategy, e);
  }

  /**
   * A first-class constructor of concurrent effects, as actors that don't guarantee ordering of messages.
   * Such an actor is not thread-safe unless the given Effect is.
   *
   * @return A function that takes an effect and returns a concurrent effect.
   */
  public <A> F<Effect<A>, Actor<A>> effect() {
    return new F<Effect<A>, Actor<A>>() {
      public Actor<A> f(final Effect<A> effect) {
        return effect(effect);
      }
    };
  }

  /**
   * Creates a concurrent actor that is guaranteed to process only one message at a time.
   *
   * @param e The effect that the actor should have on its messages.
   * @return A concurrent actor that is guaranteed to process its messages in some order.
   */
  public <A> QueueActor<A> actor(final Effect<A> e) {
    return QueueActor.queueActor(strategy, e);
  }

  /**
   * A first-class constructor of actors.
   *
   * @return A function that takes an effect and returns an actor that processes messages in some order.
   */
  public <A> F<Effect<A>, QueueActor<A>> actor() {
    return new F<Effect<A>, QueueActor<A>>() {
      public QueueActor<A> f(final Effect<A> effect) {
        return actor(effect);
      }
    };
  }

  /**
   * List iteration inside a Promise. Traverses a List of Promises yielding a Promise of a List.
   *
   * @param ps A list of promises to sequence.
   * @return A promise of the List of values promised by the list of promises.
   */
  public <A> Promise<List<A>> sequence(final List<Promise<A>> ps) {
    return Promise.sequence(strategy, ps);
  }

  /**
   * A first-class function that traverses a list inside a promise.
   *
   * @return A first-class function that traverses a list inside a promise.
   */
  public <A> F<List<Promise<A>>, Promise<List<A>>> sequenceList() {
    return new F<List<Promise<A>>, Promise<List<A>>>() {
      public Promise<List<A>> f(final List<Promise<A>> list) {
        return sequence(list);
      }
    };
  }

  /**
   * Stream iteration inside a Promise. Traverses a Stream of Promises yielding a Promise of a Stream.
   *
   * @param ps A Stream of promises to sequence.
   * @return A promise of the Stream of values promised by the Stream of promises.
   */
  public <A> Promise<Stream<A>> sequence(final Stream<Promise<A>> ps) {
    return Promise.sequence(strategy, ps);
  }

  /**
   * A first-class function that traverses a stream inside a promise.
   *
   * @return A first-class function that traverses a stream inside a promise.
   */
  public <A> F<Stream<Promise<A>>, Promise<Stream<A>>> sequenceStream() {
    return new F<Stream<Promise<A>>, Promise<Stream<A>>>() {
      public Promise<Stream<A>> f(final Stream<Promise<A>> stream) {
        return sequence(stream);
      }
    };
  }

  /**
   * Takes a Promise-valued function and applies it to each element
   * in the given List, yielding a promise of a List of results.
   *
   * @param as A list to map across.
   * @param f  A promise-valued function to map across the list.
   * @return A Promise of a new list with the given function applied to each element.
   */
  public <A, B> Promise<List<B>> mapM(final List<A> as, final F<A, Promise<B>> f) {
    return sequence(as.map(f));
  }

  /**
   * First-class function that maps a concurrent function over a List inside a promise.
   *
   * @return a function that maps a concurrent function over a List inside a promise.
   */
  public <A, B> F<F<A, Promise<B>>, F<List<A>, Promise<List<B>>>> mapList() {
    return curry(new F2<F<A, Promise<B>>, List<A>, Promise<List<B>>>() {
      public Promise<List<B>> f(final F<A, Promise<B>> f, final List<A> list) {
        return mapM(list, f);
      }
    });
  }

  /**
   * Takes a Promise-valued function and applies it to each element
   * in the given Stream, yielding a promise of a Stream of results.
   *
   * @param as A Stream to map across.
   * @param f  A promise-valued function to map across the Stream.
   * @return A Promise of a new Stream with the given function applied to each element.
   */
  public <A, B> Promise<Stream<B>> mapM(final Stream<A> as, final F<A, Promise<B>> f) {
    return sequence(as.map(f));
  }

  /**
   * First-class function that maps a concurrent function over a Stream inside a promise.
   *
   * @return a function that maps a concurrent function over a Stream inside a promise.
   */
  public <A, B> F<F<A, Promise<B>>, F<Stream<A>, Promise<Stream<B>>>> mapStream() {
    return curry(new F2<F<A, Promise<B>>, Stream<A>, Promise<Stream<B>>>() {
      public Promise<Stream<B>> f(final F<A, Promise<B>> f, final Stream<A> stream) {
        return mapM(stream, f);
      }
    });
  }

  /**
   * Maps across a list in parallel.
   *
   * @param as A list to map across in parallel.
   * @param f  A function to map across the given list.
   * @return A Promise of a new list with the given function applied to each element.
   */
  public <A, B> Promise<List<B>> parMap(final List<A> as, final F<A, B> f) {
    return mapM(as, promise(f));
  }

  /**
   * A first-class function that maps another function across a list in parallel.
   *
   * @return A function that maps another function across a list in parallel.
   */
  public <A, B> F<F<A, B>, F<List<A>, Promise<List<B>>>> parMapList() {
    return curry(new F2<F<A, B>, List<A>, Promise<List<B>>>() {
      public Promise<List<B>> f(final F<A, B> abf, final List<A> list) {
        return parMap(list, abf);
      }
    });
  }

  /**
   * Maps across a Stream in parallel.
   *
   * @param as A Stream to map across in parallel.
   * @param f  A function to map across the given Stream.
   * @return A Promise of a new Stream with the given function applied to each element.
   */
  public <A, B> Promise<Stream<B>> parMap(final Stream<A> as, final F<A, B> f) {
    return mapM(as, promise(f));
  }

  /**
   * A first-class function that maps another function across a stream in parallel.
   *
   * @return A function that maps another function across a stream in parallel.
   */
  public <A, B> F<F<A, B>, F<Stream<A>, Promise<Stream<B>>>> parMapStream() {
    return curry(new F2<F<A, B>, Stream<A>, Promise<Stream<B>>>() {
      public Promise<Stream<B>> f(final F<A, B> abf, final Stream<A> stream) {
        return parMap(stream, abf);
      }
    });
  }

  /**
   * Maps across an Iterable in parallel.
   *
   * @param as An Iterable to map across in parallel.
   * @param f  A function to map across the given Iterable.
   * @return A Promise of a new Iterable with the given function applied to each element.
   */
  public <A, B> Promise<Iterable<B>> parMap(final Iterable<A> as, final F<A, B> f) {
    return parMap(iterableStream(as), f)
        .fmap(Function.<Stream<B>, Iterable<B>>vary(Function.<Stream<B>>identity()));
  }

  /**
   * A first-class function that maps another function across an iterable in parallel.
   *
   * @return A function that maps another function across an iterable in parallel.
   */
  public <A, B> F<F<A, B>, F<Iterable<A>, Promise<Iterable<B>>>> parMapIterable() {
    return curry(new F2<F<A, B>, Iterable<A>, Promise<Iterable<B>>>() {
      public Promise<Iterable<B>> f(final F<A, B> abf, final Iterable<A> iterable) {
        return parMap(iterable, abf);
      }
    });
  }

  /**
   * Maps across an Array in parallel.
   *
   * @param as An array to map across in parallel.
   * @param f  A function to map across the given Array.
   * @return A Promise of a new Array with the given function applied to each element.
   */
  public <A, B> Promise<Array<B>> parMap(final Array<A> as, final F<A, B> f) {
    return parMap(as.toStream(), f).fmap(new F<Stream<B>, Array<B>>() {
      public Array<B> f(final Stream<B> stream) {
        return stream.toArray();
      }
    });
  }

  /**
   * A first-class function that maps another function across an array in parallel.
   *
   * @return A function that maps another function across an array in parallel.
   */
  public <A, B> F<F<A, B>, F<Array<A>, Promise<Array<B>>>> parMapArray() {
    return curry(new F2<F<A, B>, Array<A>, Promise<Array<B>>>() {
      public Promise<Array<B>> f(final F<A, B> abf, final Array<A> array) {
        return parMap(array, abf);
      }
    });
  }

  /**
   * Binds a list-valued function across a list in parallel, concatenating the results into a new list.
   *
   * @param as A list to bind across in parallel.
   * @param f  A function to bind across the given list in parallel.
   * @return A promise of a new List with the given function bound across its elements.
   */
  public <A, B> Promise<List<B>> parFlatMap(final List<A> as, final F<A, List<B>> f) {
    return parMap(as, f).fmap(List.<B>join());
  }

  /**
   * Binds a Stream-valued function across a Stream in parallel, concatenating the results into a new Stream.
   *
   * @param as A Stream to bind across in parallel.
   * @param f  A function to bind across the given Stream in parallel.
   * @return A promise of a new Stream with the given function bound across its elements.
   */
  public <A, B> Promise<Stream<B>> parFlatMap(final Stream<A> as, final F<A, Stream<B>> f) {
    return parMap(as, f).fmap(Stream.<B>join());
  }

  /**
   * Binds an Array-valued function across an Array in parallel, concatenating the results into a new Array.
   *
   * @param as An Array to bind across in parallel.
   * @param f  A function to bind across the given Array in parallel.
   * @return A promise of a new Array with the given function bound across its elements.
   */
  public <A, B> Promise<Array<B>> parFlatMap(final Array<A> as, final F<A, Array<B>> f) {
    return parMap(as, f).fmap(Array.<B>join());
  }

  /**
   * Binds an Iterable-valued function across an Iterable in parallel, concatenating the results into a new Iterable.
   *
   * @param as A Iterable to bind across in parallel.
   * @param f  A function to bind across the given Iterable in parallel.
   * @return A promise of a new Iterable with the given function bound across its elements.
   */
  public <A, B> Promise<Iterable<B>> parFlatMap(final Iterable<A> as, final F<A, Iterable<B>> f) {
    return parMap(as, f).fmap(IterableW.<B, Iterable<B>>join())
        .fmap(Function.<IterableW<B>, Iterable<B>>vary(Function.<Iterable<B>>identity()));
  }

  /**
   * Zips two lists together with a given function, in parallel.
   *
   * @param as A list to zip with another in parallel.
   * @param bs A list to zip with another in parallel.
   * @param f  A function with which to zip two lists in parallel.
   * @return A Promise of a new list with the results of applying the given function across the two lists in lockstep.
   */
  public <A, B, C> Promise<List<C>> parZipWith(final List<A> as, final List<B> bs, final F<A, F<B, C>> f) {
    return sequence(as.<B, Promise<C>>zipWith(bs, promise(uncurryF2(f))));
  }

  /**
   * Zips two streams together with a given function, in parallel.
   *
   * @param as A stream to zip with another in parallel.
   * @param bs A stream to zip with another in parallel.
   * @param f  A function with which to zip two streams in parallel.
   * @return A Promise of a new stream with the results of applying the given function across the two streams, stepwise.
   */
  public <A, B, C> Promise<Stream<C>> parZipWith(final Stream<A> as, final Stream<B> bs, final F<A, F<B, C>> f) {
    return sequence(as.<B, Promise<C>>zipWith(bs, promise(uncurryF2(f))));
  }

  /**
   * Zips two arrays together with a given function, in parallel.
   *
   * @param as A array to zip with another in parallel.
   * @param bs A array to zip with another in parallel.
   * @param f  A function with which to zip two arrays in parallel.
   * @return A Promise of a new array with the results of applying the given function across the two arrays, stepwise.
   */
  public <A, B, C> Promise<Array<C>> parZipWith(final Array<A> as, final Array<B> bs, final F<A, F<B, C>> f) {
    return parZipWith(as.toStream(), bs.toStream(), f).fmap(new F<Stream<C>, Array<C>>() {
      public Array<C> f(final Stream<C> stream) {
        return stream.toArray();
      }
    });
  }

  /**
   * Maps with the given function across the given stream in parallel, while folding with
   * the given monoid.
   *
   * @param as     A stream to map over and reduce.
   * @param map    The function to map over the given stream.
   * @param reduce The monoid with which to sum the results.
   * @return A promise of a result of mapping and folding in parallel.
   */
  public <A, B> Promise<B> parFoldMap(final Stream<A> as, final F<A, B> map, final Monoid<B> reduce) {
    return as.map(promise(map)).foldLeft1(liftM2(reduce.sum()));
  }

  /**
   * Maps with the given function across chunks of the given stream in parallel, while folding with
   * the given monoid. The stream is split into chunks according to the given chunking function,
   * the given map function is mapped over all chunks simultaneously, but over each chunk sequentially.
   * All chunks are summed concurrently and the sums are then summed sequentially.
   *
   * @param as       A stream to chunk, then map over and reduce.
   * @param map      The function to map over the given stream.
   * @param reduce   The monoid with which to sum the results.
   * @param chunking A function describing how the stream should be split into chunks. Should return the first chunk
   *                 and the rest of the stream.
   * @return A promise of a result of mapping and folding in parallel.
   */
  public <A, B> Promise<B> parFoldMap(final Stream<A> as, final F<A, B> map, final Monoid<B> reduce,
                                      final F<Stream<A>, P2<Stream<A>, Stream<A>>> chunking) {
    return parMap(Stream.unfold(new F<Stream<A>, Option<P2<Stream<A>, Stream<A>>>>() {
      public Option<P2<Stream<A>, Stream<A>>> f(final Stream<A> stream) {
        return stream.isEmpty() ? Option.<P2<Stream<A>, Stream<A>>>none() : some(chunking.f(stream));
      }
    }, as), Stream.<A, B>map_().f(map)).bind(new F<Stream<Stream<B>>, Promise<B>>() {
      public Promise<B> f(final Stream<Stream<B>> stream) {
        return parMap(stream, reduce.sumLeftS()).fmap(reduce.sumLeftS());
      }
    });
  }
}
