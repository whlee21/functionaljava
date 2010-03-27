package net.psammead.scutil

object Unique {
	/** get a distinguished name for a value by adding a unique suffix if there are other equally named values */
	def name[T](values:List[T], name:T=>String)(value:T) = {
		val	index	= this.index[T,String](values, name)(value)
		name(value) + index.map("_"+_).getOrElse("")
	}
	
	def index[T,X](values:Seq[T], extract:T=>X)(value:T):Option[Int] = {
		def collides(a:T,b:T):Boolean = extract(a) == extract(b)
		index(values, collides _)(value)
	}
	
	/** given some values and whether two values collide returns the index of a value in the list of values it's colliding with */
	def index[T](values:Seq[T], collides:(T,T)=>Boolean)(value:T):Option[Int] = {
		val	candidates	= values.filter(collides(value, _))
		if (candidates.size > 1)	Some(candidates.indexOf(value)) 
		else 						None
	}
}
