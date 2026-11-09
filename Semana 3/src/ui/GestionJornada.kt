package ui

import modelo.EstadoPedido
import servicios.JornadaService
import servicios.RepartidorService

fun solicitarZonaEntrega(): String {
    println("\nZona de reparto demostrativa: Centro / Norte / Costa")
    print("Indique zona (Enter = Centro): ")
    return when (readlnOrNull()?.trim()?.lowercase()) {
        "", null, "centro" -> "Centro"
        "norte" -> "Norte"
        "costa" -> "Costa"
        else -> { println("Zona desconocida. Se utilizará Centro."); "Centro" }
    }
}

fun solicitarNotasPedido(): String? {
    print("\nNotas del pedido (Enter = Sin notas): ")
    return readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }
}

fun deseaRegistrarOtroPedido(): Boolean {
    print("\n¿Registrar otro pedido? (s/N): ")
    return readlnOrNull()?.trim()?.equals("s", ignoreCase = true) == true
}

fun gestionarEstados(jornada: JornadaService, repartidores: RepartidorService): Boolean {
    if (jornada.cantidadRegistrada() == 0) return false
    var huboCambios = false
    println("\n========= GESTIÓN DE ESTADOS =========")
    println("Ingrese un ID para avanzar el pedido o 0 para terminar.")
    println("En preparación -> Listo -> En camino -> Entregado")
    while (true) {
        print("ID de pedido (0 = salir): ")
        val entrada = readlnOrNull() ?: break
        val id = entrada.trim().toIntOrNull()
        if (id == null) { println("ID inválido. Ingrese un número."); continue }
        if (id == 0) break
        val pedido = jornada.buscar(id)
        if (pedido == null) { println("No existe un pedido con ID $id."); continue }
        when (pedido.estado) {
            EstadoPedido.EN_PREPARACION -> pedido.cambiarEstado(EstadoPedido.LISTO)
            EstadoPedido.LISTO -> {
                if (pedido.repartidor == null) {
                    val zona = jornada.zonaDePedido(id) ?: "Centro"
                    val nuevo = repartidores.asignarMasCercano(zona)
                    if (nuevo != null) pedido.asignarRepartidor(nuevo)
                }
                val repartidor = pedido.repartidor
                if (repartidor == null) { println("Sin repartidores disponibles. Permanece Listo."); continue }
                repartidor.entregarPedido(pedido)
            }
            EstadoPedido.EN_CAMINO -> pedido.cambiarEstado(EstadoPedido.ENTREGADO)
            EstadoPedido.ENTREGADO -> { println("El pedido #$id ya está entregado."); continue }
            EstadoPedido.RECHAZADO -> { println("El pedido #$id fue rechazado; no se puede despachar."); continue }
        }
        println("Pedido #$id actualizado a: ${pedido.estado}")
        huboCambios = true
    }
    return huboCambios
}
