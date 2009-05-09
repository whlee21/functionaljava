package fj.data;

import static fj.Bottom.error;
import fj.F;
import fj.data.vector.V2;
import fj.data.vector.V;

import java.math.BigInteger;

/**
 * Represents a natural number (zero, one, two, etc.)
 */
public class Natural extends Number {
  private BigInteger value;
  private static final long serialVersionUID = -588673650944359682L;

  private Natural(final BigInteger i) {
    if (i.compareTo(BigInteger.ZERO) < 0)
      throw error("Natural less than zero");
    value = i;
  }

  /**
   * Returns the natural number equal to the given BigInteger
   *
   * @param i A given BigInteger
   * @return An optional natural number, or none if the given BigInteger is less than zero.
   */
  public static Option<Natural> natural(final BigInteger i) {
    return i.compareTo(BigInteger.ZERO) < 0
           ? Option.<Natural>none()
           : Option.some(new Natural(i));
  }

  /**
   * A function that returns the natural number equal to a given BigInteger
   */
  public static final F<BigInteger, Option<Natural>> fromBigInt =
      new F<BigInteger, Option<Natural>>() {
        public Option<Natural> f(final BigInteger i) {
          return natural(i);
        }
      };

  /**
   * Returns the natural number equal to the given long
   *
   * @param i A given long
   * @return An optional natural number, or none if the given long is less than zero.
   */
  public static Option<Natural> natural(final long i) {
    return natural(BigInteger.valueOf(i));
  }

  /**
   * The natural number zero
   */
  public static final Natural ZERO = natural(0).some();

  /**
   * The natural number one
   */
  public static final Natural ONE = natural(1).some();

  /**
   * Return the successor of this natural number
   *
   * @return the successor of this natural number
   */
  public Natural succ() {
    return add(ONE);
  }

  /**
   * Return the predecessor of this natural number
   *
   * @return the predecessor of this natural number
   */
  public Option<Natural> pred() {
    return subtract(ONE);
  }

  /**
   * Add two natural numbers together.
   *
   * @param n A natural number to add to this one.
   * @return the sum of the two natural numbers.
   */
  public Natural add(final Natural n) {
    return natural(n.value.add(value)).some();
  }

  /**
   * Subtract a natural number from another.
   *
   * @param n A natural number to subtract from this one.
   * @return The difference between the two numbers, if this number is larger than the given one. Otherwise none.
   */
  public Option<Natural> subtract(final Natural n) {
    return natural(n.value.subtract(value));
  }

  /**
   * Multiply a natural number by another.
   *
   * @param n A natural number to multiply by this one.
   * @return The product of the two numbers.
   */
  public Natural multiply(final Natural n) {
    return natural(n.value.multiply(value)).some();
  }

  /**
   * Divide a natural number by another.
   *
   * @param n A natural number to divide this one by.
   * @return The quotient of this number and the highest number, less than or equal to the given number,
   *         that divides this number.
   */
  public Natural divide(final Natural n) {
    return natural(value.divide(n.value)).some();
  }

  /**
   * Take the remainder of a natural number division.
   *
   * @param n A natural number to divide this one by.
   * @return The remainder of division of this number by the given number.
   */
  public Natural mod(final Natural n) {
    return natural(value.mod(n.value)).some();
  }

  /**
   * Divide a natural number by another yielding both the quotient and the remainder.
   *
   * @param n A natural number to divide this one by.
   * @return The quotient and the remainder.
   */
  public V2<Natural> divmod(final Natural n) {
    BigInteger[] x = value.divideAndRemainder(n.value);
    return V.v(natural(x[0]).some(), natural(x[1]).some());
  }

  /**
   * Return the BigInteger value of this natural number.
   *
   * @return the BigInteger value of this natural number.
   */
  public BigInteger bigIntegerValue() {
    return value;
  }

  /**
   * Return the long value of this natural number.
   *
   * @return the long value of this natural number.
   */
  public long longValue() {
    return value.longValue();
  }

  /**
   * Return the float value of this natural number.
   *
   * @return the float value of this natural number.
   */
  public float floatValue() {
    return value.floatValue();
  }

  /**
   * Return the double value of this natural number.
   *
   * @return the double value of this natural number.
   */
  public double doubleValue() {
    return value.doubleValue();
  }

  /**
   * Return the int value of this natural number.
   *
   * @return the int value of this natural number.
   */
  public int intValue() {
    return value.intValue();
  }

  /**
   * A function that returns the BigInteger value of a given Natural.
   */
  public static final F<Natural, BigInteger> bigIntegerValue = new F<Natural, BigInteger>() {
    public BigInteger f(final Natural n) {
      return n.bigIntegerValue();
    }
  };
}
