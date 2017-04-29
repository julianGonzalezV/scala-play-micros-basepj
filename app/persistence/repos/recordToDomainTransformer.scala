package persistence.repos

import domain.{AddressDomain, PersonDomain}
import persistence.tables.ContextTables.{Address, Person}

/**
  * Created by juligove on 2017/02/02.
  * De igual forma podemos tener los transformadores separados
  * pero acá metí los del bounded context que deseo
  */
trait recordToDomainTransformer {

  implicit def registroToDominioPA(pr: Person, ad: Address): PersonDomain = {
    PersonDomain(pr.id, pr.name, pr.age,AddressDomain(ad.id, ad.street, ad.city))
  }


  implicit def domainToRegistro(pD: PersonDomain): Person ={
    Person(pD.id, pD.name, pD.age,pD.adressId.id)
  }

  implicit def addressDomainToRegistro(adD:AddressDomain): Address ={
    Address(adD.id, adD.street, adD.city)
  }


}

