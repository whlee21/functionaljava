package net.psammead.scgen.processor

import java.lang.{ 
	Iterable => JIterable 
}
import java.util.{ 
	Collection	=> JCollection,
	Map			=> JMap, 
	List		=> JList,	ArrayList	=> JArrayList,
	Set			=> JSet,	HashSet		=> JHashSet, 
	Collections	=> JCollections 
}
import java.io._

import javax.lang.model._
import javax.lang.model.element._
import javax.lang.model.`type`._
import javax.lang.model.util._
import javax.annotation.processing._ 
import javax.tools._

import net.psammead.scutil.Unique
import net.psammead.scutil.Strings._
import net.psammead.scutil.ImplicitCollectionUtils._
import net.psammead.scutil.ImplicitJavaScalaConversions._

import net.psammead.scgen.annotation._

class FunctionProcessor extends Processor {
	//## constants
	
	// processor option names
	val FUNCTIONAL_LIBRARY	= "functional.library"
	val FUNCTIONAL_PREFIX	= "functional.prefix"
	
	// default values for processor options
	val	FUNCTIONAL_PREFIX_DEFAULT	= "functional"
	
	//## processor stuff
	
	// environment dields
	var processingEnv:ProcessingEnvironment = null
	var messager:Messager 	= null
	var filer:Filer			= null
	var types:Types			= null
	var elements:Elements	= null
	
	def init(processingEnv:ProcessingEnvironment) {
		this.processingEnv	= processingEnv
		
		messager	= processingEnv.getMessager
		filer		= processingEnv.getFiler
		types		= processingEnv.getTypeUtils
		elements	= processingEnv.getElementUtils
	}
	
	private def JSet[T](array:T*):JSet[T] = { val out = new JHashSet[T]; array.foreach(out.add(_)); out }
	def getSupportedOptions:JSet[String]			= JSet(FUNCTIONAL_LIBRARY)
	def getSupportedAnnotationTypes:JSet[String]	= JSet(classOf[Functional].getName, classOf[FunctionalClass].getName)
	def getSupportedSourceVersion:SourceVersion		= SourceVersion.RELEASE_6
	
	def getCompletions(element:Element, annotation:AnnotationMirror, member:ExecutableElement, userText:String):JIterable[_ <: Completion] = JCollections.emptyList[Completion]

	//## code generator

	// TODO pass around as a parameter
	var javaCode:JavaCode = _
	
