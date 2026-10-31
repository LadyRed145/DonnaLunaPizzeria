package ui

import modelo.Pedido
import servicios.InventarioService
import servicios.ResultadoInventario
import utils.desglosarIvaIncluido
import utils.formatearPesos
import utils.formatearPesosConDecimales

/**
 * Formato de comprobante en consola. No altera los cálculos ni el estado del pedido.
 * Las líneas largas se dividen para conservar los datos sin romper el marco.
 */
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
        val contenido = if (centrado) {
            linea.padStart((ANCHO_COMPROBANTE + linea.length) / 2).padEnd(ANCHO_COMPROBANTE)
        } else {
            linea.padEnd(ANCHO_COMPROBANTE)
        }
        println("│ $contenido │")
    }
}

private fun parComprobante(etiqueta: String, valor: String) {
    val hueco = ANCHO_COMPROBANTE - etiqueta.length - valor.length
    if (hueco >= 2) {
        textoComprobante(etiqueta + " ".repeat(hueco) + valor)
    } else {
        textoComprobante(etiqueta)
        textoComprobante("  $valor")
    }
}

private fun seccionComprobante(titulo: String) {
    bordeComprobante('├', '─', '┤')
    textoComprobante("  $titulo")
    bordeComprobante('├', '─', '┤')
}

/** Presentación separada de las reglas del dominio. */
fun imprimirResumen(
    pedido: Pedido,
    inventario: InventarioService,
    mensajeInventario: String,
    evaluacion: ResultadoInventario
) {
    val cantidadPizzas: Long = pedido.cantidadPizzas
    val subtotal: Double = pedido.calcularSubtotal()
    val porcentajeDescuento = pedido.porcentajeDescuento()
    val descuentoAplicado = pedido.calcularDescuento()
    val totalPedido: Double = pedido.calcularTotal()
    val iva = desglosarIvaIncluido(totalPedido)
    val rechazado = pedido.estado == "Rechazado"

    println()
    bordeComprobante('┌', '─', '┐')
    textoComprobante("✦ DONNA LUNA PIZZERIA ✦", centrado = true)
    textoComprobante(
        if (rechazado) "COTIZACIÓN REFERENCIAL — VENTA NO CONFIRMADA"
        else "COMPROBANTE DE PEDIDO SIMULADO",
        centrado = true
    )
    bordeComprobante('├', '─', '┤')
    textoComprobante("PEDIDO #${pedido.id}")
    if (rechazado) {
        textoComprobante("No se realizó ninguna venta ni cobro.")
    }

    seccionComprobante("CLIENTE")
    textoComprobante("Nombre: ${pedido.cliente.nombre}")
    textoComprobante("Dirección: ${pedido.cliente.direccion}")
    textoComprobante("Teléfono: ${pedido.cliente.telefono}")
    textoComprobante("Correo electrónico: ${pedido.cliente.correoParaMostrar}")

    seccionComprobante("PIZZAS SOLICITADAS")
    for ((producto, cantidad) in pedido.cantidadesPorProducto) {
        val subtotalPizza = producto.precio * cantidad
        parComprobante("${cantidad} x ${producto.nombre}", formatearPesos(subtotalPizza))
        textoComprobante("  Precio unitario: ${formatearPesos(producto.precio)} | Cantidad: $cantidad")
    }
    textoComprobante()
    parComprobante("Cantidad total de pizzas", cantidadPizzas.toString())

    seccionComprobante("RESUMEN DEL PEDIDO")
    parComprobante("Subtotal", formatearPesos(subtotal))
    parComprobante("Descuento ($porcentajeDescuento%)", "-${formatearPesos(descuentoAplicado)}")
    if (rechazado) {
        parComprobante("Base neta referencial", formatearPesosConDecimales(iva.baseNeta))
        parComprobante("IVA referencial incluido (19%)", formatearPesosConDecimales(iva.ivaIncluido))
    } else {
        parComprobante("Base neta del total final", formatearPesosConDecimales(iva.baseNeta))
        parComprobante("IVA incluido (19%)", formatearPesosConDecimales(iva.ivaIncluido))
    }
    bordeComprobante('├', '═', '┤')
    parComprobante(
        if (rechazado) "TOTAL REFERENCIAL (SIN COBRO)" else "TOTAL FINAL",
        formatearPesos(totalPedido)
    )
    bordeComprobante('├', '═', '┤')
    textoComprobante("Notas: ${pedido.notas?.takeIf { it.isNotBlank() } ?: "Sin notas"}")
    textoComprobante("Estado: ${pedido.estado}")
    textoComprobante("Repartidor: ${pedido.repartidor?.nombre ?: "Sin asignar"}")
    textoComprobante()
    textoComprobante("Gracias por elegir Donna Luna", centrado = true)
    bordeComprobante('└', '─', '┘')

    // Información operativa: se conserva fuera del comprobante del cliente.
    println("\nINVENTARIO POR INGREDIENTE (porciones de demostración)")
    println("Stock inicial por ingrediente: ${inventario.stockInicial}")
    for ((ingrediente, disponibles) in inventario.consultarExistencias()) {
        println("- $ingrediente: $disponibles porción(es) disponibles")
    }
    println("Resultado: $mensajeInventario")

    if (!evaluacion.confirmado && evaluacion.faltantes.isNotEmpty()) {
        println("\nINGREDIENTES INSUFICIENTES")
        for ((ingrediente, faltan) in evaluacion.faltantes) {
            val necesarios = evaluacion.requeridos.getValue(ingrediente)
            val disponibles = evaluacion.disponiblesAlEvaluar[ingrediente] ?: 0L
            println("- $ingrediente: disponibles $disponibles, necesarios $necesarios, faltan $faltan")
        }
        println("PASOS PARA REABASTECER")
        println("1. Revisar la lista de ingredientes faltantes.")
        println("2. Reponer las porciones indicadas de cada ingrediente.")
        println("3. Registrar la reposición en el inventario antes de volver a confirmar el pedido.")
    }

    if (rechazado) {
        println("Aviso: el total es referencial. El pedido no fue confirmado.")
    }

    // Únicamente los pedidos realmente confirmados generan el aviso simulado.
    if (evaluacion.confirmado) {
        pedido.cliente.correo?.let { correo ->
            println("Aviso de pedido preparado para $correo (simulación; no se envía correo real).")
        }
    }
}
