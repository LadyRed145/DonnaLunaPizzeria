package servicios

import modelo.Producto

/**
 * Catálogo de S1: conserva exactamente nombres y precios.
 * Las recetas son cantidades DEMOSTRATIVAS en porciones por pizza,
 * no medidas reales ni una ficha técnica gastronómica.
 */
object CatalogoPizzas {
    private val ingredientesBase = linkedMapOf(
        "Masa" to 1,
        "Salsa de tomate" to 1,
        "Queso mozzarella" to 1
    )

    private fun receta(vararg adicionales: Pair<String, Int>): Map<String, Int> =
        ingredientesBase + adicionales.toMap()

    val menu: List<Producto> = listOf(
        Producto("Italiana", 8500.0, receta("Jamón" to 1, "Tomate" to 1)),
        Producto("Margarita", 7500.0, receta("Albahaca" to 1)),
        Producto("Pepperoni", 9500.0, receta("Pepperoni" to 1)),
        Producto(
            "Cuatro Quesos", 10500.0,
            receta("Queso cheddar" to 1, "Queso azul" to 1, "Queso parmesano" to 1)
        ),
        Producto("Hawaiana", 9000.0, receta("Jamón" to 1, "Piña" to 1))
    )
}
