package servicios

import modelo.Producto

/** Cantidades requeridas, faltantes y disponibles expresadas en porciones. */
data class ResultadoInventario(
    val confirmado: Boolean,
    val requeridos: Map<String, Long>,
    val faltantes: Map<String, Long>,
    val disponiblesAlEvaluar: Map<String, Long>
)

/**
 * Inventario de ingredientes en memoria (sin persistencia).
 * Se inicia con [stockInicial] porciones de cada ingrediente del catálogo.
 */
class InventarioService(
    val stockInicial: Int,
    catalogo: List<Producto> = CatalogoPizzas.menu
) {
    init {
        require(stockInicial >= 0) { "El stock inicial no puede ser negativo" }
    }

    val stockInicialPorIngrediente: Map<String, Long>
    private val existencias: MutableMap<String, Long>

    init {
        val ingredientes = catalogo.flatMap { it.ingredientes.keys }.distinct()
        stockInicialPorIngrediente = ingredientes.associateWith { stockInicial.toLong() }
        existencias = stockInicialPorIngrediente.toMutableMap()
    }

    /** Snapshot: ningún consumidor externo puede alterar el stock interno. */
    fun consultarExistencias(): Map<String, Long> = existencias.toMap()

    /** Calcula ingredientes para todas las variedades y cantidades del pedido. */
    fun calcularIngredientesNecesarios(cantidades: Map<Producto, Int>): Map<String, Long> {
        val requeridos = linkedMapOf<String, Long>()
        for ((producto, cantidadPizzas) in cantidades) {
            require(cantidadPizzas > 0) { "La cantidad de pizzas debe ser mayor que cero" }
            require(producto.ingredientes.isNotEmpty()) {
                "La pizza ${producto.nombre} no tiene una receta configurada"
            }
            for ((ingrediente, porcionesPorPizza) in producto.ingredientes) {
                require(porcionesPorPizza > 0) {
                    "Las porciones del ingrediente $ingrediente deben ser positivas"
                }
                val cantidadNecesaria = porcionesPorPizza.toLong() * cantidadPizzas.toLong()
                requeridos[ingrediente] = (requeridos[ingrediente] ?: 0L) + cantidadNecesaria
            }
        }
        return requeridos
    }

    /**
     * Comprueba TODO el lote antes de descontar una sola porción.
     * Un pedido con faltantes se rechaza sin modificar el inventario.
     */
    fun confirmarPedido(cantidades: Map<Producto, Int>): ResultadoInventario {
        val requeridos = calcularIngredientesNecesarios(cantidades)
        val disponibles = consultarExistencias()
        val faltantes = linkedMapOf<String, Long>()

        for ((ingrediente, necesarios) in requeridos) {
            val disponiblesIngrediente = disponibles[ingrediente]
            when {
                disponiblesIngrediente == null -> faltantes[ingrediente] = necesarios
                disponiblesIngrediente < necesarios ->
                    faltantes[ingrediente] = necesarios - disponiblesIngrediente
                else -> Unit // Ingrediente disponible: no requiere acción.
            }
        }

        if (faltantes.isNotEmpty()) {
            return ResultadoInventario(false, requeridos, faltantes, disponibles)
        }

        for ((ingrediente, necesarios) in requeridos) {
            existencias[ingrediente] = requireNotNull(existencias[ingrediente]) - necesarios
        }
        return ResultadoInventario(true, requeridos, emptyMap(), disponibles)
    }
}
