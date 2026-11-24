package com.donnaluna.pizzeriaapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import modelo.Cliente
import modelo.EstadoPedido
import modelo.Pedido
import modelo.Producto
import utils.formatearPesos

internal data class BarraItem(val titulo: String, val icono: String)

@Composable
internal fun BarraDePaso(pagina: Int) {
    if (pagina > 2) return
    val pasos = listOf("Cliente", "Pizzas", "Entrega")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pasos.forEachIndexed { indice, titulo ->
            val activo = indice == pagina
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(999.dp),
                color = if (activo) MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (activo) MaterialTheme.colorScheme.secondary else Color.Transparent
                )
            ) {
                Text(
                    text = if (activo) "✦ ${indice + 1}. $titulo" else "${indice + 1}. $titulo",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (activo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
internal fun TituloBloque(etiqueta: String, titulo: String, descripcion: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = "✦ $etiqueta",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun TarjetaDonna(destacada: Boolean = false, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (destacada) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface
        ),
        border = if (destacada) androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)
        ) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (destacada) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = contenido
        )
    }
}

@Composable
internal fun TarjetaPizza(
    pizza: Producto,
    cantidad: Int,
    onMenos: () -> Unit,
    onMas: () -> Unit,
    puedeSumar: Boolean
) {
    TarjetaDonna(destacada = cantidad > 0) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(id = imagenPizza(pizza.nombre)),
                contentDescription = "Fotografía ilustrativa de pizza ${pizza.nombre}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(92.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.75f), RoundedCornerShape(14.dp))
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(pizza.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    resumenIngredientes(pizza),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    formatearPesos(pizza.precio),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Unidades", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalIconButton(modifier = Modifier.size(40.dp), onClick = onMenos, enabled = cantidad > 0) {
                    Text("−", fontWeight = FontWeight.Bold)
                }
                Text(cantidad.toString(), modifier = Modifier.widthIn(min = 26.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                FilledTonalIconButton(modifier = Modifier.size(40.dp), onClick = onMas, enabled = puedeSumar) {
                    Text("+", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
internal fun MensajeDonna(texto: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
internal fun EstadoVisualPedido(estado: EstadoPedido) {
    val pasos = listOf(
        EstadoPedido.EN_PREPARACION to "Preparación",
        EstadoPedido.LISTO to "Listo",
        EstadoPedido.EN_CAMINO to "En camino",
        EstadoPedido.ENTREGADO to "Entregado"
    )
    if (estado == EstadoPedido.RECHAZADO) {
        Surface(color = Color(0xFFF9E5E2), shape = RoundedCornerShape(12.dp)) {
            Text("Pedido rechazado", color = Color(0xFF953B2B), modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
        }
        return
    }
    val indiceActual = pasos.indexOfFirst { it.first == estado }.coerceAtLeast(0)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Estado: ${estado.descripcion}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            pasos.forEachIndexed { indice, item ->
                val alcanzado = indice <= indiceActual
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = if (alcanzado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(999.dp)
                    ) {}
                    Text(
                        if (indice == indiceActual) "✦ ${item.second}" else item.second,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (alcanzado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontWeight = if (indice == indiceActual) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
internal fun ResumenJornada(pedidos: Int, completados: Int, pendientes: Int, rechazados: Int) {
    TarjetaMiniStat("Pedidos", pedidos.toString())
    TarjetaMiniStat("Completados", completados.toString())
    TarjetaMiniStat("Pendientes", pendientes.toString())
    TarjetaMiniStat("Rechazados", rechazados.toString())
}

@Composable
internal fun TarjetaMiniStat(titulo: String, valor: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(titulo, fontWeight = FontWeight.Medium)
            Text(valor, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
internal fun ItemInventario(ingrediente: String, unidades: Long, stockInicial: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        FilaEtiquetaValor(ingrediente, "$unidades porciones")
        BarraStock(unidades, stockInicial)
    }
}

@Composable
internal fun BarraStock(actual: Long, maximo: Int) {
    val proporcion = if (maximo <= 0) 0f else (actual.toFloat() / maximo.toFloat()).coerceIn(0f, 1f)
    val color = when {
        proporcion > 0.66f -> Color(0xFF58A55C)
        proporcion > 0.33f -> MaterialTheme.colorScheme.secondary
        else -> Color(0xFFC55B5B)
    }
    Box(
        modifier = Modifier.fillMaxWidth().height(8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(999.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(proporcion).height(8.dp)
                .background(color, RoundedCornerShape(999.dp))
        )
    }
}

@Composable
internal fun FilaEtiquetaValor(titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(titulo, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End)
    }
}

internal fun imagenPizza(nombre: String): Int = when (nombre.lowercase()) {
    "italiana" -> R.drawable.pizza_italiana
    "margarita" -> R.drawable.pizza_margarita
    "pepperoni" -> R.drawable.pizza_pepperoni
    "cuatro quesos" -> R.drawable.pizza_cuatro_quesos
    "hawaiana" -> R.drawable.pizza_hawaiana
    else -> R.drawable.pizza_margarita
}

/** Se describe la receta real del catálogo para no inventar ingredientes en la interfaz. */
internal fun resumenIngredientes(pizza: Producto): String =
    pizza.ingredientes.keys.filterNot { it == "Masa" || it == "Salsa de tomate" }
        .joinToString(" · ")
