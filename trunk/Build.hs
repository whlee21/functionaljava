module Build where

import qualified Lastik.Java.Javac as J
import qualified Lastik.Java.Javadoc as Jd
import qualified Lastik.Scala.Scalac as S
import qualified Lastik.Scala.Scaladoc as Sd
import Lastik.Java.Javadoc
import Lastik.Scala.Scaladoc
import Lastik.Runner
import Lastik.Output
import Lastik.Util
import System.Cmd
import System.Directory

src = ["src" // "main", "src" // "package-info"]
deps = ["src" // "deps-test"]
test = ["src" // "test"]

javaco = "build" // "classes" // "javac"
javadoco = "build" // "javadoc"
scalaco = "build" // "classes" // "scalac"
scaladoco = "build" // "scaladoc"
depso = "build" // "classes" // "deps"
testo = "build" // "classes" // "test"
resources = "resources"
cp = "classpath" ~?? [javaco, scalaco, depso, testo, resources]
wt v = Just ("Functional Java " ++ v)
dt v = Just ("Functional Java " ++ v ++ " API Specification")
hd = Just "<div><p><em>Copyright 2008 - 2009 Tony Morris, Runar Bjarnason, Tom Adams, Brad Clow, Ricky Clarkson</em></p>This software is released under an open source BSD licence.</div>"
ds = ".deps"

dependencies = do createDirectoryIfMissing True ds
                  mapM_ (\d -> system ("wget -c --directory " ++ ds ++ ' ' : d)) k
  where
  k = map ("http://projects.tmorris.net/public/standards/artifacts/1.17/" ++) ["javadoc-style/javadoc-style.css", "scaladoc-style/script.js", "scaladoc-style/style.css"] ++ ["http://software.tmorris.net/artifacts//package-list-j2se/1.5.0/package-list"]

clean = removeDirectoryRecursive "build"

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

javadoc'' d v = javadoc {
  Jd.directory = Just d,
  Jd.windowtitle = wt v,
  Jd.doctitle = dt v,
  Jd.header = hd,
  Jd.stylesheetfile = Just (ds // "javadoc-style.css"),
  Jd.linkoffline = [("http://java.sun.com/j2se/1.5.0/docs/api", ds)],
  Jd.linksource = True
}

jdc v = javadoc'' javadoco v

jd v = jdc v ->- src

scaladoc'' d v = scaladoc {
  Sd.directory = Just d,
  Sd.doctitle = dt v,
  Sd.header = hd,
  Sd.windowtitle = wt v,
  Sd.stylesheetfile = Just (ds // "style.css"),
  Sd.linksource = True
}

sdc v = j >=>=> scaladoc'' scaladoco v

sd v = copyFile (ds // "script.js") (scaladoco // "script.js") >> fj >>>> (sdc v ->- src)

-- todo get dependencies, jar, release