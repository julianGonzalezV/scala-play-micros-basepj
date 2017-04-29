package persistence.repos

import javax.inject.Inject

import cats.data.Reader
import domain.PersonExample1
import persistence.tables.PersonTable._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.db.NamedDatabase
import slick.backend.DatabaseConfig
import slick.dbio.Effect.Read
import slick.profile.FixedSqlStreamingAction

import scala.concurrent.{ExecutionContext, Future}
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend

class PersonRepoDIEngine  @Inject()(@NamedDatabase("mydatabase") val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with PersonaTransformador{

  def all(implicit ec: ExecutionContext): Future[Seq[PersonExample1]] = {

    db.run(TB_Personas.result).map(_.map(registroToDominio(_)))


    //La linea anterior un poco desglosada es equivalente a lo siguiente:
    /*
    val dbio: FixedSqlStreamingAction[Seq[PersonaRegistro], PersonaRegistro, Read] = TB_Personas.result
    val f: Future[Seq[PersonaRegistro]] = db.run(dbio)
    f.map(seq => seq.map(registroToDominio(_)))
     */

  }
}

object personRepoReader extends PersonaTransformador{

  val dbconfigH2: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.mydatabase")

  def all(implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile], Future[Seq[PersonExample1]]] = Reader {
    case dbconfig: DatabaseConfig[JdbcProfile] =>
      val db: JdbcBackend#DatabaseDef = dbconfig.db
      db.run(TB_Personas.result).map(_.map(registroToDominio(_)))

  }

  def count(implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile], Future[Int]] = Reader {
    case dbconfig: DatabaseConfig[JdbcProfile] =>
      val db: JdbcBackend#DatabaseDef = dbconfig.db
      db.run(TB_Personas.length.result)
  }

  def countWithFilter(a:String)(implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile], Future[Int]] = Reader {
    case dbconfig: DatabaseConfig[JdbcProfile] =>
      dbconfig.db.run(TB_Personas.filter(p => p.apellido === a).length.result)
  }
  
  def insert(p:PersonExample1)(implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile], Future[Int]] = Reader {
    case dbconfig: DatabaseConfig[JdbcProfile] =>
      val db = dbconfig.db
      val resInsert: Future[Int] = db.run(TB_Personas += p)
      resInsert
      //resInsert.map { _ => () }
  }

  def find(a:String)(implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile], Future[Seq[PersonExample1]]] = Reader {
    case dbconfig: DatabaseConfig[JdbcProfile] =>
      val r1: Future[Seq[PersonRecord]] = dbconfig.db.run(TB_Personas.filter(p => p.apellido === a).result)
      r1.map(seqp => seqp.map(p => registroToDominio(p)))
  }
}
