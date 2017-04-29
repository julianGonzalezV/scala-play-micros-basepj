package persistence.tables

import slick.driver.H2Driver.api._
//import com.typesafe.slick.driver.oracle.OracleDriver.api._

object PersonTable {

  val TB_Personas = TableQuery[TB_PERSONAS]

  class TB_PERSONAS(tag: Tag) extends Table[PersonRecord](tag, "TB_PERSONAS") {

    def dni = column[String]("DNI", O.PrimaryKey)
    def nombre = column[String]("NOMBRE")
    def apellido = column[String]("APELLIDO")

    def pk = primaryKey("TB_PERSONAS_PK", dni)

    def * = (dni, nombre, apellido) <> (PersonRecord.tupled, PersonRecord.unapply _)
  }
  case class PersonRecord(dni: String, nombre: String, apellido: String)
}
