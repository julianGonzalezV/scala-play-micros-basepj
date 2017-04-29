package controllers

import javax.inject._

import cats.data.Reader
import domain.PersonExample1
import persistence.repos.PersonRepoDIEngine
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ConsultaController @Inject()(repo: PersonRepoDIEngine) extends Controller {

  def ejecutarSaludoPrueba = Action { request =>
    val qs = request.queryString
    val nombre = qs.get("nombre").getOrElse(Nil).headOption.getOrElse("NO NAME")
    Ok(s"Mi primer metodo en Controller, soy feliz. EL parametro nombre es: ${nombre}")
  }

  def ejecutarSaludoPruebaFuture = Action.async {
    import scala.concurrent.ExecutionContext.Implicits.global
    Future(Ok("Mi primer metodo en Controller, soy feliz."))
  }

  def consultarPersonaDIEngine = Action.async {
    Logger.debug("Ejecutando consultarPersonaDIEngine")
    implicit val personaWrites = Json.writes[PersonExample1]
    implicit val ec: ExecutionContext = persistence.repos.personaRepoQueryExecutionContext
    val personas: Future[Seq[PersonExample1]] = repo.all
    personas.map(sp => Ok(Json.toJson(sp)))

  }

  def consultarPersonaReader = Action.async {
    import persistence.repos.personRepoReader
    implicit val empleadoFormat = Json.format[PersonExample1]
    implicit val ec: ExecutionContext = persistence.repos.personaRepoQueryExecutionContext
    val dbconfig = personRepoReader.dbconfigH2
    val personas: Reader[DatabaseConfig[JdbcProfile], Future[Seq[PersonExample1]]] = personRepoReader.all
    val res = personas.run(dbconfig)
    res.map(x => Ok(Json.toJson(x)))
  }

  def consultarPorApellido(apellido:String) = Action.async{
    import persistence.repos.personRepoReader
    implicit val empleadoFormat = Json.format[PersonExample1]
    implicit val ec: ExecutionContext = persistence.repos.personaRepoQueryExecutionContext
    val dbconfig = personRepoReader.dbconfigH2
    val personas: Reader[DatabaseConfig[JdbcProfile], Future[Seq[PersonExample1]]] = personRepoReader.find(apellido)
    val res = personas.run(dbconfig)
    res.map(x => Ok(Json.toJson(x)))
  }

  def count() = Action.async {
    import persistence.repos.personRepoReader
    implicit val ec: ExecutionContext = persistence.repos.personaRepoQueryExecutionContext
    val dbconfig = personRepoReader.dbconfigH2
    val countReader: Reader[DatabaseConfig[JdbcProfile], Future[Int]]= personRepoReader.count
    val res = countReader.run(dbconfig)
    res.map(x => Ok(Json.toJson(x)))
  }

  def countPorApellido(a:String) = Action.async {
    import persistence.repos.personRepoReader
    implicit val ec: ExecutionContext = persistence.repos.personaRepoQueryExecutionContext
    val dbconfig = personRepoReader.dbconfigH2
    val countReader: Reader[DatabaseConfig[JdbcProfile], Future[Int]]= personRepoReader.countWithFilter(a)
    val res = countReader.run(dbconfig)
    res.map(x => Ok(Json.toJson(x)))
  }

}