	def process(annotations:JSet[_ <: TypeElement], roundEnv:RoundEnvironment):Boolean	= {
		if (roundEnv.processingOver)	return true
		if (roundEnv.errorRaised)		return true
		
		msg.note("### FunctionProcessor ###")
		
		javaCode	= configureJavaCode
		if (roundEnv.errorRaised) { msg.note("aborted"); return true }
	
		// TODO warn about @Functional members within @Functional types
		// TODO warn about unreachable and deprecated types if not accessed with a package wildcard
		// TODO check members for reachability and deprecatedness instead of simply filtering out
		
		//## find classes for @Functional
		
		val	eFromFunctionalAll		= roundEnv.getElementsAnnotatedWith(classOf[Functional]).toScalaSet
		// additional toList and toSet hackery to get around bug http://lampsvn.epfl.ch/trac/scala/ticket/1538
		
		// VariableElement | ExecutableElement
		val eFromFunctionalMembers	= eFromFunctionalAll.toList.toSet
										.filter(isMemberKind)

		// TypeElement
		val eFromFunctionalTypes	= eFromFunctionalAll.toList.toSet
										.filter(isTypeKind)	// ElementFilter.typesIn
										.flatMap[Element]{it => it.asInstanceOf[TypeElement].getEnclosedElements.toScalaList.toSet }	

		//## find classes for @FunctionalClass
				
		val sFromFunctionalClass	= roundEnv.getElementsAnnotatedWith(classOf[FunctionalClass]).toScalaSet
										//.flatMap{_.getAnnotation(classOf[FunctionalClass]).value}
										.map{_.getAnnotation(classOf[FunctionalClass]).value}
										// known eclipse bug: @FunctionalClass("foo.Bar") returns a null value
										.map{x => if (x != null) x else { msg.error("@FunctionalClass values need to be surrounded with {}");  new Array[String](0) }}
										.flatMap(x=>x)	// == flatten
										
		val wildcardPackage	= cutAtEnd(".*") _

		val eFromFunctionalClassWild	= sFromFunctionalClass
											// why can a Set[Option] not be flatMapped?
											.flatMap[String](it => wildcardPackage(it) match {
												case None 		=> Set()
												case Some(x)	=> Set(x)
											})
											.flatMap{ pack =>
												val ePackage	= elements.getPackageElement(pack)
												if (ePackage == null) { 
													msg.error("cannot find package for FunctionalClass annotation value: " + pack)
													List()
												}
												else {
													val	eTypes	= ElementFilter.typesIn(ePackage.getEnclosedElements).toScalaList
													if (eTypes.isEmpty)	msg.warning("not a single type found for FunctionalClass annotation value: " + pack)
													eTypes
												}
											}
											.flatMap(deepTypes)
											.filter(isPublicReachable)
											.filter(isNotDeprecated)
											.flatMap[Element]{ it => it.getEnclosedElements.toScalaList.toSet }
										
		val eFromFunctionalClassSingle	= sFromFunctionalClass
											.filter(it => !wildcardPackage(it).isDefined)
											.flatMap{ name =>
												val eType = elements.getTypeElement(name)
												if (eType == null) { 
													msg.error("type not found for FunctionalClass annotation value: " + name)
													List()
												}
												else {
													List(eType)
												}
											}
											.flatMap(deepTypes)
											// not necessary for original types, but for deepTypes...
											.filter(isPublicReachable)
											.filter(isNotDeprecated)
											.flatMap[Element]{ it => it.getEnclosedElements.toScalaList.toSet }
									
		if (roundEnv.errorRaised) { msg.note("aborted"); return true }
		
		//## find members and their types

		val eAllMembers:Set[Element]	= (eFromFunctionalMembers ++ eFromFunctionalTypes ++ eFromFunctionalClassWild ++ eFromFunctionalClassSingle)
											.filter(isRelevantMember)
		val	eElementsIndexByType		= eAllMembers.groupIndexed(enclosingType _)
		
		val eAllTypes					= eElementsIndexByType.keySet
		def lt(eType1:TypeElement, eType2:TypeElement):Boolean = (eType1.getQualifiedName.toString compareTo eType2.getQualifiedName.toString) < 0
		val eTypesOrderByName			= eAllTypes.toList.sort(lt)
		
		val eUnreachable		= eAllTypes.filter(it => !isPublicReachable(it))
		eUnreachable.foreach { it => msg.error("@Functional used on non-public type: " + it) }
		
		val eDeprecated			= eAllTypes.filter(it => isDeprecated(it))
		eDeprecated.foreach { it => msg.warning("@Functional used on deprecated type: " + it) }
		
		if (roundEnv.errorRaised) { msg.note("aborted"); return true }
		
		if (eTypesOrderByName.isEmpty) { msg.note("done"); return true }
		else msg.note("processing " + eAllMembers.size + " members in " +  eAllTypes.size + " types");
		
		//## generate code
		
		eTypesOrderByName.foreach { eType:TypeElement =>
			val	fqn		= javaCode.mirrorFQN(eType)
			val filter	= eElementsIndexByType(eType)
			//msg.note("processing type: " + eType + "\t=> " + fqn)
			val	sType	= processTypeElement(eType, filter)
			try {
				val sourceFile:JavaFileObject	= filer.createSourceFile(fqn, eType)
				val sourceWriter:Writer			= sourceFile.openWriter();
				sourceWriter.write(sType)
				sourceWriter.close()
			}
			catch {
				case e:IOException	=> msg.error("cannot generate class: " + fqn + "\n\t" + e)
			}
		}
		true
	}
	
	/** read configuration properties and setup javaCode */
	def configureJavaCode:JavaCode = {
		// configure package prefix
		val	functionalPrefix = processingEnv.getOptions.get(FUNCTIONAL_PREFIX) match {
			case null =>
				msg.note("using default " + FUNCTIONAL_PREFIX + " value: " + FUNCTIONAL_PREFIX_DEFAULT)
				FUNCTIONAL_PREFIX_DEFAULT
			case functionalPrefixOption =>
				msg.note("using configured " + FUNCTIONAL_PREFIX + " value: " + functionalPrefixOption)
				functionalPrefixOption
		}
	
		// configure target functional library
		processingEnv.getOptions.get(FUNCTIONAL_LIBRARY) match {
			case null =>
				val javaCode	=  new FjJavaCode(functionalPrefix)
				msg.note("using default " + FUNCTIONAL_LIBRARY + " value " + javaCode.functionalLibrary)
				return javaCode
			case functionalLibraryOption =>
				val	javaCodeOptions	= Set(new OwnJavaCode(functionalPrefix), new FjJavaCode(functionalPrefix))
				javaCodeOptions.find(_.functionalLibrary == functionalLibraryOption) match {
					case Some(javaCode) =>
						msg.note("using configured " + FUNCTIONAL_LIBRARY + " value: " + javaCode.functionalLibrary)
						return javaCode
						
					case None => 
						msg.error("invalid " + FUNCTIONAL_LIBRARY + " value: " + functionalLibraryOption + ". " +
								"possible values are: " + javaCodeOptions.map(_.functionalLibrary).mkString(", "))
						return null
				}
		}
	}
	
	//------------------------------------------------------------------------------
	//## naming
	
