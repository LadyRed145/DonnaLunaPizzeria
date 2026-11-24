package com.donnaluna.pizzeriaapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import modelo.Cliente
import modelo.EstadoPedido
import modelo.Pedido
import utils.desglosarIvaIncluido
import utils.formatearPesos

@Composable
internal fun PantallaPanel(vm: PizzeriaViewModel, resumen: ResumenInicio) {
    ResumenActividad(resumen)
    TituloBloque(
        etiqueta = "PANEL DE CONTROL",
        titulo = "Seguimiento y gestión",
        descripcion = "Consulta el pedido, la jornada y el inventario."
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        listOf("Pedido", "Jornada", "Stock").forEachIndexed { indice, titulo ->
            FilterChip(selected = vm.panelVista == indice, onClick = { vm.panelVista = indice }, label = { Text(titulo) })
        }
    }
    when (vm.panelVista) {
        0 -> {
            MensajeDonna(vm.aviso)
            val pedido = vm.ultimoPedido
            if (pedido == null) {
                TarjetaDonna {
                    Text("Todavía no hay pedidos registrados.")
                    Text("Crea uno desde las pestañas Cliente, Pizzas y Entrega.", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                TarjetaDonna(destacada = true) {
                    Text("Pedido #${pedido.id}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    EstadoVisualPedido(pedido.estado)
                    Text("Cliente: ${pedido.cliente.nombre}")
                    HorizontalDivider()
                    pedido.cantidadesPorProducto.forEach { (producto, cantidad) ->
                        FilaEtiquetaValor(producto.nombre, "x$cantidad")
                    }
                    val importes = pedido.detalleImportes()
                    HorizontalDivider()
                    FilaEtiquetaValor("Subtotal", formatearPesos(importes.subtotal))
                    FilaEtiquetaValor("Descuento por volumen", "−${formatearPesos(importes.descuentoVolumen)}")
                    FilaEtiquetaValor("Descuento promocional", "−${formatearPesos(importes.descuentoPromocion)}")
                    val iva = desglosarIvaIncluido(importes.total)
                    FilaEtiquetaValor("Neto", formatearPesos(iva.baseNeta))
                    FilaEtiquetaValor("IVA incluido", formatearPesos(iva.ivaIncluido))
                    HorizontalDivider()
                    Text(
                        if (pedido.estado == EstadoPedido.RECHAZADO) "TOTAL REFERENCIAL (SIN COBRO)" else "TOTAL FINAL",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        formatearPesos(importes.total),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Repartidor: ${pedido.repartidor?.nombre ?: "Sin asignar"}")
                    if (pedido.estado in listOf(EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO, EstadoPedido.EN_CAMINO)) {
                        Button(onClick = { vm.avanzarEstado() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Avanzar estado del pedido")
                        }
                    }
                }
            }
        }
        1 -> {
            TarjetaDonna(destacada = true) {
                Text("Reporte del día", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                ResumenJornada(
                    pedidos = vm.jornada.cantidadRegistrada(),
                    completados = vm.jornada.completados().size,
                    pendientes = vm.jornada.pendientes().size,
                    rechazados = vm.jornada.rechazados().size
                )
                HorizontalDivider()
                val pedidos = vm.jornada.pendientes() + vm.jornada.completados() + vm.jornada.rechazados()
                if (pedidos.isEmpty()) Text("Aún no hay pedidos en esta jornada.")
                pedidos.forEach { pedido ->
                    Text("#${pedido.id} · ${pedido.cliente.nombre} · ${pedido.estado.descripcion}")
                }
            }
        }
        else -> {
            TarjetaDonna(destacada = true) {
                Text("Inventario disponible", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "Stock inicial: ${vm.inventario.stockInicial} porciones por ingrediente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider()
                vm.inventario.consultarExistencias().forEach { (ingrediente, unidades) ->
                    ItemInventario(ingrediente, unidades, vm.inventario.stockInicial)
                }
            }
        }
    }
}
