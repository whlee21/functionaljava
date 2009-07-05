{-

Depends
* http://hackage.haskell.org/package/Lastik
* http://hackage.haskell.org/package/pureMD5
* http://hackage.haskell.org/package/SHA

-}
module Build where

import qualified Lastik.Java.Javac as J
import qualified Lastik.Java.Javadoc as Jd
import qualified Lastik.Scala.Scalac as S
import qualified Lastik.Scala.Scaladoc as Sd
import Lastik.Runner
import Lastik.Output
import Lastik.Util
import Lastik.Directory
import Control.Monad
import System.Cmd
import System.Directory
import System.FilePath
import System.FilePath.Find
import Codec.Archive.Zip
import qualified Data.ByteString.Lazy as B
import Data.Digest.Pure.MD5
import Data.Digest.Pure.SHA

src = ["src" </> "main", "src" </> "package-info"]
deps = ["src" </> "deps-test"]
test = ["src" </> "test"]

build = "build"
javaco = build </> "classes" </> "javac"
javadoco = build </> "javadoc"
scalaco = build </> "classes" </> "scalac"
scaladoco = build </> "scaladoc"
depso = build </> "classes" </> "deps"
testo = build </> "classes" </> "test"
jardir = build </> "jar"
releasedir = build </> "release"
etcdir = "etc"

resources = "resources"
cp = "classpath" ~?? [javaco, scalaco, depso, testo, resources]
wt v = Just ("Functional Java " ++ v)
dt v = Just ("Functional Java " ++ v ++ " API Specification")
hd = Just "<div><p><em>Copyright 2008 - 2009 Tony Morris, Runar Bjarnason, Tom Adams, Brad Clow, Ricky Clarkson</em></p>This software is released under an open source BSD licence.</div>"
ds = ".deps"

resolve = do mkdir ds
             mapM_ (\d -> system ("wget -c --directory " ++ ds ++ ' ' : d)) k
  where
  k = map ("http://projects.tmorris.net/public/standards/artifacts/1.30/" ++) ["javadoc-style/javadoc-style.css", "scaladoc-style/script.js", "scaladoc-style/style.css"] ++ ["http://software.tmorris.net/artifacts//package-list-j2se/1.5.0/package-list"]

clean = rmdir build

fullClean = rmdir ds >> clean

javac'' d = J.javac {
  J.directory = Just d
}

scalac'' d = S.scalac {
  S.directory = Just d
}

j = javac'' javaco

javac = j +->- src

s = j >=>=> scalac'' scalaco

scalac = javac >>>> (s +->- src)

d = scalac'' depso

dep = d +->- deps

tj = s >=>=> d >=>=> scalac'' testo

compile = dep >>>> scalac >>>> (tj +->- test)

-- todo scala function in Lastik
scala k = system ("scala " ++ k)

repl = scala cp

tests = compile >>>> scala (cp ++ " fj.Tests")

javadoc'' d v = Jd.javadoc {
  Jd.directory = Just d,
  Jd.windowtitle = wt v,
  Jd.doctitle = dt v,
  Jd.header = hd,
  Jd.stylesheetfile = Just (ds </> "javadoc-style.css"),
  Jd.linkoffline = [("http://java.sun.com/j2se/1.5.0/docs/api", ds)],
  Jd.linksource = True
}

javadoc v = javadoc'' javadoco v ->- src

scaladoc'' d v = Sd.scaladoc {
  Sd.directory = Just d,
  Sd.doctitle = dt v,
  Sd.header = hd,
  Sd.windowtitle = wt v,
  Sd.stylesheetfile = Just (ds </> "style.css"),
  Sd.linksource = True
}

scaladoc v = mkdir scaladoco >> copyFile (ds </> "script.js") (scaladoco </> "script.js") >> javac >>>> (j >=>=> scaladoc'' scaladoco v ->- src)

-- todo jar function for Lastik
jar k = system ("jar " ++ k)

nosvn = fileName /~? ".svn"

nosvnf = nosvn &&? fileType ==? RegularFile

archive = compile >>>>> do mkdir jardir
                           writeArchive ([javaco, scalaco, resources] `zip` repeat ".")
                                        nosvn
                                        nosvnf
                                        [OptVerbose]
                                        (jardir </> "functionaljava.jar")

release v = let k = build </> "functionaljava"
            in do fullClean >> resolve >> archive >> javadoc v >> scaladoc v
                  mkdir k
                  forM_ ([(javadoco, 1), (scaladoco, 1), (jardir, 2), (etcdir, 1)] ++ map (\k -> (k, 0)) (src ++ test)) (\(d, l) -> copyDir nosvn nosvnf l d k)
                  mkdir releasedir
                  a <- archiveDirectories [(build, "functionaljava")] always always [OptVerbose]
                  let s = fromArchive a
                  B.writeFile (releasedir </> "functionaljava.zip") s
                  writeFile (releasedir </> "functionaljava.zip.MD5") (show (md5 s))
                  writeFile (releasedir </> "functionaljava.zip.SHA") (show (sha1 s))