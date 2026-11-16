package servicios

import modelo.EstadoPedido
import modelo.Pedido
import modelo.Usuario
import utils.formatearPesos

class JornadaService {
    private var pedidosDelDia: Array<Pedido> = emptyArray()
    private val zonas = linkedMapOf<Int, String>()

    fun registrar(pedido: Pedido, zona: String? = null) {
        require(pedidosDelDia.none { it.id == pedido.id }) { "ID duplicado #${pedido.id}" }
        pedidosDelDia += pedido
        if (zona != null) zonas[pedido.id] = zona
    }

    fun buscar(id: Int): Pedido? = pedidosDelDia.find { it.id == id }
    fun zonaDePedido(id: Int): String? = zonas[id]
    fun cantidadRegistrada(): Int = pedidosDelDia.size
    fun pendientes(): List<Pedido> = pedidosDelDia.filter {
        it.estado in setOf(EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO, EstadoPedido.EN_CAMINO)
    }
    fun completados(): List<Pedido> = pedidosDelDia.filter { it.estado == EstadoPedido.ENTREGADO }
    fun rechazados(): List<Pedido> = pedidosDelDia.filter { it.estado == EstadoPedido.RECHAZADO }

    fun imprimirReporte() {
        println("\n========= REPORTE DE PEDIDOS DEL DÍA =========")
        if (pedidosDelDia.isEmpty()) { println("No se registraron pedidos."); return }
        for (pedido in pedidosDelDia) {
            val pizzas = pedido.cantidadesPorProducto.entries.joinToString(", ") { "${it.key.nombre} x${it.value}" }
            val etiqueta = if (pedido.estado == EstadoPedido.RECHAZADO) "Total referencial (venta no confirmada)" else "Total"
            println("Pedido #${pedido.id} | Cliente: ${pedido.cliente.nombre}")
            println("Pizzas: $pizzas")
            println("$etiqueta: ${formatearPesos(pedido.calcularTotal())} | Estado: ${pedido.estado}")
            println("Repartidor: ${pedido.repartidor?.nombre ?: "Sin asignar"}")
            // Llamada polimórfica desde referencias al tipo base Usuario.
            val roles: List<Usuario> = listOfNotNull(pedido.cliente, pedido.repartidor)
            println("Roles: ${roles.joinToString { "${it.describirRol()}: ${it.nombre}" }}")
            println("Productos: ${pedido.productos.joinToString { it.nombre }}")
            println("-----------------------------------------------")
        }
        println("Completados (Entregado): ${completados().size}")
        println("Pendientes (En preparación, Listo, En camino): ${pendientes().size}")
        println("Rechazados: ${rechazados().size}")
    }
}
