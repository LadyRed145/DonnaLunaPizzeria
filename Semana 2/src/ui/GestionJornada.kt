package ui

import modelo.Pedido
import servicios.JornadaService

/** La zona es una aproximación didáctica, no una distancia real. */
fun solicitarZonaEntrega(): String {
    println("\nZona de reparto demostrativa: Centro / Norte / Costa")
    print("Indique zona (Enter = Centro): ")
    val entrada = readlnOrNull()?.trim().orEmpty()
    return when (entrada.lowercase()) {
        "", "centro" -> "Centro"
        "norte" -> "Norte"
        "costa" -> "Costa"
        else -> {
            println("Zona desconocida. Se utilizará Centro para esta demostración.")
            "Centro"
        }
    }
}

/** Campo opcional: Enter registra un valor nulo y el resumen mostrará «Sin notas». */
fun solicitarNotasPedido(): String? {
    print("\nNotas del pedido (Enter = Sin notas): ")
    return readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }
}

fun deseaRegistrarOtroPedido(): Boolean {
    print("\n¿Registrar otro pedido? (s/N): ")
    return readlnOrNull()?.trim()?.equals("s", ignoreCase = true) == true
}

/** Avanza los estados sin permitir despachar pedidos rechazados. */
fun gestionarEstados(jornada: JornadaService): Boolean {
    if (jornada.cantidadRegistrada() == 0) return false
    var huboCambios = false

    println("\n========= GESTIÓN DE ESTADOS =========")
    println("Ingrese un ID para avanzar el pedido o 0 para terminar.")
    println("En preparación -> Listo -> En camino -> Entregado")

    while (true) {
        print("ID de pedido (0 = salir): ")
        val id = readlnOrNull()?.trim()?.toIntOrNull() ?: break
        if (id == 0) break

        val pedido: Pedido? = jornada.buscar(id)
        if (pedido == null) {
            println("No existe un pedido con ID $id.")
            continue
        }

        when (pedido.estado) {
            "En preparación" -> pedido.cambiarEstado("Listo")
            "Listo" -> {
                val repartidor = pedido.repartidor
                if (repartidor == null) {
                    println("No se puede despachar sin repartidor asignado.")
                    continue
                }
                repartidor.entregarPedido(pedido)
            }
            "En camino" -> pedido.cambiarEstado("Entregado")
            "Entregado" -> {
                println("El pedido #$id ya está entregado.")
                continue
            }
            "Rechazado" -> {
                println("El pedido #$id fue rechazado; no se puede despachar.")
                continue
            }
            else -> {
                println("Estado no reconocido; no se realizó ningún cambio.")
                continue
            }
        }
        println("Pedido #$id actualizado a: ${pedido.estado}")
        huboCambios = true
    }
    return huboCambios
}
