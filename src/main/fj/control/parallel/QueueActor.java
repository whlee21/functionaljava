package fj.control.parallel;

import fj.Effect;
import fj.F;
import fj.P;
import fj.P1;
import fj.Unit;
import static fj.Function.compose;
import static fj.control.parallel.Actor.actor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An Actor equipped with a queue. Messages are acted on in some (but any) order. This actor is guaranteed to
 * act on only one message at a time, but the order in which they are acted upon is undefined.
 * Author: Runar
 */
public final class QueueActor<A> {
  private final AtomicBoolean suspended = new AtomicBoolean(true);
  private final Queue<A> mbox = new ConcurrentLinkedQueue<A>();

  private final Actor<Unit> act;
  private final Actor<A> selfish;
  private final Actor<Unit> work;

  private QueueActor(final Strategy<Unit> s, final Effect<A> ea) {
    act = actor(s, new Effect<Unit>() {
      public void e(final Unit u) {
        if (!mbox.isEmpty()) {
          ea.e(mbox.remove());
          act.act(u);
        }
        else {
          suspended.set(true);
          work.act(u);
        }
      }
    });
    selfish =
        actor(s, new Effect<A>() {
          public void e(final A a) {
            act(a);
          }
        });
    work = actor(s, new Effect<Unit>() {
      public void e(final Unit u) {
        if (suspended.compareAndSet(true, false))
          act.act(Unit.unit());
      }
    });
  }

  /**
   * Constructs an actor, equipped with a queue, that uses the given strategy and has the given effect.
   *
   * @param s The strategy to use to manage this actor's queue.
   * @param e What this actor does with its messages.
   * @return A new actor, equipped with a queue so that it processes one message at a time.
   */
  public static <A> QueueActor<A> queueActor(final Strategy<Unit> s, final Effect<A> e) {
    return new QueueActor<A>(s, e);
  }

  /**
   * Constructs an actor, equipped with a queue, that uses the given strategy and has the given effect.
   *
   * @param s The strategy to use to manage this actor's queue.
   * @param e What this actor does with its messages.
   * @return A new actor, equipped with a queue so that it processes one message at a time.
   */
  public static <A> QueueActor<A> queueActor(final Strategy<Unit> s, final F<A, P1<Unit>> e) {
    return queueActor(s, Effect.f(compose(P1.<Unit>__1(), e)));
  }

  /**
   * Provides an Actor representation of this QueueActor
   *
   * @return An Actor that represents this QueueActor
   */
  public Actor<A> asActor() {
    return selfish;
  }

  /**
   * Submit a message to this actor's queue.
   *
   * @param a A message to submit to this actor's queue.
   */
  public void act(final A a) {
    if (mbox.offer(a))
      work.act(Unit.unit());
    else
      selfish.act(a);
  }

}
