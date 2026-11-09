package ui

import modelo.EstadoPedido
import modelo.Pedido
import utils.desglosarIvaIncluido
import utils.formatearPesos

private const val ANCHO_COMPROBANTE = 58

private fun bordeComprobante(inicio: Char, trazo: Char, fin: Char) {
    println("$inicio${trazo.toString().repeat(ANCHO_COMPROBANTE + 2)}$fin")
}

private fun dividirLinea(texto: String): List<String> {
    if (texto.isEmpty()) return listOf("")
    val lineas = mutableListOf<String>()
    var resto = texto
    while (resto.length > ANCHO_COMPROBANTE) {
        val espacio = resto.lastIndexOf(' ', ANCHO_COMPROBANTE)
        val corte = if (espacio >= ANCHO_COMPROBANTE / 2) espacio else ANCHO_COMPROBANTE
        lineas.add(resto.substring(0, corte))
        resto = resto.substring(corte).trimStart()
    }
    lineas.add(resto)
    return lineas
}

private fun textoComprobante(texto: String = "", centrado: Boolean = false) {
    for (linea in dividirLinea(texto)) {
        val contenido = if (centrado) linea.padStart((ANCHO_COMPROBANTE + linea.length) / 2).padEnd(ANCHO_COMPROBANTE)
        else linea.padEnd(ANCHO_COMPROBANTE)
        println("│ $contenido │")
    }
}

private fun parComprobante(etiqueta: String, valor: String) {
    val hueco = ANCHO_COMPROBANTE - etiqueta.length - valor.length
    if (hueco >= 2) textoComprobante(etiqueta + " ".repeat(hueco) + valor)
    else { textoComprobante(etiqueta); textoComprobante("  $valor") }
}

private fun seccionComprobante(titulo: String) {
    bordeComprobante('├', '─', '┤')
    textoComprobante("  $titulo")
    bordeComprobante('├', '─', '┤')
}

private fun imprimirEncabezado(pedido: Pedido, rechazado: Boolean) {
    println()
    bordeComprobante('┌', '─', '┐')
    textoComprobante("✦ DONNA LUNA PIZZERIA ✦", centrado = true)
    textoComprobante(if (rechazado) "COTIZACIÓN REFERENCIAL — VENTA NO CONFIRMADA"
        else "COMPROBANTE DE PEDIDO SIMULADO", centrado = true)
    bordeComprobante('├', '─', '┤')
    textoComprobante("PEDIDO #${pedido.id}")
    if (rechazado) textoComprobante("No se realizó ninguna venta ni cobro.")
}

private fun imprimirCliente(pedido: Pedido) {
    seccionComprobante("CLIENTE")
    textoComprobante("Nombre: ${pedido.cliente.nombre}")
    textoComprobante("Dirección: ${pedido.cliente.direccion}")
    textoComprobante("Teléfono: ${pedido.cliente.telefono}")
    textoComprobante("Correo electrónico: ${pedido.cliente.correoParaMostrar}")
}

private fun imprimirPizzas(pedido: Pedido) {
    seccionComprobante("PIZZAS SOLICITADAS")
    pedido.cantidadesPorProducto.forEach { (producto, cantidad) ->
        val importe = producto.precio.multiply(java.math.BigDecimal.valueOf(cantidad.toLong()))
        parComprobante("${cantidad} x ${producto.nombre}", formatearPesos(importe))
        textoComprobante("  Precio unitario: ${formatearPesos(producto.precio)} | Cantidad: $cantidad")
    }
    textoComprobante()
    parComprobante("Cantidad total de pizzas", pedido.cantidadPizzas.toString())
}

private fun imprimirImportes(pedido: Pedido, rechazado: Boolean) {
    val importes = pedido.detalleImportes()
    val iva = desglosarIvaIncluido(importes.total)
    seccionComprobante("RESUMEN DEL PEDIDO")
    parComprobante("Subtotal", formatearPesos(importes.subtotal))
    parComprobante("Descuento volumen (${pedido.porcentajeDescuento()}%)", "-${formatearPesos(importes.descuentoVolumen)}")
    if (pedido.promocion != null) {
        parComprobante("Promoción (${pedido.promocion.porcentaje}%)", "-${formatearPesos(importes.descuentoPromocion)}")
    }
    parComprobante(if (rechazado) "Base neta referencial" else "Base neta del total final",
        formatearPesos(iva.baseNeta))
    parComprobante(if (rechazado) "IVA referencial incluido (19%)" else "IVA incluido (19%)",
        formatearPesos(iva.ivaIncluido))
    bordeComprobante('├', '═', '┤')
    parComprobante(if (rechazado) "TOTAL REFERENCIAL (SIN COBRO)" else "TOTAL FINAL",
        formatearPesos(importes.total))
}

private fun imprimirPie(pedido: Pedido) {
    bordeComprobante('├', '═', '┤')
    textoComprobante("Notas: ${pedido.notas?.takeIf { it.isNotBlank() } ?: "Sin notas"}")
    textoComprobante("Estado: ${pedido.estado}")
    textoComprobante("Repartidor: ${pedido.repartidor?.nombre ?: "Sin asignar"}")
    textoComprobante()
    textoComprobante("Gracias por elegir Donna Luna", centrado = true)
    bordeComprobante('└', '─', '┘')
}

/** Solo voucher: el inventario y sus avisos se imprimen en InventarioConsola.kt. */
fun imprimirResumen(pedido: Pedido) {
    val rechazado = pedido.estado == EstadoPedido.RECHAZADO
    imprimirEncabezado(pedido, rechazado)
    imprimirCliente(pedido)
    imprimirPizzas(pedido)
    imprimirImportes(pedido, rechazado)
    imprimirPie(pedido)
}
