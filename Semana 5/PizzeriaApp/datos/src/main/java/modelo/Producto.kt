package modelo

import java.math.BigDecimal
import java.util.Locale
import utils.formatearPesos

/** La identidad se define por nombre y categoría; el precio y la receta no son identidad. */
class Producto(
    val nombre: String,
    val precio: BigDecimal,
    ingredientes: Map<String, Int> = emptyMap(),
    val categoria: CategoriaProducto = CategoriaProducto.PIZZA
) {
    init {
        require(nombre.isNotBlank()) { "El producto debe tener nombre" }
        require(precio.signum() >= 0) { "El precio no puede ser negativo" }
        require(ingredientes.values.all { it > 0 }) { "Todas las porciones deben ser positivas" }
    }

    val ingredientes: Map<String, Int> = ingredientes.toMap()

    private fun identidadNombre(): String = nombre.trim().lowercase(Locale.ROOT)

    override fun equals(other: Any?): Boolean = this === other ||
        (other is Producto && identidadNombre() == other.identidadNombre() && categoria == other.categoria)

    override fun hashCode(): Int = 31 * identidadNombre().hashCode() + categoria.hashCode()

    override fun toString(): String = "$nombre (${categoria.name})"

    fun detallarProducto(): String {
        val descripcion = if (ingredientes.isEmpty()) "Sin ingredientes configurados"
        else ingredientes.entries.joinToString(", ") { (ingrediente, cantidad) -> "$ingrediente ($cantidad)" }
        return "$nombre - ${formatearPesos(precio)} - Categoría: $categoria - Ingredientes: $descripcion"
    }
}
