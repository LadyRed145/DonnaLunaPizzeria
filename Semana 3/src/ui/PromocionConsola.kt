package ui

import java.time.LocalDate
import modelo.PromocionPedido
import servicios.PromocionesService
import utils.parsearFecha

/** Solicitud optativa para no alterar los precios de las entregas anteriores. */
fun solicitarPromocion(): PromocionPedido? {
    print("\n¿Evaluar una promoción por fecha? (s/N): ")
    if (!readlnOrNull().orEmpty().trim().equals("s", ignoreCase = true)) return null
    println("Promoción demostrativa: 01-11-2026 a 30-11-2026, 5% en pizzas anotadas.")
    while (true) {
        print("Ingrese fecha (AAAA-MM-DD, Enter = hoy): ")
        val entrada = readlnOrNull()?.trim() ?: return null
        val fecha = if (entrada.isEmpty()) LocalDate.now() else parsearFecha(entrada)
        if (fecha == null) { println("Aviso: fecha inválida. Ejemplo 2026-11-15."); continue }
        val promo = PromocionesService.obtenerPromocion(fecha)
        if (promo == null) println("Fuera del rango promocional: se mantiene el precio habitual.")
        else println("Promoción vigente: ${promo.productosElegibles.joinToString { it.nombre }}.")
        return promo
    }
}
