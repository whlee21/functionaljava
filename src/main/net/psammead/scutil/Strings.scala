package net.psammead.scutil

object Strings {
	def cutAtStart(prefix:String)(s:String):Option[String] = 
			if (s.startsWith(prefix))	Some(s.substring(prefix.length)) 
			else						None
			
	def cutAtEnd(suffix:String)(s:String):Option[String] = 
			if (s.endsWith(suffix))		Some(s.substring(0, s.length-suffix.length)) 
			else						None
			
	def indent(prefix:String)(s:String):String =
			s.replaceAll("(?m)^", prefix) 
			
	def xmlEscape(quot:Boolean, apos:Boolean)(s:String) = s map {
		case '<' 			=> "&lt;" 
		case '>' 			=> "&gt;"
		case '&' 			=> "&amp;" 
		case '"' if quot	=> "&quot;"
		case '"' if apos	=> "&apos;"
		case x				=> x 
	} mkString
	
	// def escapeBody(s:String) = xmlEscape(false, false) _
	// def escapeAttribute(s:String) = xmlEscape(true, false) _ 
}
