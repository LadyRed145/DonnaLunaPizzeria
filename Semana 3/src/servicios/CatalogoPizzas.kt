package servicios

import anotaciones.Promocionable
import java.math.BigDecimal
import modelo.CategoriaProducto
import modelo.Producto

/** Catálogo original: exactamente cinco pizzas, sus nombres y sus precios. */
object CatalogoPizzas {
    private val ingredientesBase = linkedMapOf("Masa" to 1, "Salsa de tomate" to 1, "Queso mozzarella" to 1)
    private fun receta(vararg adicionales: Pair<String, Int>) = ingredientesBase + adicionales.toMap()

    val italiana = Producto("Italiana", BigDecimal("8500"), receta("Jamón" to 1, "Tomate" to 1))

    @get:Promocionable("Margarita destacada en noviembre")
    val margarita = Producto("Margarita", BigDecimal("7500"), receta("Albahaca" to 1))

    @get:Promocionable("Pepperoni destacada en noviembre")
    val pepperoni = Producto("Pepperoni", BigDecimal("9500"), receta("Pepperoni" to 1))

    val cuatroQuesos = Producto("Cuatro Quesos", BigDecimal("10500"),
        receta("Queso cheddar" to 1, "Queso azul" to 1, "Queso parmesano" to 1))
    val hawaiana = Producto("Hawaiana", BigDecimal("9000"), receta("Jamón" to 1, "Piña" to 1))

    val menu: List<Producto> = listOf(italiana, margarita, pepperoni, cuatroQuesos, hawaiana)

    init {
        require(menu.distinct().size == menu.size) { "Catálogo con productos duplicados" }
        require(menu.all { it.categoria == CategoriaProducto.PIZZA })
    }
}

/** Segunda clase con miembros anotados: la misma pizza del catálogo, sin precio nuevo. */
class SeleccionEspecial {
    @get:Promocionable("Selección de la casa: Cuatro Quesos")
    val pizzaDestacada: Producto get() = CatalogoPizzas.cuatroQuesos
}