	// TODO butt ugly
	/** add types instead of a simple index. indizes are not declaration order in eclipse as they are in javac */
	class ProcessMemberNaming(allProcessMembers:List[ProcessMember]) {
		val	simpleUniqueFunc	=  Unique.index[ProcessMember,String](allProcessMembers, _.simpleName) _
		val	typedUniqueFunc		=  Unique.index[ProcessMember,String](allProcessMembers, _.typedName) _
		def unclashObjectMethods(name:String):String	= (if (objectMethodNames.contains(name)) "_" else "") + name
		def usableName(p:ProcessMember) = {
			val	simpleUnique	= simpleUniqueFunc(p)
			val typedUnique		= typedUniqueFunc(p)
			def bestName	= ( 
					 if (!simpleUnique.isDefined)	p.simpleName
				else if (!typedUnique.isDefined)	p.typedName
				else								p.typedName + "_" + typedUnique.get
			)
			unclashObjectMethods(bestName)
		}
	}
	
	/*
	def memberNameExtract(eAny:Element):String = eAny.getKind match {
		case ElementKind.FIELD			=> eAny.getSimpleName.toString
		case ElementKind.METHOD			=> eAny.getSimpleName.toString
		case ElementKind.ENUM_CONSTANT	=> eAny.getSimpleName.toString	// TODO check enums
		case ElementKind.CONSTRUCTOR	=> "_new"
		case _							=> throw new RuntimeException("cannot extract name from: " + eAny + " " + eAny.getKind)
	}
	def memberNameUnique:(Element=>String) = Unique.name(allMembers, memberNameExtract _)
	*/
	
	//------------------------------------------------------------------------------
	//## code generator
	
	/** generate code for given members of a type */
	def processTypeElement(eType:TypeElement, eFilter:Set[Element]):String = {
		msg.note("processing type: " + eType)
		// DeclaredType
		
		// enclosed elements in the order of the source
		val eEnclosed		= eType.getEnclosedElements.toScalaList.filter{it=>eFilter.contains(it)}.toJavaList
		
		var eConstructors	= ElementFilter.constructorsIn(eEnclosed).toScalaList
								// no constructors for abstract classes.
								.filter{ x:ExecutableElement => !isAbstract(x.getEnclosingElement) }
		val eMethods		= ElementFilter.methodsIn(eEnclosed).toScalaList
		val eFields			= ElementFilter.fieldsIn(eEnclosed).toScalaList
		
		val allProcessMembers:List[ProcessMember]	= eFields.map(processField) ::: eConstructors.map(processConstructor) ::: eMethods.map(processMethod)
		val	naming									= new ProcessMemberNaming(allProcessMembers)
		def stringifyMember(p:ProcessMember):String	= p.code(naming.usableName(p))
		val allStringMembers						= allProcessMembers.map(stringifyMember)
		
		javaCode.clazz(
			eType.getQualifiedName.toString,
			javaCode.mirrorPackage(eType),
			javaCode.mirrorName(eType),
			allStringMembers
		)
	}
	
	/** dispatches field processing */
	def processField(eField:VariableElement):ProcessField = {
		if (isStatic(eField))	new ProcessStaticField(eField)
		else					new ProcessInstanceField(eField)
	}
	
	
	/** dispatches constructor processing */
	def processConstructor(eConstructor:ExecutableElement):ProcessConstructor = {
		if (isInnerClass(enclosingType(eConstructor)))	new ProcessInnerConstructor(eConstructor)
		else											new ProcessStaticConstructor(eConstructor)
	}
	
	/** dispatched method processing */
	def processMethod(eMethod:ExecutableElement):ProcessMethod = {
		//msg.note("processing method: " + eMethod)
		if (isStatic(eMethod))	new ProcessStaticMethod(eMethod)
		else					new ProcessInstanceMethod(eMethod)
	}
	
	/** generates code for an Element */
	abstract class ProcessMember {
		def eElement:Element
		
		final val eEnclosing				= enclosingType(eElement)
		final def eEnclosingTypeParameters	= eEnclosing.getTypeParameters.toScalaList
		final val tEnclosingType			= eEnclosing.asType
			
		def eTypeParameters:List[TypeParameterElement]
		final def tTypeParameters	= eTypeParameters.map(typeOfTypeParameter)
		final def typeStr			= new TypeString(tTypeParameters)

		// TODO not used in constructors
		final val sNameOrig			= eElement.getSimpleName.toString
		final def sTypeParameters	= typeStr.processTypeParameters(eTypeParameters)
		
		def sCode:String
		def sReturnType:String
		def xParameters:List[Parameter]
		final def xResult			= Result(sReturnType, sCode)
		
		// TODO these are the only ones being public
		def simpleName:String
		def typedName:String
		final def code(sophisticatedName:String):String = javaCode.curriedFunction(sophisticatedName, sTypeParameters, xParameters, xResult)
	}
	
	/** generates code for a VariableElemement of kind field */
	abstract class ProcessField extends ProcessMember {
		val eVariable:VariableElement
		final def eElement:Element	= eVariable
		final val tSelfType			= eVariable.asType;
		
		final def sReturnType		= typeStr.processTypeBoxed(tSelfType)
		
		final def simpleName		= eVariable.getSimpleName.toString
		final def typedName			= simpleName
	}
	
