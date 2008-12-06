package fj.data

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import List.{nil, cons}

object ArbitraryList {
  implicit def arbitraryList[A](implicit a: Arbitrary[A]): Arbitrary[List[A]] =
    Arbitrary(arbitrary[scala.List[A]].map(_.foldRight(nil[A])(cons(_, _))))
}
