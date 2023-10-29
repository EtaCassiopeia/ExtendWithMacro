# Extend-With Macro

The `@ExtendWith` macro is a Scala-based solution designed to alleviate the boilerplate associated with extending case classes to include additional fields. By annotating a base model with `@ExtendWith` and specifying the new fields, the macro generates an extended model encompassing both the original and the additional fields. This project showcases the implementation of this macro, demonstrating the potent combination of Scala and metaprogramming to automate routine coding tasks.

## Features

- Automatically generate extended case classes with specified additional fields.
- Reduce boilerplate and adhere to DRY (Don't Repeat Yourself) principle.
- Enhance model extensibility with minimal manual code intervention.

## Installation

To use the `@ExtendWith` macro in your project, you need to first publish it locally using sbt. Follow the steps below:

1. Clone the repository to your local machine.
```bash
git clone https://github.com/EtaCassiopeia/extend-with-macro.git
cd extend-with-macro
```

2. Publish the macro locally using sbt.
```bash
sbt publishLocal
```

3. Now, you can include the `@ExtendWith` macro in your Scala project by adding the following line to your `build.sbt` file:
```scala
libraryDependencies += "dev.celestica" %% "extend-with-macro" % "0.1.0"
```

## Usage

After including the `@ExtendWith` macro in your project, you can use it to extend your case classes as shown below:

```scala
import dev.celestica.ExtendWith.Field

@ExtendWith(Field[String]("address"), Field[Int]("age"))
case class Person(name: String)

// Usage of the extended case class
val extendedPerson = Person.ExtendedPerson("John Doe", "123 Street Name", 25)
```
---

## Further Reading

For a more in-depth description and discussion on the `@ExtendWith` macro, its implementation, and its usage, feel free to read the accompanying blog post published on Medium: [Harnessing Scala Macros to Extend Models: A Deep Dive](https://medium.com/@zainalpour_79971/harnessing-scala-macros-to-extend-models-a-deep-dive-cef769ff18fd).