	/** generates code for a VariableElemement of kind field representing a static field */
	final class ProcessStaticField(val eVariable:VariableElement) extends ProcessField {
		// TODO treat this like a no-args static method: generate a Donor
		final def eTypeParameters	= List()
		
		final def sCode				= "return " + eEnclosing.getQualifiedName.toString + "." + sNameOrig + ";"
		
		final def xParameters		= List()
	}
	
	/** generates code for a VariableElemement of kind field representing an instance field */
	final class ProcessInstanceField(val eVariable:VariableElement) extends ProcessField {
		final def eTypeParameters		= eEnclosingTypeParameters
			
		final val sSelfType				= typeStr.processType(tEnclosingType)
		final val sSelfName				= "_this"
		final def sCode					= "return  " + sSelfName + "." + sNameOrig + ";"
		
		final def xParameters			= List(Parameter(sSelfType, sSelfType, sSelfName))
	}
		
	/** generates code for an ExecutableElement */
	abstract class ProcessExecutable extends ProcessMember {
		val eExecutable:ExecutableElement
		final def eElement:Element = eExecutable
		
		final val eSelfParameters		= eExecutable.getParameters.toScalaList
		final def eSelfTypeParameters	= eExecutable.getTypeParameters.toScalaList
		final val tSelfThrownTypes		= eExecutable.getThrownTypes.toScalaList
		
		def eTypeParameters:List[TypeParameterElement]
		
		final def sArguments		= typeStr.processArguments(eSelfParameters)
		final def sThrownTypes		= typeStr.processTypes(removeSubclasses(tSelfThrownTypes))
		
		final def sCode				= javaCode.exceptionWrapper(sThrownTypes, sBody)
		def sBody:String
		
		final def xSelfParameters	= eSelfParameters.map(mkParameter(typeStr))
		def xExtraParameter:Option[Parameter]
		final def xParameters	= xExtraParameter match {
			case Some(param)	=> param :: xSelfParameters
			case None			=> xSelfParameters
		}
		
		final def mkParameter(typeStr:TypeString)(eVariable:VariableElement):Parameter = Parameter(
			typeStr.processType(eVariable.asType), 
			typeStr.processTypeBoxed(eVariable.asType), 
			eVariable.getSimpleName.toString
		)
		
		// TODO cleanup
		final def typedName			= simpleName + (
			if (eSelfParameters.isEmpty)	"" 
			else 							"_" + eSelfParameters.map(it => typeStr.processShortType(it.asType)).mkString("_")
		)
	}
	
	/** generates code for an ExecutableElement of type constructor */
	abstract class ProcessConstructor extends ProcessExecutable {
		final def sReturnType		= typeStr.processTypeBoxed(tEnclosingType)
		
		final def simpleName		= "_new"
	}
	
	/** generate a method for the construction of a static class instance */
	final class ProcessStaticConstructor(val eExecutable:ExecutableElement) extends ProcessConstructor {
		final def eTypeParameters	= eEnclosingTypeParameters ::: eSelfTypeParameters
		
		final def sBody		= "return new " + sReturnType + sArguments + ";"
		
		final def xExtraParameter	= None
	}
	
	/** generate a method for the construction of an inner class instance */
	final class ProcessInnerConstructor(val eExecutable:ExecutableElement) extends ProcessConstructor {
		final val eOuterTypeParameters	= outerClasses(eEnclosing).map(_.getTypeParameters.toScalaList).flatten[TypeParameterElement]
		final val eOuter				= enclosingType(eEnclosing)
		final def sOuterType			= typeStr.processType(eOuter.asType)
		final val sOuterName			= "_this"
		
		final def eTypeParameters		= eOuterTypeParameters ::: eSelfTypeParameters
		
		final def sNewType				= cutAtStart(sOuterType + ".")(sReturnType).get
		final def sBody					= "return " + sOuterName + ".new " + sNewType + sArguments + ";"
		
		final def xExtraParameter		= Some(Parameter(sOuterType, sOuterType, sOuterName))
	}
	
	/** generates code for an ExecutableElement of type method */
	abstract class ProcessMethod extends ProcessExecutable {
		final val eReturnType		= eExecutable.getReturnType
		final val eTypeArguments	= eSelfTypeParameters
		
		final def sTypeArguments	= typeStr.processTypeArguments(eTypeArguments)
		final def sReturnType		= typeStr.processTypeBoxed(eReturnType)
		final def sBody				= javaCode.returnStatement(isVoidType(eReturnType), sStatement)
		def sStatement:String
		
		final def simpleName		= eExecutable.getSimpleName.toString
	}
	
	/** generate a method for a static method */
	final class ProcessStaticMethod(val eExecutable:ExecutableElement) extends ProcessMethod {
		final def eTypeParameters	= eSelfTypeParameters
		
		def	sStatement				= eEnclosing.getQualifiedName.toString + "." + sTypeArguments + sNameOrig + sArguments
		
		def xExtraParameter			= None
	}
	
