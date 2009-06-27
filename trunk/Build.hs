module Build where

import qualified Lastik.Javac as J
import qualified Lastik.Scalac as S
import Lastik.Runner
import Lastik.Output
import Lastik.Util
import System.Cmd

src = ["src/main", "src/package-info"]
deps = ["src/deps-test"]
test = ["src/test"]

javaco = "build/classes/javac"
scalaco = "build/classes/scalac"
depso = "build/classes/deps"
testo = "build/classes/test"
resources = "resources"
cp = "classpath" ~?? [javaco, scalaco, depso, testo, resources]

javac'' d = J.javac {
  J.directory = Just d
}

scalac'' d = S.scalac {
  S.directory = Just d
}

j = javac'' javaco

fj = j +->- src

s = j >=>=> scalac'' scalaco

fjs = fj >>>> (s +->- src)

d = scalac'' depso

dep = d +->- deps

tj = s >=>=> d >=>=> scalac'' testo

ts = dep >>>> fjs >>>> (tj +->- test)

-- todo need a scala function in Lastik
scala k = system ("scala " ++ k)

repl = scala cp

testit = ts >>>> scala (cp ++ " fj.Tests")

-- todo get dependencies, scaladoc, javadoc, run tests, jar, release