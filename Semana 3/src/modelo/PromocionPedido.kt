package modelo

import java.time.LocalDate

/** Datos inmutables de una promoción validada externamente. */
data class PromocionPedido(
    val fecha: LocalDate,
    val porcentaje: Int,
    val productosElegibles: Set<Producto>
) {
    init { require(porcentaje in 1..100) }
}
