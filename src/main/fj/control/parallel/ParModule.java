package fj.control.parallel;

import fj.Unit;
import fj.P1;
import fj.F;
import fj.Effect;
import fj.data.List;
import static fj.data.List.iterableList;
import static fj.FW.$;

import java.util.ArrayList;

/**
 * A module of higher-order concurrency features.
 */
public final class ParModule {
  private final Strategy<Unit> strategy;

  private ParModule(final Strategy<Unit> strategy) {
    this.strategy = strategy;
  }

  /**
   * Constructor method for ParModules
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
   * Creates a very fast concurrent actor that does not guarantee ordering of its messages.
   *
   * @param e The effect that the actor should have on its messages.
   * @return A concurrent actor that does not guarantee ordering of its messages.
   */
  public <A> Actor<A> effect(final Effect<A> e) {
    return Actor.actor(strategy, e);
  }

  /**
   * Creates a concurrent actor that is guaranteed to process only one message at a time.
   *
   * @param e The effect that the actor should have on its messages.
   * @return A concurrent actor that is guaranteed to process its messages in some order.
   */
  public <A> Actor<A> actor(final Effect<A> e) {
    return QueueActor.queueActor(strategy, e).asActor();
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
   * Maps across a list in parallel.
   *
   * @param as A list to map across in parallel.
   * @param f  A function to map across the given list.
   * @return A Promise of a new list with the given function applied to each element.
   */
  public <A, B> Promise<List<B>> parMap(final List<A> as, final F<A, B> f) {
    return mapM(as, promise(f));
  }

  public <A, B> Promise<ArrayList<B>> parMap(final ArrayList<A> as, final F<A, B> f) {
    return parMap(iterableList(as), f).fmap(new F<List<B>, ArrayList<B>>() {
      public ArrayList<B> f(final List<B> list) {
        return new ArrayList<B>(list.toCollection());
      }
    });
  }

}
