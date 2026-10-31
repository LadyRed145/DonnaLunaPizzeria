package modelo

/** Un producto aparece una sola vez; el mapa registra sus cantidades. */
class Pedido(
    val id: Int,
    val cliente: Cliente,
    cantidades: Map<Producto, Int>,
    estadoInicial: String = "En preparación",
    val notas: String? = null
) {
    val cantidadesPorProducto: Map<Producto, Int> = cantidades.toMap()
    val productos: List<Producto> get() = cantidadesPorProducto.keys.toList()

    var estado: String = estadoInicial
        private set

    var repartidor: Repartidor? = null
        private set

    val cantidadPizzas: Long get() = cantidadesPorProducto.values.sumOf { it.toLong() }

    fun calcularSubtotal(): Double = cantidadesPorProducto.entries.sumOf { (producto, cantidad) ->
        producto.precio * cantidad
    }

    fun porcentajeDescuento(): Int = if (cantidadPizzas > 3) 10 else 0

    fun calcularDescuento(): Double = calcularSubtotal() * porcentajeDescuento() / 100

    fun calcularTotal(): Double = calcularSubtotal() - calcularDescuento()

    fun cambiarEstado(nuevoEstado: String) {
        estado = nuevoEstado
    }

    fun asignarRepartidor(nuevoRepartidor: Repartidor?) {
        repartidor = nuevoRepartidor
    }
}