	/** generate a method for an instance method */
	final class ProcessInstanceMethod(val eExecutable:ExecutableElement) extends ProcessMethod {
		final def eTypeParameters	= eEnclosingTypeParameters ::: eSelfTypeParameters
			
		val sSelfType				= typeStr.processType(tEnclosingType)
		val sSelfName				= "_this"
		def	sStatement				= sSelfName + "." + sTypeArguments + sNameOrig + sArguments
		
		def xExtraParameter			= Some(Parameter(sSelfType, sSelfType, sSelfName))
	}
	
	//------------------------------------------------------------------------------
	//## code templates
	
	// TODO can this be replaced by VariableElement?
	case class Parameter(val sType:String, val sTypeBoxed:String, val sName:String)
	case class Result(val sTypeBoxed:String, sBody:String) 
	
	/*
	fj.F		new fj.F<S,T>() { public T apply(S s); }
	fj.F2		new fj.F2<S1,S2,T>() { public T apply(S1 v1, S2 v2) { ... } }
	fj.F3
	fj.Unit		fj.Unit.unit()
	fj.P1		fj.P.p(v1)
	fj.P2		fj.P.p(v1,v2)
	fj.P3		fj.P.p(v1,v2,v3)
	*/
	
	abstract class JavaCode(functionalPrefix:String) {
		def functionalLibrary:String
		
		def mirrorFQN(eType:TypeElement):String		= mirrorPackage(eType) + "." + mirrorName(eType)
		
		def mirrorPackage(eType:TypeElement):String	= {
			val	pack	= elements.getPackageOf(eType)
			functionalPrefix + (if (pack.isUnnamed) "" else "." + pack.getQualifiedName.toString)
		}
		
		def mirrorName(eType:TypeElement):String = 
				(eType.getNestingKind match {
					case NestingKind.MEMBER	=> mirrorName(enclosingType(eType))
					case _					=> ""
				}) + 
				"_" + eType.getSimpleName.toString
		
		def clazz(sOriginal:String, sPackage:String, sSimpleName:String, sMethods:List[String]):String
		
		def curriedFunction(sNameGen:String, sTypeParameters:String, xParameters:List[Parameter], xResult:Result):String = xParameters match {
			case Nil	=>
					publicStaticMethod(sNameGen, sTypeParameters, 
							boxType(xResult.sTypeBoxed), 
							boxValue(xResult.sTypeBoxed, xResult.sBody))
			case _		=>
					def addParamToResult(param:Parameter, result:Result):Result = Result(
						functionType(param.sTypeBoxed, result.sTypeBoxed), 
						functionValue(param.sTypeBoxed, result.sTypeBoxed, param.sName, result.sBody)
					)
					val xFolded	= xParameters.foldRight(xResult)(addParamToResult)
					publicStaticMethod(sNameGen, sTypeParameters, xFolded.sTypeBoxed, xFolded.sBody)
		}
		
		def publicStaticMethod(sName:String, sTypeParams:String, sOutType:String, sReturn:String):String =
				"public static " + sTypeParams + " " + sOutType + " " + sName + "() {\n" + sReturn + " }"
			
		def boxValue(sOutType:String, sReturn:String):String
		def boxType(sOutType:String):String
		def functionValue(sInType:String, sOutType:String, sInName:String, sReturn:String):String
		def functionType(sInType:String, sOutType:String):String
		
		def exceptionWrapper(sExceptionTypes:List[String], sBody:String):String = sExceptionTypes match {
			case Nil	=> sBody
			case _		=> "try { " + sBody + " } " + sExceptionTypes.map("catch (" + _ + " e) { throw new " + uncheckedException + "(e); }").mkString(" ")
		}
		
		def returnStatement(voidType:Boolean, sValue:String):String =
				if (voidType)	voidReturn(sValue)
				else			valueReturn(sValue)
		
		def valueReturn(sValue:String) = "return " + sValue + ";"
		def voidReturn(sValue:String)	= sValue + "; return " + unitValue + ";"
		
		def uncheckedException:String
		def unitValue:String
		def unitType:String
		val basePackage:String
		
		def typeKlammer(sTypes:List[String]):String = 
				if (sTypes.isEmpty) "" else sTypes.mkString("<", ",", ">")
		
		def valueKlammer(sValues:List[String]):String =
				sValues.mkString("(", ", ", ")")
	}
	
	class OwnJavaCode(functionalPrefix:String) extends JavaCode(functionalPrefix) {
		def functionalLibrary = "own"
		
		def clazz(sOriginal:String, sPackage:String, sSimpleName:String, sMethods:List[String]):String = 
				List(
					"package " + sPackage + ";",
					"",
					"/** mirrors {@link " + sOriginal + "} */",
					"@javax.annotation.Generated(\"net.psammead.scgen.processor.FunctionProcessor\")",
					//"""@SuppressWarnings({"deprecation","cast","unchecked"})""",
					"@java.lang.SuppressWarnings(\"all\")",
					"public final class " + sSimpleName + " {",
					"\t/** utility class */",
					"\tprivate " + sSimpleName + "() {}"
				).mkString("\n") +
				"\n\n" +
				indent("\t")(sMethods.mkString("\n\n")) +
				"\n" +
				List(
					"}"
				).mkString("\n")
		
