package net.psammead.scutil

object ImplicitCollectionUtils {
	implicit def extendList[T](orig:List[T]) = new {
		def toSet:Set[T] = Set() ++ orig
	}
			
	implicit def extendIterable[T](orig:Iterable[T]) = new {
		def groupIndexed[K](f: (T) => K):Map[K,Set[T]] =  orig.foldLeft(Map[K,Set[T]]() withDefaultValue Set()){(set,x) => set(f(x)) += x}
	}
	
	/*
	// ugly: mutable result
	private def partition[S,T](key:T=>S)(set:Set[T]) = {
		val out	= new scala.collection.mutable.HashMap[S,scala.collection.mutable.Set[T]] with scala.collection.mutable.MultiMap[S,T]
		set foreach { it => out.add(key(it), it) }
		out
	}
	
	// see http://www.nabble.com/-scala---Partitioning-an-Iterable-td23132765.html
	private def partition[K,V](xs: Iterable[V], f: (V) => K): Map[K,Set[V]] = 
			xs.foldLeft(Map[K,Set[V]]() withDefaultValue Set()){(set,x) => set(f(x)) += x}
	*/
}
