package servicios

import anotaciones.Promocionable
import java.time.LocalDate
import modelo.Producto
import modelo.PromocionPedido
import utils.Constantes

/** Reflexión Java sobre getters de Kotlin anotados con @get:Promocionable. */
object PromocionesService {
    val inicio: LocalDate = LocalDate.parse(Constantes.FECHA_INICIO_PROMOCION)
    val fin: LocalDate = LocalDate.parse(Constantes.FECHA_FIN_PROMOCION)

    fun fechaEnPromocion(fecha: LocalDate): Boolean = fecha in inicio..fin

    fun detectarProductosAnotados(): Set<Producto> {
        val fuentes = listOf(CatalogoPizzas to CatalogoPizzas.javaClass, SeleccionEspecial() to SeleccionEspecial::class.java)
        val encontrados = linkedSetOf<Producto>()
        for ((instancia, tipo) in fuentes) {
            for (metodo in tipo.methods) {
                if (metodo.getAnnotation(Promocionable::class.java) == null || metodo.parameterCount != 0) continue
                val producto = metodo.invoke(instancia) as? Producto ?: continue
                // Un valor anotado solo es válido si pertenece realmente al catálogo oficial.
                val oficial = CatalogoPizzas.menu.find { it == producto } ?: continue
                require(oficial.precio == producto.precio && oficial.ingredientes == producto.ingredientes)
                encontrados.add(oficial)
            }
        }
        return encontrados
    }

    /** El descuento solo puede activarse con una fecha válida dentro del rango. */
    fun obtenerPromocion(fecha: LocalDate): PromocionPedido? =
        if (fechaEnPromocion(fecha)) PromocionPedido(fecha, Constantes.PORCENTAJE_PROMOCION, detectarProductosAnotados())
        else null
}
