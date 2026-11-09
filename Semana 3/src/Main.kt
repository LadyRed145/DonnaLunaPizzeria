import modelo.EstadoPedido
import modelo.Pedido
import servicios.CatalogoPizzas
import servicios.InventarioService
import servicios.JornadaService
import servicios.RegistroClientesService
import servicios.RepartidorService
import ui.deseaRegistrarOtroPedido
import ui.gestionarEstados
import ui.imprimirDemostracionSemana3
import ui.imprimirInventarioYAvisos
import ui.imprimirResumen
import ui.registrarCliente
import ui.registrarSelecciones
import ui.solicitarZonaEntrega
import ui.solicitarNotasPedido
import ui.solicitarPromocion
import utils.Constantes

/** Coordina registro e inventario; la demo S3 no vende ni consume stock. */
fun main() {
    val menuPizzas = CatalogoPizzas.menu
    val inventario = InventarioService(stockInicial = Constantes.STOCK_INICIAL_INGREDIENTE)
    val repartidores = RepartidorService()
    val registroClientes = RegistroClientesService()
    val jornada = JornadaService()

    do {
        val cliente = registrarCliente(registroClientes) ?: break
        val cantidades = registrarSelecciones(menuPizzas) ?: break
        if (cantidades.isEmpty()) {
            println("\nNo se registraron pizzas.")
            println("Pedido finalizado sin productos.")
        } else {
            val notas = solicitarNotasPedido()
            val promocion = solicitarPromocion()
            val pedido = Pedido(
                id = Constantes.ID_INICIAL_PEDIDOS + jornada.cantidadRegistrada(),
                cliente = cliente, cantidades = cantidades, notas = notas, promocion = promocion
            )
            val evaluacion = inventario.confirmarPedido(pedido.cantidadesPorProducto)
            var zona: String? = null
            val mensajeInventario = if (evaluacion.confirmado) {
                zona = solicitarZonaEntrega()
                val repartidor = repartidores.asignarMasCercano(zona)
                if (repartidor != null) pedido.asignarRepartidor(repartidor)
                else println("Aviso: todos los repartidores están ocupados; el pedido espera asignación.")
                "Pedido confirmado. Ingredientes suficientes."
            } else {
                pedido.cambiarEstado(EstadoPedido.RECHAZADO)
                "Pedido no completado. Ingredientes insuficientes."
            }
            jornada.registrar(pedido, zona)
            imprimirResumen(pedido)
            imprimirInventarioYAvisos(pedido, inventario, mensajeInventario, evaluacion)
        }
    } while (deseaRegistrarOtroPedido())

    jornada.imprimirReporte()
    if (gestionarEstados(jornada, repartidores)) jornada.imprimirReporte()
    imprimirDemostracionSemana3()
}
