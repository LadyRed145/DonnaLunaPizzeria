package servicios

import modelo.Pedido
import utils.formatearPesos

/** Arreglo de pedidos del día; datos en memoria durante una ejecución. */
class JornadaService {
    private var pedidosDelDia: Array<Pedido> = emptyArray()

    fun registrar(pedido: Pedido) {
        require(pedidosDelDia.none { it.id == pedido.id }) {
            "No se puede registrar dos veces el ID ${pedido.id}"
        }
        pedidosDelDia += pedido
    }

    fun buscar(id: Int): Pedido? = pedidosDelDia.find { it.id == id }

    fun cantidadRegistrada(): Int = pedidosDelDia.size

    /** Un pedido despachado todavía está pendiente de entrega. */
    fun pendientes(): List<Pedido> = pedidosDelDia.filter {
        it.estado in setOf("En preparación", "Listo", "En camino")
    }

    fun completados(): List<Pedido> = pedidosDelDia.filter { it.estado == "Entregado" }

    fun rechazados(): List<Pedido> = pedidosDelDia.filter { it.estado == "Rechazado" }

    fun imprimirReporte() {
        println("\n========= REPORTE DE PEDIDOS DEL DÍA =========")
        if (pedidosDelDia.isEmpty()) {
            println("No se registraron pedidos.")
            return
        }

        // El recorrido for y el Array cumplen la gestión pedida en S2.
        for (pedido in pedidosDelDia) {
            val pizzas = pedido.cantidadesPorProducto.entries.joinToString(", ") {
                "${it.key.nombre} x${it.value}"
            }
            println("Pedido #${pedido.id} | Cliente: ${pedido.cliente.nombre}")
            println("Pizzas: $pizzas")
            val tipoTotal = if (pedido.estado == "Rechazado") {
                "Total referencial (venta no confirmada)"
            } else {
                "Total"
            }
            println("$tipoTotal: ${formatearPesos(pedido.calcularTotal())} | Estado: ${pedido.estado}")
            println("Repartidor: ${pedido.repartidor?.nombre ?: "Sin asignar"}")
            println("-----------------------------------------------")
        }

        val completados = completados()
        val pendientes = pendientes()
        val rechazados = rechazados()
        println("Completados (Entregado): ${completados.size}")
        println("Pendientes (En preparación, Listo, En camino): ${pendientes.size}")
        println("Rechazados: ${rechazados.size}")
    }
}
