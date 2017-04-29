package persistence.repos

import domain.PersonExample1
import persistence.tables.PersonTable.PersonRecord

trait PersonaTransformador {
  implicit def registroToDominio(pr: PersonRecord): PersonExample1 = {
    PersonExample1(pr.dni, pr.nombre, pr.apellido)
  }

  implicit def transformarDominioARegistro(p: PersonExample1): PersonRecord = {
    PersonRecord(p.dni, p.nombre, p.apellido)
  }
}