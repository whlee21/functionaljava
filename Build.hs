{-

Depends
* The Haskell Platform http://hackage.haskell.org/platform/
* Lastik http://hackage.haskell.org/package/Lastik

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
import System.Exit
import System.Directory
import System.FilePath
import Lastik.Find
import Codec.Archive.Zip
import qualified Data.ByteString.Lazy as B
import Data.Digest.Pure.MD5
import Data.Digest.Pure.SHA

src = ["src" </> "main", "src" </> "package-info"]
deps = ["src" </> "deps-test"]
test = ["src" </> "test"]

build = "build"
javaco = build </> "classes" </> "javac"
scalaco = build </> "classes" </> "scalac"
depso = build </> "classes" </> "deps"
testo = build </> "classes" </> "test"
javadoco = build </> "javadoc"
scaladoco = build </> "scaladoc"
jardir = build </> "jar"
releasedir = build </> "release"
mavendir = build </> "maven"
etcdir = "etc"

resources = "resources"
cp = "classpath" ~?? [javaco, scalaco, depso, testo, resources]
wt v = Just ("Functional Java " ++ v)
dt v = Just ("Functional Java " ++ v ++ " API Specification")
hd = Just "<div><p><em>Copyright 2008 - 2009 Tony Morris, Runar Bjarnason, Tom Adams, Brad Clow, Ricky Clarkson</em></p>This software is released under an open source BSD licence.</div>"
ds = ".deps"

resolve = do e <- doesDirectoryExist ds
             unless e $ do mkdir ds
                           mapM_ (\d -> system ("wget -c --directory " ++ ds ++ ' ' : d)) k
  where
  k = map ("http://projects.tmorris.net/public/standards/artifacts/1.30/" ++) ["javadoc-style/javadoc-style.css", "scaladoc-style/script.js", "scaladoc-style/style.css"] ++ ["http://soft.tmorris.net/artifacts/package-list-j2se/1.5.0/package-list"]

type Version = String

readVersion :: IO Version
readVersion = readFile "version"

clean :: IO ()
clean = rmdir build

fullClean :: IO ()
fullClean = rmdir ds >> clean

javac'' :: FilePath -> J.Javac
javac'' d = J.javac {
  J.directory = Just d
}

scalac'' :: FilePath -> S.Scalac
scalac'' d = S.scalac {
  S.directory = Just d
}

j :: J.Javac
j = javac'' javaco

javac :: IO ExitCode
javac = j +->- src

s :: S.Scalac
s = j >=>=> scalac'' scalaco

scalac :: IO ExitCode
scalac = javac >>>> (s +->- src)

d :: S.Scalac
d = scalac'' depso

dep :: IO ExitCode
dep = d +->- deps

tj :: S.Scalac
tj = s >=>=> d >=>=> scalac'' testo

compile :: IO ExitCode
compile = dep >>>> scalac >>>> (tj +->- test)

-- todo scala function in Lastik
scala :: String -> IO ExitCode
scala k = system ("scala " ++ k)

repl :: IO ExitCode
repl = scala cp

tests :: IO ExitCode
tests = compile >>>> scala (cp ++ " fj.Tests")

javadoc'' :: Version -> Jd.Javadoc
javadoc'' v = Jd.javadoc {
  Jd.directory = Just javadoco,
  Jd.windowtitle = wt v,
  Jd.doctitle = dt v,
  Jd.header = hd,
  Jd.stylesheetfile = Just (ds </> "javadoc-style.css"),
  Jd.linkoffline = [("http://java.sun.com/j2se/1.5.0/docs/api", ds)],
  Jd.linksource = True
}

javadoc :: Version -> IO ExitCode
javadoc v = resolve >> javadoc'' v ->- src

scaladoc'' :: Version -> Sd.Scaladoc
scaladoc'' v = Sd.scaladoc {
  Sd.directory = Just scaladoco,
  Sd.doctitle = dt v,
  Sd.header = hd,
  Sd.windowtitle = wt v,
  Sd.stylesheetfile = Just (ds </> "style.css"),
  Sd.linksource = True
}

scaladoc :: Version -> IO ExitCode
scaladoc v = resolve >> mkdir scaladoco >> copyFile (ds </> "script.js") (scaladoco </> "script.js") >> javac >>>> (j >=>=> scaladoc'' v ->- src)

nosvn :: FilePather Bool
nosvn = fileName /=? ".svn"

nosvnf :: FilterPredicate
nosvnf = constant nosvn ?&&? isFile

archive :: IO ()
archive = compile >>>>> do mkdir jardir
                           writeArchive ([javaco, scalaco, resources] `zip` repeat ".")
                                        nosvn
                                        nosvnf
                                        [OptVerbose]
                                        (jardir </> "functionaljava.jar")

buildAll :: IO ExitCode
buildAll = do v <- readVersion
              resolve >> archive >> javadoc v >> scaladoc v

maven :: IO ()
maven = do buildAll
           mkdir mavendir
           v <- readVersion
           forM_ [("javadoc", [javadoco]), ("scaladoc", [scaladoco]), ("sources", src), ("tests", test)] (\(n, f) ->
             writeHashArchive (map (flip (,) ".") f) nosvn nosvnf [OptVerbose] (mavendir </> "fj-" ++ v ++ '-' :  n ++ ".jar"))

release :: IO ()
release = let k = build </> "functionaljava"
          in do buildAll
                mkdir k
                forM_ ([(1, javadoco), (1, scaladoco), (2, jardir), (1, etcdir)] ++ map ((,) 0) (src ++ test)) (\(l, d) -> copyDir nosvn nosvnf l d k)
                mkdir releasedir
                writeHashArchive [(build, "functionaljava")] always always' [OptVerbose] (releasedir </> "functionaljava.zip")
