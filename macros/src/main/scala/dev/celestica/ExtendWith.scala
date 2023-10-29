package dev.celestica

import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import scala.annotation.{StaticAnnotation, compileTimeOnly}

/**
 * The `@ExtendWith` macro annotation facilitates extending case classes with additional fields.
 * By annotating a case class with `@ExtendWith` and specifying the new fields as parameters, a new extended case class is generated.
 *
 * The syntax for specifying new fields is through the `Field` case class provided within the `ExtendWith` object.
 * Each `Field` takes a name and a type as arguments, encapsulating the new field's information.
 *
 * Example Usage:
 * {{{
 * @ExtendWith(Field[String]("address"), Field[Int]("age"))
 * case class Person(name: String)
 *
 * val extendedPerson = Person.ExtendedPerson("John Doe", "123 Street Name", 25)
 * }}}
 *
 * In the example above, `@ExtendWith` generates a new case class `ExtendedPerson` with fields from `Person` plus the additional specified fields.
 */

@compileTimeOnly("Enable macro paradise to expand macro annotations")
class ExtendWith(fields: ExtendWith.Field[_]*) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ExtendWithImpl.impl
}

object ExtendWith {
  /** Encapsulates information about a new field to be added to the extended case class. */
  case class Field[T](name: String)
}

object ExtendWithImpl {
  /** Macro implementation for `@ExtendWith`. */
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    /**
     * Extracts essential parts of the annotated case class for later use in generating the extended case class.
     *
     * @param classDecl The AST of the annotated case class.
     * @return A tuple containing the name, fields, parents, and body of the annotated case class.
     */
    def extractCaseClassParts(
                               classDecl: c.universe.ClassDef
                             ): (c.universe.TypeName, List[c.universe.Tree], List[c.universe.Tree], List[c.universe.Tree]) =
      classDecl match {
        // Matches the structure of a Scala case class and extracts its components.
        case q"case class $className(..$fields) extends ..$parents { ..$body }" =>
          (className, fields, parents, body)
      }

    // This block extracts the field information specified in the `@ExtendWith` annotation.
    val fields: List[(String, c.universe.Type)] = c.prefix.tree match {
      case Apply(_, args) =>
        // Iterates through the arguments of the annotation.
        args.collect {
          // Matches the structure of Field arguments in the annotation, extracting field name and type.
          case Apply(TypeApply(_, List(tpt)), List(Literal(Constant(name: String)))) =>
            (name, c.typecheck(tpt, c.TYPEmode).tpe) // Obtains the type of the field from the type tree.
        }
      case _ =>
        // Halts macro expansion and reports an error if the annotation arguments don't match the expected structure.
        c.abort(c.enclosingPosition, "Invalid annotation arguments")
    }


    /**
     * Generates a companion object containing the extended class.
     */
    annottees.map(_.tree).toList match {
      case (classDecl: ClassDef) :: Nil =>
        // Extracts essential components from the annotated case class.
        val (className, existingFields, parents, body) = extractCaseClassParts(classDecl)
        val extendedClassName = TypeName(s"Extended$className")
        // Combines existing fields with the new fields specified in the `@ExtendWith` annotation.
        val allFields = existingFields ++ fields.map { case (name, tpe) =>
          q"val ${TermName(name)}: $tpe"
        }
        c.Expr[Any](
          q"""
          $classDecl  // Original case class
          object ${className.toTermName} {  // Companion object
            case class $extendedClassName(..$allFields) extends ..$parents {
              ..$body
            }
          }
          """
        )
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}
