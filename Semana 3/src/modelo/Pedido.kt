package modelo

import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import utils.Constantes
import utils.porcentajeDe

/** Pedido de cantidades inmutables: los importes se calculan una vez por instancia. */
class Pedido(
    val id: Int,
    val cliente: Cliente,
    cantidades: Map<Producto, Int>,
    estadoInicial: EstadoPedido = EstadoPedido.EN_PREPARACION,
    val notas: String? = null,
    val promocion: PromocionPedido? = null
) {
    companion object {
        private val secuenciaCombinados = AtomicInteger(Constantes.ID_INICIAL_COMBINADOS)
    }

    init {
        require(id > 0)
        require(estadoInicial == EstadoPedido.EN_PREPARACION) { "Un pedido nuevo comienza En preparación" }
        require(cantidades.isNotEmpty()) { "Se requiere al menos un producto" }
        require(cantidades.values.all { it in Constantes.CANTIDAD_MINIMA..Constantes.CANTIDAD_MAXIMA }) {
            "Cada cantidad debe estar entre 1 y 100"
        }
        require(cantidades.values.sumOf { it.toLong() } <= Constantes.CANTIDAD_MAXIMA) {
            "El pedido no puede superar 100 unidades"
        }
    }

    val cantidadesPorProducto: Map<Producto, Int> = cantidades.toMap()
    val productos: List<Producto> get() = cantidadesPorProducto.keys.toList()
    val cantidadPizzas: Long = cantidadesPorProducto.values.sumOf { it.toLong() }

    var estado: EstadoPedido = estadoInicial
        private set
    var repartidor: Repartidor? = null
        private set

    /** El cálculo es estable: los productos y cantidades no mutan después de crear el pedido. */
    private val importes: ImportesPedido = calcularImportes()

    data class ImportesPedido(
        val subtotal: BigDecimal,
        val descuentoVolumen: BigDecimal,
        val descuentoPromocion: BigDecimal,
        val total: BigDecimal
    ) {
        val descuentoTotal: BigDecimal get() = descuentoVolumen.add(descuentoPromocion)
    }

    private fun calcularImportes(): ImportesPedido {
        val subtotal = cantidadesPorProducto.entries.fold(BigDecimal.ZERO) { suma, (producto, cantidad) ->
            suma.add(producto.precio.multiply(BigDecimal.valueOf(cantidad.toLong())))
        }
        val descuentoVolumen = porcentajeDe(subtotal, porcentajeDescuento())
        // La promo del 5% se calcula sobre productos elegibles *después* del descuento de volumen.
        val importeElegible = cantidadesPorProducto.entries.fold(BigDecimal.ZERO) { suma, (producto, cantidad) ->
            if (promocion?.productosElegibles?.contains(producto) == true)
                suma.add(producto.precio.multiply(BigDecimal.valueOf(cantidad.toLong()))) else suma
        }
        val elegibleDespuesVolumen = importeElegible.subtract(porcentajeDe(importeElegible, porcentajeDescuento()))
        val descuentoPromocion = if (promocion == null) BigDecimal.ZERO
            else porcentajeDe(elegibleDespuesVolumen, promocion.porcentaje)
        val total = subtotal.subtract(descuentoVolumen).subtract(descuentoPromocion)
        require(total.signum() >= 0)
        return ImportesPedido(subtotal, descuentoVolumen, descuentoPromocion, total)
    }

    fun porcentajeDescuento(): Int = if (cantidadPizzas > Constantes.UMBRAL_DESCUENTO_VOLUMEN)
        Constantes.PORCENTAJE_DESCUENTO_VOLUMEN else 0

    fun detalleImportes(): ImportesPedido = importes
    fun calcularSubtotal(): BigDecimal = importes.subtotal
    fun calcularDescuento(): BigDecimal = importes.descuentoTotal
    fun calcularTotal(): BigDecimal = importes.total

    /** components explícitos: cliente, productos con cantidades y total. */
    operator fun component1(): Cliente = cliente
    operator fun component2(): Map<Producto, Int> = cantidadesPorProducto
    operator fun component3(): BigDecimal = calcularTotal()

    /** Combina dos pedidos compatibles SIN reservar stock ni alterar los pedidos originales. */
    operator fun plus(otro: Pedido): Pedido {
        require(this !== otro) { "No combine el mismo pedido consigo mismo" }
        require(cliente == otro.cliente) { "Solo pueden combinarse pedidos del mismo cliente" }
        require(estado == EstadoPedido.EN_PREPARACION && otro.estado == EstadoPedido.EN_PREPARACION) {
            "Solo se combinan pedidos En preparación"
        }
        require(repartidor == null && otro.repartidor == null) {
            "No se combinan pedidos que ya tienen repartidor"
        }
        require(promocion == otro.promocion) { "La promoción de ambos pedidos debe coincidir" }
        val unidos = linkedMapOf<Producto, Int>()
        for ((producto, cantidad) in cantidadesPorProducto.toList() + otro.cantidadesPorProducto.toList()) {
            // Igualdad ignora precio/receta: la combinación no debe ocultar inconsistencias.
            val existente = unidos.keys.find { it == producto }
            require(existente == null || (existente.precio == producto.precio && existente.ingredientes == producto.ingredientes)) {
                "Producto con mismo nombre/categoría pero precio o receta incompatible"
            }
            val acumulado = (unidos[producto] ?: 0) + cantidad
            require(acumulado in 1..Constantes.CANTIDAD_MAXIMA) { "Cantidad combinada fuera de rango" }
            unidos[producto] = acumulado
        }
        require(unidos.values.sum() <= Constantes.CANTIDAD_MAXIMA) { "Pedido combinado excede 100 unidades" }
        return Pedido(
            id = secuenciaCombinados.getAndIncrement(), cliente = cliente,
            cantidades = unidos, notas = "Pedido combinado (demostración)", promocion = promocion
        )
    }

    fun cambiarEstado(nuevoEstado: EstadoPedido) {
        require(estado.permiteTransicionA(nuevoEstado)) { "Transición no permitida: $estado -> $nuevoEstado" }
        if (nuevoEstado == EstadoPedido.EN_CAMINO) {
            val asignado = requireNotNull(repartidor) { "No se puede despachar sin repartidor" }
            require(asignado.disponible.not()) { "El repartidor debe estar reservado para el pedido" }
        }
        if (nuevoEstado == EstadoPedido.ENTREGADO) repartidor?.liberarDePedido(id)
        if (nuevoEstado == EstadoPedido.RECHAZADO) repartidor?.liberarDePedido(id)
        estado = nuevoEstado
    }

    fun asignarRepartidor(nuevoRepartidor: Repartidor?) {
        require(estado == EstadoPedido.EN_PREPARACION || estado == EstadoPedido.LISTO) {
            "No se puede asignar repartidor en estado $estado"
        }
        if (repartidor === nuevoRepartidor) return
        if (nuevoRepartidor != null) nuevoRepartidor.reservarParaPedido(id)
        repartidor?.liberarDePedido(id)
        repartidor = nuevoRepartidor
    }
}
