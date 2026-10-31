package modelo

import utils.formatearPesos

/** Los ingredientes se asignarán en la fase de inventario por ingrediente. */
data class Producto(
    val nombre: String,
    val precio: Double,
    val ingredientes: Map<String, Int> = emptyMap()
) {
    fun detallarProducto(): String {
        val descripcionIngredientes = if (ingredientes.isEmpty()) {
            "Pendientes de configurar"
        } else {
            ingredientes.entries.joinToString(", ") { (nombre, cantidad) ->
                "$nombre ($cantidad)"
            }
        }
        return "$nombre - ${formatearPesos(precio)} - Ingredientes: $descripcionIngredientes"
    }
}
