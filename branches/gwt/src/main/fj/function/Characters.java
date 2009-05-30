package fj.function;

import fj.F;
import fj.F2;
import fj.data.Stream;
import static fj.Function.curry;

/**
 * First-class functions on Characters.
 */
public final class Characters {
  public static final F<Character, String> toString = new F<Character, String>() {
    public String f(final Character c) {return Character.toString(c);}
  };
  public static final F<Character, Boolean> isLowerCase = new F<Character, Boolean>() {
    public Boolean f(final Character ch) {return Character.isLowerCase(ch);}
  };
  public static final F<Character, Boolean> isUpperCase = new F<Character, Boolean>() {
    public Boolean f(final Character ch) {return Character.isUpperCase(ch);}
  };
  public static final F<Character, Boolean> isDigit = new F<Character, Boolean>() {
    public Boolean f(final Character ch) {return Character.isDigit(ch);}
  };
  public static final F<Character, Boolean> isLetter = new F<Character, Boolean>() {
    public Boolean f(final Character ch) {return Character.isLetter(ch);}
  };
  public static final F<Character, Boolean> isLetterOrDigit = new F<Character, Boolean>() {
    public Boolean f(final Character ch) {return Character.isLetterOrDigit(ch);}
  };
  public static final F<Character, Character> toLowerCase = new F<Character, Character>() {
    public Character f(final Character ch) {return Character.toLowerCase(ch);}
  };
  public static final F<Character, Character> toUpperCase = new F<Character, Character>() {
    public Character f(final Character ch) {return Character.toUpperCase(ch);}
  };
  public static final F<Character, F<Integer, Integer>> digit = curry(new F2<Character, Integer, Integer>() {
    public Integer f(final Character ch, final Integer radix) {return Character.digit(ch, radix);}
  });
  public static final F<Character, Boolean> isNewLine = new F<Character, Boolean>() {
    public Boolean f(final Character c) { return c == '\n'; }
  };  
}
