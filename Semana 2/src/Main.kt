import modelo.Pedido
import servicios.CatalogoPizzas
import servicios.InventarioService
import servicios.JornadaService
import servicios.RepartidorService
import ui.deseaRegistrarOtroPedido
import ui.gestionarEstados
import ui.imprimirResumen
import ui.registrarCliente
import ui.registrarSelecciones
import ui.solicitarZonaEntrega
import ui.solicitarNotasPedido

/** Coordina pedidos múltiples sin cambiar el catálogo ni el inventario anterior. */
fun main() {
    val menuPizzas = CatalogoPizzas.menu
    val inventario = InventarioService(stockInicial = 20)
    val repartidores = RepartidorService()
    val jornada = JornadaService()

    do {
        val cliente = registrarCliente()
        val cantidades = registrarSelecciones(menuPizzas)

        if (cantidades.isEmpty()) {
            println("\nNo se registraron pizzas.")
            println("Pedido finalizado sin productos.")
        } else {
            val notas = solicitarNotasPedido()
            val pedido = Pedido(
                id = 1001 + jornada.cantidadRegistrada(),
                cliente = cliente,
                cantidades = cantidades,
                notas = notas
            )
            val evaluacion = inventario.confirmarPedido(pedido.cantidadesPorProducto)
            val mensajeInventario = if (evaluacion.confirmado) {
                pedido.cambiarEstado("En preparación")
                val zona = solicitarZonaEntrega()
                pedido.asignarRepartidor(repartidores.asignarMasCercano(zona))
                "Pedido confirmado. Ingredientes suficientes."
            } else {
                pedido.cambiarEstado("Rechazado")
                pedido.asignarRepartidor(null)
                "Pedido no completado. Ingredientes insuficientes."
            }

            jornada.registrar(pedido)
            imprimirResumen(pedido, inventario, mensajeInventario, evaluacion)
        }
    } while (deseaRegistrarOtroPedido())

    jornada.imprimirReporte()
    // Solo repite el reporte si se actualizaron estados de pedidos.
    if (gestionarEstados(jornada)) jornada.imprimirReporte()
}
