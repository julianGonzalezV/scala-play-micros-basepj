package controllers

import javax.inject._

import cats.Id
import cats.data._
import domain.{AddressDomain, PersonDomain}
import persistence.repos.EjercicioRepo
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, Writes, _}
import play.api.mvc._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by juligove on 2017/02/02.
  */
@Singleton
class PersonController @Inject() extends Controller  {

  /**
    *
    * @return
    */
  def prueba1 = Action.async {
    import scala.concurrent.ExecutionContext.Implicits.global
    Future(Ok("Mi primer metodo en Controller, soy feliz."))
  }


  /**
    *
    * @return
    */
  def consultarPersonas = Action.async {
    implicit val addressFormat = Json.format[AddressDomain]
    implicit val personAddressWrites:Writes[PersonDomain] = (
        (__ \ "id").write[Int] and
        (__ \ "name").write[String] and
        (__ \ "age").write[Int] and
        (__ \ "address").write[AddressDomain]
      )(unlift(PersonDomain.unapply))

    implicit val ec: ExecutionContext = persistence.repos.ejercicioRepoQueryExecutionContext
    val dbconfig = EjercicioRepo.dbconfigH2
    val allPersons: Reader[DatabaseConfig[JdbcProfile], Future[Seq[PersonDomain]]] = EjercicioRepo.all
    val res = allPersons.run(dbconfig)
    res.map(x => Ok(Json.toJson(x)))

  }


  /**
    *
    * @param age
    * @param name
    * @return
    */
  def ageNameFilter(age:Int, name:String) = Action.async {
    implicit val addressFormat = Json.format[AddressDomain]
    implicit val personAddressWrites:Writes[PersonDomain] = (
      (__ \ "id").write[Int] and
        (__ \ "name").write[String] and
        (__ \ "age").write[Int] and
        (__ \ "address").write[AddressDomain]
      )(unlift(PersonDomain.unapply))
    implicit val ec: ExecutionContext = persistence.repos.ejercicioRepoQueryExecutionContext
    val dbconfig = EjercicioRepo.dbconfigH2
    val personsFilter: Reader[DatabaseConfig[JdbcProfile], Future[Seq[PersonDomain]]] = EjercicioRepo.filter(age, name)
    val res: Id[Future[Seq[PersonDomain]]] = personsFilter.run(dbconfig)
    res.map(x => Ok(Json.toJson(x)))
  }


  /**
    *
    * @return
    */
  def sortedPersons = Action.async {
    implicit val addressFormat = Json.format[AddressDomain]
    implicit val personAddressWrites:Writes[PersonDomain] = (
      (__ \ "id").write[Int] and
        (__ \ "name").write[String] and
        (__ \ "age").write[Int] and
        (__ \ "address").write[AddressDomain]
      )(unlift(PersonDomain.unapply))
    implicit val ec: ExecutionContext = persistence.repos.ejercicioRepoQueryExecutionContext
    val dbconfig = EjercicioRepo.dbconfigH2
    val allPersons: Reader[DatabaseConfig[JdbcProfile], Future[Seq[PersonDomain]]] = EjercicioRepo.allOrdered
    val res = allPersons.run(dbconfig)
    res.map(x => Ok(Json.toJson(x)))
  }


  /**
    *
    * @return
    */
  def addressByAvgAge = Action.async {
    implicit val ec: ExecutionContext = persistence.repos.ejercicioRepoQueryExecutionContext
    val dbconfig = EjercicioRepo.dbconfigH2
    val addressByAvgAgeReader: Reader[DatabaseConfig[JdbcProfile], Future[Map[String, Int]]] = EjercicioRepo.addressByAvgAge
    val res: Id[Future[Map[String, Int]]] = addressByAvgAgeReader.run(dbconfig)
    val t = res.map(x => {
      Ok(Json.toJson(x))
    } )
    t
  }


  /**
    *
    * @return
    */
  def insertPerson() = Action.async { request =>
    import persistence.repos.EjercicioRepo.{_}
    import persistence.repos.EjercicioRepo
    implicit val addressFormat = Json.format[AddressDomain]
    implicit val personAddressRead:Reads[PersonDomain] = (
      (__ \ "id").read[Int] and
        (__ \ "name").read[String] and
        (__ \ "age").read[Int] and
        (__ \ "address").read[AddressDomain]
      )(PersonDomain.apply _)

    val json = request.body.asJson.get
    val pD = json.as[PersonDomain]
    implicit val ec: ExecutionContext = persistence.repos.ejercicioRepoQueryExecutionContext
    val dbconfig: DatabaseConfig[JdbcProfile] = EjercicioRepo.dbconfigH2


     EjercicioRepo.insert(pD, pD.adressId)
      .run(dbconfig).map{x =>  Ok("Persona insertada")}
      .recover{
        case e: Exception =>  InternalServerError(s"Ha ocurrido un error en insertarPersonaReader: ${e}")
      }
  }


  /**
    * Method to update a record type person
    * @return
    */
  def updatePerson() = Action.async { request =>
    import persistence.repos.EjercicioRepo.{_}
    import persistence.repos.EjercicioRepo
    implicit val addressFormat = Json.format[AddressDomain]
    implicit val personAddressRead:Reads[PersonDomain] = (
      (__ \ "id").read[Int] and
        (__ \ "name").read[String] and
        (__ \ "age").read[Int] and
        (__ \ "address").read[AddressDomain]
      )(PersonDomain.apply _)

    val json = request.body.asJson.get
    val pD = json.as[PersonDomain]
    implicit val ec: ExecutionContext = persistence.repos.ejercicioRepoQueryExecutionContext
    val dbconfig: DatabaseConfig[JdbcProfile] = EjercicioRepo.dbconfigH2


    EjercicioRepo.update(pD)
      .run(dbconfig).map{x =>  Ok("Record changed")}
      .recover{
        case e: Exception =>  InternalServerError(s"Error on updatePerson op: ${e}")
      }
  }



  /**
    * Delete a person by Id
    * @param id
    * @return
    */
  def delete(id:Int) = Action.async {
    implicit val ec: ExecutionContext = persistence.repos.ejercicioRepoQueryExecutionContext
    val dbconfig = EjercicioRepo.dbconfigH2
    EjercicioRepo.delete(id)
      .run(dbconfig).map{x =>  Ok("Ok")}
      .recover{
        case e: Exception =>  InternalServerError(s"Error : ${e}")
      }
  }




}
