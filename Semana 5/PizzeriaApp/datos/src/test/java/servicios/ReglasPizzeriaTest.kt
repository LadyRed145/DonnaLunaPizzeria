package servicios

import java.math.BigDecimal
import java.time.LocalDate
import modelo.Cliente
import modelo.Pedido
import modelo.EstadoPedido
import org.junit.Assert.*
import org.junit.Test

class ReglasPizzeriaTest {
    private val cliente = Cliente("Camila", "Calle Central 123", "987654321", "camila@ejemplo.cl")

    @Test fun pedidoConUnaPizzaNoTieneDescuentoPorVolumen() {
        val pedido = Pedido(1001, cliente, mapOf(CatalogoPizzas.italiana to 1))
        assertEquals(0, pedido.porcentajeDescuento())
        assertEquals(0, BigDecimal("8500").compareTo(pedido.calcularTotal()))
    }

    @Test fun pedidoConCuatroPizzasAplicaDescuentoDiezPorCiento() {
        val pedido = Pedido(1002, cliente, mapOf(CatalogoPizzas.margarita to 4))
        assertEquals(10, pedido.porcentajeDescuento())
        assertEquals(0, BigDecimal("27000").compareTo(pedido.calcularTotal()))
    }

    @Test fun promocionSoloDentroDelPeriodoYSobreProductosElegibles() {
        assertNull(PromocionesService.obtenerPromocion(LocalDate.of(2026, 12, 1)))
        val promocion = requireNotNull(PromocionesService.obtenerPromocion(LocalDate.of(2026, 11, 15)))
        assertTrue(CatalogoPizzas.margarita in promocion.productosElegibles)
        assertFalse(CatalogoPizzas.italiana in promocion.productosElegibles)
        val pedido = Pedido(1003, cliente, mapOf(CatalogoPizzas.margarita to 1), promocion = promocion)
        assertEquals(0, BigDecimal("7125").compareTo(pedido.calcularTotal()))
    }

    @Test fun rechazoPorStockNoDescuentaIngredientes() {
        val inventario = InventarioService(1)
        val antes = inventario.consultarExistencias()
        val resultado = inventario.confirmarPedido(mapOf(CatalogoPizzas.italiana to 2))
        assertFalse(resultado.confirmado)
        assertEquals(antes, inventario.consultarExistencias())
    }

    @Test fun confirmacionPorStockDescuentaExactamenteIngredientes() {
        val inventario = InventarioService(20)
        val antes = inventario.consultarExistencias()
        val resultado = inventario.confirmarPedido(mapOf(CatalogoPizzas.italiana to 1))
        assertTrue(resultado.confirmado)
        val despues = inventario.consultarExistencias()
        for ((ingrediente, unidades) in CatalogoPizzas.italiana.ingredientes) {
            assertEquals((antes.getValue(ingrediente) - unidades.toLong()), despues.getValue(ingrediente))
        }
    }

    @Test fun registroYJornadaAcumulanClientesYPedidos() {
        val registro = RegistroClientesService()
        val jornada = JornadaService()
        assertNull(registro.ultimoClienteRegistrado())
        assertTrue(registro.registrar(cliente))
        assertFalse(registro.registrar(cliente))
        assertEquals(1, registro.cantidadRegistrada())
        assertEquals("Camila", registro.ultimoClienteRegistrado()?.nombre)
        jornada.registrar(Pedido(1004, cliente, mapOf(CatalogoPizzas.italiana to 1)))
        assertEquals(1, jornada.cantidadRegistrada())
    }
}
