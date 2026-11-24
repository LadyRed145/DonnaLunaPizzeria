package utils

import org.junit.Assert.*
import org.junit.Test

class ValidacionesTest {
    @Test fun correosValidosEInvalidos() {
        assertTrue(esCorreoValido("cliente@ejemplo.cl"))
        assertFalse(esCorreoValido("cliente@-ejemplo.cl"))
        assertFalse(esCorreoValido("cliente@ejemplo.123"))
    }

    @Test fun cantidadSeLimitaDeUnoACien() {
        assertFalse(esCantidadValida(0))
        assertTrue(esCantidadValida(1))
        assertTrue(esCantidadValida(100))
        assertFalse(esCantidadValida(101))
    }

    @Test fun fechasInvalidasSonRechazadas() {
        assertNotNull(parsearFecha("2026-11-15"))
        assertNull(parsearFecha("2026-02-30"))
    }
}
