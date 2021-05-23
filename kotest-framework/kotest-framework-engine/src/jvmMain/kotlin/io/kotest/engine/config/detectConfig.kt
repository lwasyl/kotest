package io.kotest.engine.config

import io.github.classgraph.ClassGraph
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.mpp.env
import io.kotest.mpp.sysprop
import java.util.*

/**
 * Loads a config object from system properties, by scanning the classpath and from ServiceLoader.
 *
 * Classpath scanning for [AbstractProjectConfig] can be disabled by use of the
 * [KotestEngineProperties.disableConfigurationClassPathScanning] system property.
 *
 * Loading [AbstractProjectConfig] from ServiceLoader can be enabled with
 * [KotestEngineProperties.enableServiceLoaderConfigurationDiscovery] property. Loading listeners, extensions and
 * filters can be enabled iwth [KotestEngineProperties.enableServiceLoaderExtensionsDiscovery] property.
 */
actual fun detectConfig(): DetectedProjectConfig {

   var config = loadConfigFromSystemProperties()

   if (
      sysprop(KotestEngineProperties.disableConfigurationClassPathScanning) == null ||
      sysprop(KotestEngineProperties.disableAutoScanClassPathScanning) == null
   ) {

      // we scan once for speed and share the results with both config loader and autoscan loader
      classgraph().scan().use { result ->

         if (sysprop(KotestEngineProperties.disableConfigurationClassPathScanning) == null) {
            config = config.merge(loadConfigFromAbstractProjectConfig(result))
         }

         if (sysprop(KotestEngineProperties.disableAutoScanClassPathScanning) == null) {
            config = config.merge(loadConfigFromAutoScanInstances(result))
         }
      }
   }

   if (sysprop(KotestEngineProperties.enableServiceLoaderConfigurationDiscovery) != null) {
      config = config.merge(loadConfigsFromServiceLoader())
   }

   if (sysprop(KotestEngineProperties.enableServiceLoaderExtensionsDiscovery) != null) {
      config = config.merge(loadConfigFromServiceLoaderExtensions())
   }

   return config
}

internal fun classgraph(): ClassGraph {
   return ClassGraph()
      .enableClassInfo()
      .enableExternalClasses()
      .enableAnnotationInfo()
      .ignoreClassVisibility()
      .disableNestedJarScanning()
      .rejectPackages(
         "java.*",
         "javax.*",
         "sun.*",
         "com.sun.*",
         "kotlin.*",
         "kotlinx.*",
         "androidx.*",
         "org.jetbrains.kotlin.*",
         "org.junit.*"
      ).apply {
         if (env(KotestEngineProperties.disableJarDiscovery) == "true" ||
            sysprop(KotestEngineProperties.disableJarDiscovery) == "true"
         ) {
            disableJarScanning()
         }
      }
}
