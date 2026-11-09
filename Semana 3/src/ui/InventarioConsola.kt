package ui

import modelo.Pedido
import modelo.EstadoPedido
import servicios.InventarioService
import servicios.ResultadoInventario

/** Vista operativa independiente del voucher para el cliente. */
fun imprimirInventarioYAvisos(
    pedido: Pedido, inventario: InventarioService, mensajeInventario: String, evaluacion: ResultadoInventario
) {
    println("\nINVENTARIO POR INGREDIENTE (porciones de demostración)")
    println("Stock inicial por ingrediente: ${inventario.stockInicial}")
    inventario.consultarExistencias().forEach { (ingrediente, disponibles) ->
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
    if (pedido.estado == EstadoPedido.RECHAZADO) println("Aviso: el total es referencial. El pedido no fue confirmado.")
    if (evaluacion.confirmado) pedido.cliente.correo?.let { correo ->
        println("Aviso de pedido preparado para $correo (simulación; no se envía correo real).")
    }
}
