package com.volumelock

import app.cash.turbine.test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Verifica que el toolchain de test (JUnit5 + Coroutines-test + Turbine) realmente
 * ejecuta, no solo que resuelve dependencias. Se puede borrar cuando existan tests reales.
 */
class ToolchainSmokeTest {

    @Test
    fun `junit5 corre`() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun `turbine y coroutines-test corren`() = runTest {
        flowOf(1, 2, 3).test {
            assertEquals(1, awaitItem())
            assertEquals(2, awaitItem())
            assertEquals(3, awaitItem())
            awaitComplete()
        }
    }
}
