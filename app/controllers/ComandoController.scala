package controllers

import javax.inject._

import domain.PersonExample1
import persistence.repos.personRepoReader
import play.api.libs.json.Json
import play.api.mvc._
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class ComandoController @Inject() extends Controller {

  def insertarPersonaReader() = Action.async { request =>
    implicit val empleadoFormat = Json.format[PersonExample1]
    val json = request.body.asJson.get
    val p = json.as[PersonExample1]
    implicit val ec: ExecutionContext = persistence.repos.personaRepoCommandExecutionContext
    val dbconfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.mydatabase")
    personRepoReader.insert(p)
      .run(dbconfig)
      .map{
        x =>  Ok("Persona insertada")
      }
      .recover{
        case e: Exception =>  InternalServerError(s"Ha ocurrido un error en insertarPersonaReader: ${e}")
      }


  }
}