		def boxValue(sOutType:String, sReturn:String):String =
				"return new " + boxType(sOutType) + "() {\npublic " + sOutType + " get() {\n" + sReturn + " } };"
				
		def boxType(sOutType:String):String =
				basePackage + "Donor<" + sOutType + ">"
				
		def functionValue(sInType:String, sOutType:String, sInName:String, sReturn:String):String =
				"return new " + functionType(sInType, sOutType) + "() {\npublic " + sOutType + " apply(final " + sInType + " " + sInName + ") {\n" + sReturn + " } };"
				
		def functionType(sInType:String, sOutType:String):String = 
				basePackage + "Function<" + sInType + "," + sOutType + ">"
		
		def uncheckedException:String =  basePackage + "Unchecked" 
				
		def unitValue:String	= unitType + ".INSTANCE"
		
		def unitType:String	= basePackage + "Unit"	
		
		val basePackage:String = "net.psammead.functional."
	}
	
	class FjJavaCode(functionalPrefix:String) extends JavaCode(functionalPrefix) {
		def functionalLibrary = "fj"
		
		def clazz(sOriginal:String, sPackage:String, sSimpleName:String, sMethods:List[String]):String = 
				List(
					"package " + sPackage + ";",
					"",
					"/** mirrors {@link " + sOriginal + "} */",
					//"@javax.annotation.Generated(\"net.psammead.scgen.processor.FunctionProcessor\")",
					//"""@SuppressWarnings({"deprecation","cast","unchecked"})""",
					"@java.lang.SuppressWarnings(\"all\")",
					"public final class " + sSimpleName + " {",
					"\t/** utility class */",
					"\tprivate " + sSimpleName + "() {}"
				).mkString("\n") +
				"\n\n" +
				indent("\t")(sMethods.mkString("\n\n")) +
				"\n" +
				List(
					"}"
				).mkString("\n")
		
		def boxValue(sOutType:String, sReturn:String):String =
				"return new " + boxType(sOutType) + "() {\npublic " + sOutType + " _1() {\n" + sReturn + " } };"
				
		def boxType(sOutType:String):String =
				basePackage + "P1<" + sOutType + ">"
				
		def functionValue(sInType:String, sOutType:String, sInName:String, sReturn:String):String =
				"return new " + functionType(sInType, sOutType) + "() {\npublic " + sOutType + " f(final " + sInType + " " + sInName + ") {\n" + sReturn + " } };"
				
		def functionType(sInType:String, sOutType:String):String = 
				basePackage + "F<" + sInType + "," + sOutType + ">"
		
		def uncheckedException:String =  "java.lang.RuntimeException" 
				
		def unitValue:String	= unitType + ".unit()"
		
		def unitType:String	= basePackage + "Unit"	
		
		val basePackage:String = "fj."
	}
	
	//------------------------------------------------------------------------------
	//## type serializer
	
	class TypeString(tAllVariables:List[TypeVariable]) {
		def typeVarExtract(tVariable:TypeVariable):String = tVariable.asElement.getSimpleName.toString
		def typeVarName:(TypeVariable=>String) = Unique.name(tAllVariables, typeVarExtract _)
		
		// TODO cleanup. unboxcasting here is ugly
		def processArguments(eVariables:List[VariableElement]):String =
				javaCode.valueKlammer(eVariables.map(processArgument))
		def processArgument(eVariable:VariableElement):String =
				processPrimitiveCast(eVariable.asType) + eVariable.getSimpleName.toString
				
		def processPrimitiveCast(tAny:TypeMirror) = 
				if (tAny.getKind.isPrimitive)	"(" + processType(tAny) + ")"
				else							""
				
		def processTypeArguments(eTypeParameters:List[TypeParameterElement]):String =
				javaCode.typeKlammer(eTypeParameters.map(processTypeArgument))
		def processTypeArgument(eTypeParameter:TypeParameterElement):String = 
				typeVarName(typeOfTypeParameter(eTypeParameter))
			
		def processTypeParameters(eTypeParameters:List[TypeParameterElement]):String =
				javaCode.typeKlammer(eTypeParameters.map(processTypeParameter))
		def processTypeParameter(eTypeParameter:TypeParameterElement):String = {
			val	bounds	= eTypeParameter.getBounds.toScalaList
			val simple	= bounds.isEmpty || bounds.size == 1 &&	isObjectType(bounds.head)
			// was eTypeParameter.getSimpleName.toString
			typeVarName(typeOfTypeParameter(eTypeParameter)) + (
				if (simple)	"" 
				else		" extends " + bounds.map(processType).mkString(" & ")
			)
		}
		
		//---------------------------------------------------------------------
			
