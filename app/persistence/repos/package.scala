package persistence

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext

package object repos {
  val personaRepoQueryExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(15))
  val personaRepoCommandExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(15))

  val ejercicioRepoQueryExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(15))
}
