package servicios

import modelo.Repartidor

/** Prioridad de zonas vecinas inventada para la simulación (NO distancias GPS). */
class RepartidorService {
    val disponibles: List<Repartidor> = listOf(
        Repartidor("Carlos", zona = "Centro"),
        Repartidor("Paula", zona = "Norte"),
        Repartidor("Diego", zona = "Costa")
    )

    private val prioridades: Map<String, List<String>> = mapOf(
        "Centro" to listOf("Centro", "Norte", "Costa"),
        "Norte" to listOf("Norte", "Centro", "Costa"),
        "Costa" to listOf("Costa", "Centro", "Norte")
    )

    fun asignarMasCercano(zonaPedido: String): Repartidor? {
        val candidatos = prioridades.entries.firstOrNull { it.key.equals(zonaPedido, ignoreCase = true) }
            ?.value ?: prioridades.getValue("Centro")
        return candidatos.firstNotNullOfOrNull { zona ->
            disponibles.firstOrNull { it.disponible && it.zona == zona }
        }
    }
}