		// NOTE: for some reason, a normal class can be a PrimitiveType
		def processTypeBoxed(tAny:TypeMirror):String = tAny.getKind match {
			case TypeKind.VOID			=> javaCode.unitType
			case x if (x.isPrimitive)	=> processType(types.boxedClass(tAny.asInstanceOf[PrimitiveType]).asType)
			case _						=> processType(tAny)
		}
		
		def processTypes(tAnys:List[TypeMirror]) = tAnys.map(processType)
		def processType(tAny:TypeMirror):String = 
				tAny.accept(new UnparseTypeMirror(), null)
				
		// see openjdk/langtools/src/share/classes/com/sun/tools/javac/code/Type.java
		class UnparseTypeMirror extends TypeVisitor[String,Void] {
			def visitArray(t:ArrayType, x:Void):String = t.getComponentType.accept(this, x) + "[]"
			def visitDeclared(t:DeclaredType, x:Void):String = {
				val eType:TypeElement	= t.asElement.asInstanceOf[TypeElement]
				val tTypeArguments		= t.getTypeArguments.toScalaList
				var sTypeArguments		= javaCode.typeKlammer(tTypeArguments.map(_.accept(this, x)))
				eType.getNestingKind match {
					case NestingKind.TOP_LEVEL	=> eType.getQualifiedName.toString + sTypeArguments
					// NOTE inner classes need the enclosing type. 
					// static types just need the qualified name!
					// see isInnerClass for elements
					/*
					// TODO borken on eclipse for java.util.Map.Entry .. why?
					// @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=100546
					case NestingKind.MEMBER	=> {
						msg.note("### found member " + t + " being " + (if (isStatic(eType)) "static" else "nonstatic") + " enclosed in " +  t.getEnclosingType + " which is a " + t.getEnclosingType.getKind)
						t.getEnclosingType.getKind match {
							case TypeKind.NONE		=> eType.getQualifiedName.toString + sTypeArguments
							case _					=> t.getEnclosingType.accept(this, x) + "." + eType.getSimpleName.toString + sTypeArguments
						}
					}
					*/
					case NestingKind.MEMBER	=> 
						if (isStatic(eType))	eType.getQualifiedName.toString + sTypeArguments
						else 					t.getEnclosingType.accept(this, x) + "." + eType.getSimpleName.toString + sTypeArguments
					case _ /*ANONYMOUS & LOCAL*/	=> cannotUnparse(t)
				}
			}
			def visitNoType(t:NoType, x:Void):String = "void"
			def visitPrimitive(t:PrimitiveType, x:Void):String = t.getKind match {
				case TypeKind.BOOLEAN	=> "boolean"
				case TypeKind.CHAR		=> "char"
				case TypeKind.BYTE		=> "byte"
				case TypeKind.SHORT		=> "short"
				case TypeKind.INT		=> "int"
				case TypeKind.LONG		=> "long"
				case TypeKind.FLOAT		=> "float"
				case TypeKind.DOUBLE	=> "double"
				case _					=> msg.error("cannot unparse PrimitiveType: " + t);	""
			}
			def visitTypeVariable(t:TypeVariable, x:Void):String = typeVarName(t)	
			def visitWildcard(t:WildcardType, x:Void):String = {
				val extendsBound	= t.getExtendsBound
				val superBound		= t.getSuperBound
				"?" + 
				(if (extendsBound == null || isObjectType(extendsBound))	"" else (" extends " + extendsBound.accept(this, x))) +
				(if (superBound   == null)									"" else (" super "   + superBound.accept(this, x)))
			}
			def visit(t:TypeMirror):String							= cannotUnparse(t)
			def visit(t:TypeMirror, x:Void):String					= cannotUnparse(t)
			def visitExecutable(t:ExecutableType, x:Void):String	= cannotUnparse(t)
			def visitError(t:ErrorType, x:Void):String				= cannotUnparse(t)
			def visitNull(t:NullType, x:Void):String				= cannotUnparse(t)
			def visitUnknown(t:TypeMirror, x:Void):String			= cannotUnparse(t)
			// could use a default action
			private def cannotUnparse(t:TypeMirror) = { msg.error("cannot unparse: " + t); "" }
		}
		
		def processShortType(tAny:TypeMirror):String = 
				tAny.accept(new UnparseShortTypeMirror(), null)
				
		// see openjdk/langtools/src/share/classes/com/sun/tools/javac/code/Type.java
		class UnparseShortTypeMirror extends TypeVisitor[String,Void] {
			def visitArray(t:ArrayType, x:Void):String				= t.getComponentType.accept(this, x) + "Array"
			def visitDeclared(t:DeclaredType, x:Void):String		= t.asElement.asInstanceOf[TypeElement].getSimpleName.toString
			def visitNoType(t:NoType, x:Void):String				= "void"
			def visitPrimitive(t:PrimitiveType, x:Void):String		= t.toString
			def visitTypeVariable(t:TypeVariable, x:Void):String	= typeVarName(t)	
			def visitWildcard(t:WildcardType, x:Void):String		= "Wild"
			def visit(t:TypeMirror):String							= cannotUnparse(t)
			def visit(t:TypeMirror, x:Void):String					= cannotUnparse(t)
			def visitExecutable(t:ExecutableType, x:Void):String	= cannotUnparse(t)
			def visitError(t:ErrorType, x:Void):String				= cannotUnparse(t)
			def visitNull(t:NullType, x:Void):String				= cannotUnparse(t)
			def visitUnknown(t:TypeMirror, x:Void):String			= cannotUnparse(t)
			// could use a default action
			private def cannotUnparse(t:TypeMirror) = { msg.error("cannot unparse: " + t); "" }
		}
	}
	
