package modelo

class Repartidor(
    override val nombre: String,
    override val contacto: String = "No informado",
    val zona: String = "Centro"
) : Usuario(nombre, contacto) {
    private var pedidoAsignado: Int? = null
    val disponible: Boolean get() = pedidoAsignado == null

    override fun describirRol(): String = "Repartidor"

    internal fun reservarParaPedido(id: Int) {
        require(disponible) { "$nombre está ocupado con otro pedido" }
        pedidoAsignado = id
    }

    internal fun liberarDePedido(id: Int) {
        require(pedidoAsignado == id) { "$nombre no estaba asignado al pedido #$id" }
        pedidoAsignado = null
    }

    fun entregarPedido(pedido: Pedido) {
        require(pedido.estado == EstadoPedido.LISTO) { "Solo se despacha un pedido Listo" }
        require(pedido.repartidor === this) { "Solo el repartidor asignado puede despachar este pedido" }
        require(!disponible) { "El repartidor debe estar reservado" }
        pedido.cambiarEstado(EstadoPedido.EN_CAMINO)
    }
}
