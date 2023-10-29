package dev.celestica

import dev.celestica.ExtendWith.Field

@ExtendWith(Field[String]("address"), Field[Int]("age"))
case class Person(name: String)

object Main extends App {
  // Create an instance of Person.ExtendedPerson
  val extendedPerson = Person.ExtendedPerson("John Doe", "123 Street Name", 25)
  println(s"extendedPerson: $extendedPerson")
}