	//------------------------------------------------------------------------------
	//## type & element shortcuts
	
	def isVoidType(tAny:TypeMirror):Boolean = types.isSameType(tAny, types.getNoType(TypeKind.VOID))
	
	def isObjectType(tAny:TypeMirror):Boolean = types.isSameType(tAny, objectType)
	def objectType:TypeMirror	= objectElement.asType
	def objectElement:Element	= elements.getTypeElement("java.lang.Object")
	
	def objectMethodNames:List[String]	=
			ElementFilter.methodsIn(objectElement.getEnclosedElements).toScalaList
			.map(_.getSimpleName.toString)
	
	def outerClasses(eEnclosing:TypeElement):List[TypeElement] =
			eEnclosing ::  (
				if (isInnerClass(eEnclosing))	outerClasses(enclosingType(eEnclosing))
				else 							Nil
			)
	
	/** ok for executables and types if nested as member or top_level */
	def enclosingType(eAny:Element):TypeElement = 
			eAny.getEnclosingElement.asInstanceOf[TypeElement]
	
	/** check: casting is safe, TypeParameterElement always correspond to a TypeVariable */
	def typeOfTypeParameter(eTypeParameter:TypeParameterElement):TypeVariable =
			eTypeParameter.asType.asInstanceOf[TypeVariable]
	
	def isInnerClass(eType:TypeElement) =
			eType.getNestingKind == NestingKind.MEMBER && !isStatic(eType)
			
	def isPublicReachable(eType:TypeElement):Boolean =
			isPublic(eType) && (eType.getNestingKind match {
				case NestingKind.MEMBER		=> isPublicReachable(enclosingType(eType))
				case NestingKind.TOP_LEVEL	=> true
				case _						=> false
			})

	// ElementFilter constants
	val constructorKind	= Set(ElementKind.CONSTRUCTOR)
	val fieldKinds		= Set(ElementKind.FIELD, ElementKind.ENUM_CONSTANT)
	val methodKind		= Set(ElementKind.METHOD)
	val packageKind		= Set(ElementKind.PACKAGE)
	val typeKinds		= Set(ElementKind.CLASS, ElementKind.ENUM, ElementKind.INTERFACE, ElementKind.ANNOTATION_TYPE)
	// without inner classes!
	val memberKinds		= fieldKinds ++ constructorKind ++ methodKind
			
	def isMemberKind(eAny:Element)	= memberKinds.contains(eAny.getKind)
	def isTypeKind(eAny:Element)	= typeKinds.contains(eAny.getKind)
				
	def isRelevantMember(eAny:Element)	= isPublic(eAny) && !isDeprecated(eAny)
	def isNotDeprecated(eAny:Element)	= !isDeprecated(eAny)
	
	def isStatic(eAny:Element)		= eAny.getModifiers.contains(Modifier.STATIC)
	def isPublic(eAny:Element)		= eAny.getModifiers.contains(Modifier.PUBLIC)
	def isAbstract(eAny:Element)	= eAny.getModifiers.contains(Modifier.ABSTRACT)
	//def isDeprecated(eAny:Element)	= eAny.getAnnotation(classOf[java.lang.Deprecated]) != null
	def isDeprecated(eAny:Element)	= elements.isDeprecated(eAny)

	/**  include inner classes, recursively */
	def deepTypes(eType:TypeElement):List[TypeElement] = 
			eType :: ElementFilter.typesIn(eType.getEnclosedElements).toScalaList.flatMap(deepTypes)
			
	/** remove all types being subtypes of another type in the list  */
	def removeSubclasses(tTypes:List[TypeMirror]):List[TypeMirror] = 
			tTypes.filter(checked =>
				tTypes.find(other =>
					// first type is assignable to the second, but they are not the same
					!types.isSameType(checked, other) &&
					types.isAssignable(checked, other)
				).isEmpty
			)

	//------------------------------------------------------------------------------
	//## message utils
	
	private object msg {
		def note(s:String)		{ print(Diagnostic.Kind.NOTE,		s) }
		def warning(s:String)	{ print(Diagnostic.Kind.WARNING,	s) }
		def error(s:String)		{ print(Diagnostic.Kind.ERROR,		s) }
		def other(s:String)		{ print(Diagnostic.Kind.OTHER,		s) }
		
		private def print(kind: Diagnostic.Kind, message:String) { 
			messager.printMessage(Diagnostic.Kind.NOTE, "FunctionProcessor: " + message) 
		}
	}
}
