package persistence.tables

import slick.driver.H2Driver.api._

/**
  * Este es un ejemplo claro  de que tambien podemos tener
  * varias tablas en una sola clase o cada una separada como PersonTable
  */
object ContextTables {

  case class Person(id:Int, name:String, age:Int, adressId:Int)

  class People(tag: Tag) extends Table[Person](tag, "PERSON") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def age = column[Int]("AGE")
    def addressId = column[Int]("ADDRESS_ID")
    def * = (id, name, age, addressId) <> (Person.tupled, Person.unapply _)

    def address = foreignKey("ADDRESS", addressId, addresses)(_.id)
  }

  val people = TableQuery[People]

  case class Address(id:Int, street:String, city:String)

  class Addresses(tag: Tag) extends Table[Address](tag, "ADDRESS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def * = (id, street, city) <> (Address.tupled, Address.unapply _)
  }

  //en haskel es siempre lazy, en scala hay que decirle , para cuando se compile
  //no evalue
  lazy val addresses = TableQuery[Addresses]
}