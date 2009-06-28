module Build where

import qualified Lastik.Java.Javac as J
import qualified Lastik.Scala.Scalac as S
import qualified Lastik.Scala.Scaladoc as Sd
import Lastik.Scala.Scaladoc
import Lastik.Runner
import Lastik.Output
import Lastik.Util
import System.Cmd

src = ["src/main", "src/package-info"]
deps = ["src/deps-test"]
test = ["src/test"]

javaco = "build/classes/javac"
scalaco = "build/classes/scalac"
scaladoco = "build/scaladoc"
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

-- todo stylesheetfile
scaladoc'' d v = Sd.scaladoc {
  Sd.directory = Just d,
  doctitle = Just ("Functional Java " ++ v ++ " API Specification"),
  header = Just "<div><p><em>Copyright 2008 - 2009 Tony Morris, Runar Bjarnason, Tom Adams, Brad Clow, Ricky Clarkson</em></p>This software is released under an open source BSD licence.</div>",
  windowtitle = Just ("Functional Java " ++ v)
}

sdc v = j >=>=> scaladoc'' scaladoco v

sd v = fj >>>> (sdc v ->- src)

-- todo get dependencies, javadoc, jar, release