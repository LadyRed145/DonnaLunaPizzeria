package servicios

import modelo.Repartidor

/**
 * Personal y zonas DE DEMOSTRACIÓN. La cercanía se aproxima por coincidencia
 * de zona; no se miden distancias ni se realiza geolocalización.
 */
class RepartidorService {
    val disponibles: List<Repartidor> = listOf(
        Repartidor(nombre = "Carlos", zona = "Centro"),
        Repartidor(nombre = "Paula", zona = "Norte"),
        Repartidor(nombre = "Diego", zona = "Costa")
    )

    fun asignarMasCercano(zonaPedido: String): Repartidor? =
        disponibles.find { repartidor ->
            repartidor.disponible && repartidor.zona.equals(zonaPedido, ignoreCase = true)
        }
}
