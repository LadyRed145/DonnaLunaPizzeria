package com.donnaluna.pizzeriaapp.ui

import org.junit.Assert.*
import org.junit.Test

/** Regresión: no se puede saltar la validación con botones ni con la barra inferior. */
class NavegacionClienteTest {
    @Test fun clienteVacioNoPuedeIrAPizzas() {
        val vm = PizzeriaViewModel()
        vm.navegarA(1)
        assertEquals(0, vm.pagina)
        assertTrue(vm.mostrarErroresCliente)
        assertNotNull(vm.erroresCliente.primerError())
    }

    @Test fun clienteInvalidoNoPuedeSaltarAEntrega() {
        val vm = PizzeriaViewModel()
        vm.nombre = "Ana"
        vm.direccion = "X"
        vm.navegarA(2)
        assertEquals(0, vm.pagina)
    }

    @Test fun clienteValidoPuedeAvanzarAPizzasPeroNoAEntregaSinSeleccion() {
        val vm = PizzeriaViewModel()
        vm.nombre = "Ana"
        vm.direccion = "Av. Siempre Viva 123"
        vm.navegarA(1)
        assertEquals(1, vm.pagina)
        vm.navegarA(2)
        assertEquals(1, vm.pagina)
        assertTrue(vm.mostrarErrorPizzas)
    }

    @Test fun noSePuedeAvanzarConCorreoErroneoAunqueLosObligatoriosSeanValidos() {
        val vm = PizzeriaViewModel()
        vm.nombre = "Ana"
        vm.direccion = "Calle del Mar 123"
        vm.correo = "ana@"
        vm.navegarA(1)
        assertEquals(0, vm.pagina)
        assertNotNull(vm.erroresCliente.correo)
    }

    @Test fun corregirCamposPermiteAvanzarSinRecrearViewModel() {
        val vm = PizzeriaViewModel()
        vm.navegarA(1)
        assertEquals(0, vm.pagina)
        vm.nombre = "Ana"
        vm.direccion = "Calle del Mar 123"
        vm.navegarA(1)
        assertEquals(1, vm.pagina)
    }
}
