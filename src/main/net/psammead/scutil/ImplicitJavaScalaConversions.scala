package net.psammead.scutil

/** replacement for scala.collection.jcl.Conversions returning immutable collections */
object ImplicitJavaScalaConversions { 
	//------------------------------------------------------------------------------
	//## java to scala
	
	implicit def wrapList[T](orig:java.util.List[T]) = new {
		// def toList:List[T] = List(scala.collection.jcl.Buffer(orig):_*)
		def toScalaList:List[T] = new scala.collection.jcl.BufferWrapper[T] { def underlying = orig }.toList 
	}
	implicit def wrapSet[T](orig:java.util.Set[T]) = new {
		def toScalaSet:Set[T] = Set() ++ new scala.collection.jcl.SetWrapper[T] { val underlying = orig }
	}
	implicit def wrapMap[S,T](orig:java.util.Map[S,T]) = new {
		def toScalaMap:Map[S,T] = Map() ++ new scala.collection.jcl.MapWrapper[S,T] { def underlying = orig }
	}
	/*
	implicit def wrapCollection[T](orig:java.util.Collection[T]) = new {
		def toCollection:Collection[T] = new scala.collection.jcl.CollectionWrapper[T] { def underlying = orig }
	}
	*/
	implicit def wrapEnumeration[T](orig:java.util.Enumeration[T]) = new {
		def toScalaIterator:Iterator[T] = new Iterator[T] {
			def hasNext:Boolean = orig.hasMoreElements 
			def next:T = orig.nextElement
		}
	}
	implicit def wrapIterator[T](orig:java.util.Iterator[T]) = new {
		def toScalaIterator:Iterator[T] = new Iterator[T] {
			def hasNext:Boolean = orig.hasNext 
			def next:T = orig.next
		}
	}
	implicit def wrapIterable[T](orig:java.lang.Iterable[T]) = new {
		def toScalaIterable:Iterable[T] = new Iterable[T] {
			 def elements = wrapIterator(orig.iterator).toScalaIterator
		}
	}
	
	//------------------------------------------------------------------------------
	//## scala to java
	
	implicit def unwrapList[T](orig:List[T]) = new {
		//def toJava:java.util.List[T] =  java.util.Arrays.asList(orig.toArray: _*)
		def toJavaList:java.util.List[T] =  orig.foldLeft(new java.util.ArrayList[T]()){
				(out, element) => out.add(element); out}
	}
	implicit def unwrapSet[T](orig:Set[T]) = new {
		def toJavaSet:java.util.Set[T] = orig.foldLeft(new java.util.HashSet[T]()){
				(out, element) => out.add(element); out}
	}
	implicit def unwrapMap[S,T](orig:Map[S,T]) = new {
		def toJavaMap:java.util.Map[S,T] = orig.foldLeft(new java.util.HashMap[S,T]()){
				(out, element) => out.put(element._1, element._2); out}
	}
	// TODO collection
	// TODO enumeration
	implicit def unwrapIterator[T](orig:Iterator[T]) = new {
		def toJavaIterator:java.util.Iterator[T] = new java.util.Iterator[T] {
			def hasNext:Boolean = orig.hasNext
			def next:T = orig.next
			def remove = throw new UnsupportedOperationException()
		}
	}
	implicit def unwrapIterable[T](orig:Iterable[T]) = new {
		def toJavaIterable:java.lang.Iterable[T] = new java.lang.Iterable[T] {
			def iterator:java.util.Iterator[T] = unwrapIterator(orig.elements).toJavaIterator 
		}
	}
}
