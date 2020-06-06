package com.sksamuel.kotest.specs.shouldspec

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeLessThan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
class ShouldSpecTest : ShouldSpec() {

   init {
      context("a context") {
         should("a test") {
            1.shouldBeLessThan(2)
         }
         should("a test with config").config(enabled = true, timeout = 12321.milliseconds) {
            1.shouldBeLessThan(2)
         }
         context("a nested context") {
            should("a test") {
               1.shouldBeLessThan(2)
            }
         }
         should("a test without a parent context") {
            1.shouldBeLessThan(2)
         }
      }
      context("a context with delay in child coroutine") {
         launch { delay(1) }
         should("a test") {
            1.shouldBeLessThan(2)
         }
      }

   }
}
