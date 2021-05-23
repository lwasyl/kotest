package com.sksamuel.kotest.autoscan

import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan

object Container {
   var results = ""
}

@AutoScan
object MyObjectProjectListener : ProjectListener {
   override suspend fun beforeProject() {
      Container.results = Container.results + "A"
   }

   // we have two after project listeners, so at least one must have been executed when we get to them both
   override suspend fun afterProject() {
      Container.results = Container.results + "B"
      verifyAfterProjectListeners()
   }

}

@AutoScan
class MyClassProjectListener : ProjectListener {
   override suspend fun beforeProject() {
      Container.results = Container.results + "C"
   }

   // we have two after project listeners, so at least one must have been executed when we get to them both
   override suspend fun afterProject() {
      Container.results = Container.results + "D"
      verifyAfterProjectListeners()
   }
}

class ServiceLoaderProjectListenerClass : ProjectListener {

   override suspend fun beforeProject() {
      Container.results = Container.results + "E"
   }

   override suspend fun afterProject() {
      Container.results = Container.results + "F"
      verifyAfterProjectListeners()
   }
}

private fun verifyAfterProjectListeners() {
   val results = setOf('B', 'D', 'F')
   if (Container.results.none { it in results })
      error("boom")
}
