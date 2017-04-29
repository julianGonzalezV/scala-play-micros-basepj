package persistence.repos

import cats.data.Reader
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.backend.DatabaseConfig
import slick.driver.H2Driver.api._
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend

import scala.concurrent.{ExecutionContext, Future}
import domain.{AddressDomain, PersonDomain, PersonExample1}
import persistence.tables.ContextTables.Person
import persistence.tables.ContextTables.Address
import slick.dbio.Effect.Write
import slick.profile.FixedSqlAction


object EjercicioRepo extends recordToDomainTransformer{

  val dbconfigH2: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.mydatabase")


  //select * from PERSON
  def all(implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile], Future[Seq[PersonDomain]]] = Reader {
    case dbconfig: DatabaseConfig[JdbcProfile] =>
      import persistence.tables.ContextTables._
      val db: JdbcBackend#DatabaseDef = dbconfig.db

      val innerJoin = for {
        (p, a) <- people join addresses on (_.addressId === _.id)
      } yield (p, a)


      val r1: Future[Seq[(Person, Address)]] = db.run(innerJoin.result)
      val r:  Future[Seq[PersonDomain]] = db.run(innerJoin.result).map(x => x.map(y=> registroToDominioPA(y._1, y._2)))
      r
  }


  //select * from PERSON where AGE >= 18 AND NAME = 'C. Vogt'
  def filter(age:Int, name:String)(implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile], Future[Seq[PersonDomain]]] = Reader {
    case dbconfig: DatabaseConfig[JdbcProfile] =>
      import persistence.tables.ContextTables._
      val db: JdbcBackend#DatabaseDef = dbconfig.db


      val innerJoin = for {
        (p, a) <- people join addresses on (_.addressId === _.id)
      } yield (p, a)

//s"Ha ocurrido un error en insertarPersonaReader: ${e}"
      val result  = db.run(innerJoin.filter(p=> p._1.age === age && p._1.name.like(s"%${name}%")).result).map(x => x.map(y=> registroToDominioPA(y._1, y._2)))
      //lo que hay en r2 es un Action y le puedo compner  r2.flatMap() y devuelve todo otro Action
      //ejemplo tener un r1 que haga insert, otro que haga update
      //DBIOAction por ejemplo si tengo un combo de sentencias compuetsas por flatmap en r2
      //y se lo mando a DBIOAction para que en una sola conexion funciones
      //EJEMPLO TODLAS LAS PERSONAS DE APELLIDO X  E INSERTAR ESA MISMA PERSONA CAMBIANDOLE ALGO
      //DBIOAction.seq(r2)
      //val r: Future[Seq[PersonDomain]] = db.run(people.filter(p => p.age>=age && p.name ===name).result).map(_.map(registroToDominio(_)))
      result
  }

  //select * from PERSON order by AGE asc, NAME
  def allOrdered (implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile], Future[Seq[PersonDomain]]] = Reader {
    case dbconfig: DatabaseConfig[JdbcProfile] =>
      import persistence.tables.ContextTables._
      val db: JdbcBackend#DatabaseDef = dbconfig.db
      //val r2: Future[Seq[PersonDomain]] = db.run(people.sortBy(x=>(x.age, x.name)).result).map(_.map(registroToDominio(_)))

      //val r: Future[Seq[PersonDomain]] = db.run(people.sortBy(_.age).sortBy(_.name).result).map(_.map(registroToDominio(_)))
      val innerJoin = for {
        (p, a) <- people join addresses on (_.addressId === _.id)
      } yield (p, a)

      val result  = db.run(innerJoin.sortBy(p=> (p._1.age,p._1.name)).result).map(x => x.map(y=> registroToDominioPA(y._1, y._2)))
      result
  }

//  select ADDRESS_ID, AVG(AGE)
//  from PERSON
//    group by ADDRESS_ID
  def addressByAvgAge (implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile], Future[Map[String, Int]]] = Reader {

  case dbconfig: DatabaseConfig[JdbcProfile] =>
    import persistence.tables.ContextTables._
    def promedio(edades: Seq[Int]) = edades.sum / edades.length
    val db: JdbcBackend#DatabaseDef = dbconfig.db
    val innerJoin = for {
      (p, a) <- people join addresses on (_.addressId === _.id)
    } yield (p, a)

    val v1: Future[Map[String, Seq[(Person, Address)]]] = db.run(innerJoin.result).map{ x => x.groupBy(x=> x._2.city)}
    val v2 = db.run(innerJoin.result).map{ x => x.groupBy(x=> x._2.city).map(kv => ( kv._1, promedio(kv._2.map(x => x._1.age))))}

    v2
}




  //insert into PERSON (NAME, AGE, ADDRESS_ID) values ('M Odersky', 12345, 1)
  def insert(p:Person, ad: Address) (implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile],  Future[Int]] = Reader {

    case dbconfig: DatabaseConfig[JdbcProfile] =>
      import persistence.tables.ContextTables.people
      import persistence.tables.ContextTables.addresses
      val db = dbconfig.db
      val insertAddress: FixedSqlAction[Int, NoStream, Write] = addresses+= ad
      val insertP: FixedSqlAction[Int, NoStream, Write] = people += p


      val person = p.copy(adressId = ad.id)
      val fullPersonInsert = for{
        addresPkFromInserted <-  ( addresses returning addresses.map(_.id) )insertOrUpdate  ad
        personInserted <- people += person
      }yield personInserted

    //val resInsert: Future[Int] = db.run(people += p)
    val resInsert: Future[Int] = db.run(fullPersonInsert)
      resInsert
  }

  //update PERSON set NAME='M. Odersky', AGE=54321 where NAME='M Odersky'
  def update(p:Person) (implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile],  Future[Int]] = Reader {
    case dbconfig: DatabaseConfig[JdbcProfile] =>
      import persistence.tables.ContextTables.people
      val db = dbconfig.db
      val q = for{
        pr <- people if pr.id == p.id
      }yield (pr.name, pr.age)
      val updateAction = q.update(p.name, p.age)
      val resInsert: Future[Int] = db.run(updateAction)
      resInsert
  }

  //delete PERSON where NAME='M. Odersky'
  def delete(id:Int) (implicit ec: ExecutionContext): Reader[DatabaseConfig[JdbcProfile],  Future[Int]] = Reader {
    case dbconfig: DatabaseConfig[JdbcProfile] =>
      import persistence.tables.ContextTables.people
      val db = dbconfig.db
      val resInsert = db.run(people.filter(x=> x.id === id).delete)
      resInsert
  }
}
