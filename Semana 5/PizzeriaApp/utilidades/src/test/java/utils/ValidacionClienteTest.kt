package utils

import org.junit.Assert.*
import org.junit.Test

class ValidacionClienteTest {
    @Test fun formularioVacioBloqueaElAvance() {
        val errores = revisarDatosCliente("", "", "", "")
        assertFalse(errores.esValido)
        assertNotNull(errores.nombre)
        assertNotNull(errores.direccion)
        assertNull(errores.telefono) // Opcional
        assertNull(errores.correo) // Opcional
    }

    @Test fun espaciosEnBlancoNoSeAceptanComoNombreNiDireccion() {
        assertFalse(revisarDatosCliente("  ", "   ", "", "").esValido)
    }

    @Test fun nombreConNumerosSeRechaza() {
        assertNotNull(revisarDatosCliente("123456", "Calle Real 12", "", "").nombre)
        assertNotNull(revisarDatosCliente("Ana123", "Calle Real 12", "", "").nombre)
    }

    @Test fun nombreConTildesYApostrofesEsValido() {
        assertTrue(revisarDatosCliente("  María José O'Connor ", "Calle del Bosque 12", "", "").esValido)
    }

    @Test fun direccionDemasiadoCortaYCorreoEnLugarDeDireccionSeRechazan() {
        assertNotNull(revisarDatosCliente("Ana", "A2", "", "").direccion)
        assertNotNull(revisarDatosCliente("Ana", "ana@ejemplo.cl", "", "").direccion)
    }

    @Test fun datosValidosConOpcionalesVaciosPermitenContinuar() {
        assertTrue(revisarDatosCliente("Ana", "Av. Siempre Viva 123", "", "").esValido)
    }

    @Test fun telefonoMalFormadoBloqueaElAvance() {
        assertNotNull(revisarDatosCliente("Ana", "Calle 123", "123", "").telefono)
        assertTrue(revisarDatosCliente("Ana", "Calle 123", "987654321", "").esValido)
    }

    @Test fun correoMalFormadoBloqueaElAvance() {
        assertNotNull(revisarDatosCliente("Ana", "Calle 123", "", "ana@-ejemplo.cl").correo)
        assertTrue(revisarDatosCliente("Ana", "Calle 123", "", "ana@ejemplo.cl").esValido)
    }

    @Test fun erroresSeRecalculanTrasCorregirLosDatos() {
        val primero = revisarDatosCliente("", "Calle 123", "", "")
        val despues = revisarDatosCliente("Ana", "Calle 123", "", "")
        assertNotNull(primero.primerError())
        assertNull(despues.primerError())
    }
}
