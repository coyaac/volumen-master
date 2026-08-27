package com.volumelock.service

import com.volumelock.service.VolumeForegroundService.Companion.shouldRevert
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class VolumeForegroundServiceTest {

    @Test
    fun `revierte cuando lock activo y el valor se desvia del objetivo`() {
        assertTrue(shouldRevert(lockActive = true, target = 7, newValue = 5))
    }

    @Test
    fun `no revierte si el candado esta inactivo`() {
        assertFalse(shouldRevert(lockActive = false, target = 7, newValue = 5))
    }

    @Test
    fun `no revierte si no hay objetivo configurado`() {
        assertFalse(shouldRevert(lockActive = true, target = null, newValue = 5))
    }

    @Test
    fun `no revierte si el valor ya coincide con el objetivo (evita bucle)`() {
        assertFalse(shouldRevert(lockActive = true, target = 7, newValue = 7))
    }
}
