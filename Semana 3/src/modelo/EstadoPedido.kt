package modelo

/** Estados válidos del ciclo de vida de un pedido. */
enum class EstadoPedido(val descripcion: String) {
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado"),
    RECHAZADO("Rechazado");

    /** El estado nunca retrocede y Entregado/Rechazado son terminales. */
    fun permiteTransicionA(siguiente: EstadoPedido): Boolean = when (this) {
        EN_PREPARACION -> siguiente == LISTO || siguiente == RECHAZADO
        LISTO -> siguiente == EN_CAMINO
        EN_CAMINO -> siguiente == ENTREGADO
        ENTREGADO, RECHAZADO -> false
    }

    override fun toString(): String = descripcion
}
