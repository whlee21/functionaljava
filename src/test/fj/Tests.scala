package fj

import List.flatten

object Tests {
  def tests = flatten(List (
    // fj.data.CheckArray.tests,
    fj.data.CheckList.tests,
    fj.data.CheckStream.tests,
    fj.data.CheckOption.tests
  ))

  def main(args: Array[String]) = run(tests)

  import org.scalacheck.Prop
  import org.scalacheck.ConsoleReporter._
  import org.scalacheck.Test
  import org.scalacheck.Test.{check, defaultParams}

  def run(tests: List[(String, Prop)]) =
    tests foreach { case (name, p) => {
        val c = check(defaultParams, p, (s, d) => {})
        c.result match {
          case Test.Passed => println("Passed " + name)
          case f @ Test.Failed(_) => error(name + ": " + f)
          case Test.Exhausted => println("Exhausted " + name)
          case f @ Test.GenException(e) => {
            e.printStackTrace
            error(name + ": " + f)
          }
          case f @ Test.PropException(_, e) => {
            e.printStackTrace
            error(name + ": " + f)
          }
        }
      }
    }
}
