package modelo

class Repartidor(
    override val nombre: String,
    override val contacto: String = "No informado",
    /** Zona ficticia para demostrar asignación por cercanía, sin GPS. */
    val zona: String = "Centro",
    val disponible: Boolean = true
) : Usuario(nombre, contacto) {
    override fun describirRol(): String = "Repartidor"

    /** Un pedido listo puede pasar a «En camino» cuando sale a reparto. */
    fun entregarPedido(pedido: Pedido) {
        require(pedido.estado == "Listo") {
            "Solo se puede despachar un pedido con estado Listo"
        }
        pedido.cambiarEstado("En camino")
    }
}
