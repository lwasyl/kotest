package com.sksamuel.kotest.autoscan

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class AutoScanTestListenerTest : WordSpec({
   "@AutoScan TestListeners" should {
      "be detected for all tests" {
         AutoScanMyTestListener.beforeCounter.get() shouldBe 6
         AutoScanMyTestListener.afterCounter.get() shouldBe 0
         ServiceLoaderMyTestListenerObject.beforeCounter.get() shouldBe 6
         ServiceLoaderMyTestListenerObject.afterCounter.get() shouldBe 0
      }
      "even this one!" {
         AutoScanMyTestListener.beforeCounter.get() shouldBe 9
         AutoScanMyTestListener.afterCounter.get() shouldBe 3
         ServiceLoaderMyTestListenerObject.beforeCounter.get() shouldBe 9
         ServiceLoaderMyTestListenerObject.afterCounter.get() shouldBe 3
      }
      AutoScanMyTestListener.beforeCounter.get() shouldBe 9
      AutoScanMyTestListener.afterCounter.get() shouldBe 6
      ServiceLoaderMyTestListenerObject.beforeCounter.get() shouldBe 9
      ServiceLoaderMyTestListenerObject.afterCounter.get() shouldBe 6
   }
})

@AutoScan
object AutoScanMyTestListener : MyTestListener()

object ServiceLoaderMyTestListenerObject : MyTestListener()

class ServiceLoaderMyTestListener : TestListener by ServiceLoaderMyTestListenerObject

abstract class MyTestListener : TestListener {

   val beforeCounter = AtomicInteger(0)
   val afterCounter = AtomicInteger(0)

   override suspend fun beforeTest(testCase: TestCase) {
      maybeIncrementBeforeCounter(testCase)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      maybeIncrementAfterCounter(testCase)
   }

   override suspend fun beforeContainer(testCase: TestCase) {
      maybeIncrementBeforeCounter(testCase)
   }

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      maybeIncrementAfterCounter(testCase)
   }

   override suspend fun beforeEach(testCase: TestCase) {
      maybeIncrementBeforeCounter(testCase)
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      maybeIncrementAfterCounter(testCase)
   }

   override suspend fun beforeAny(testCase: TestCase) {
      maybeIncrementBeforeCounter(testCase)
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      maybeIncrementAfterCounter(testCase)
   }

   private fun maybeIncrementBeforeCounter(testCase: TestCase) {
      if (testCase.spec::class.java.name == AutoScanTestListenerTest::class.java.name)
         beforeCounter.incrementAndGet()
   }

   private fun maybeIncrementAfterCounter(testCase: TestCase) {
      if (testCase.spec::class.java.name == AutoScanTestListenerTest::class.java.name)
         afterCounter.incrementAndGet()
   }
}
