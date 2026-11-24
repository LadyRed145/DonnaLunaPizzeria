package com.donnaluna.pizzeriaapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import modelo.Cliente
import utils.Constantes
import utils.parsearFecha

@Composable
internal fun PantallaEntrega(vm: PizzeriaViewModel) {
    TituloBloque(
        etiqueta = "PASO 3 DE 3",
        titulo = "Entrega y confirmación",
        descripcion = "Ajusta los últimos detalles antes de enviar tu pedido."
    )
    TarjetaDonna(destacada = true) {
        Text("Zona de entrega", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("Centro", "Norte", "Costa").forEach { opcion ->
                FilterChip(selected = vm.zona == opcion, onClick = { vm.zona = opcion }, label = { Text(opcion) })
            }
        }
        OutlinedTextField(vm.notas, { vm.notas = it }, label = { Text("Notas para el pedido (opcional)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Evaluar promoción del 5 % por fecha", modifier = Modifier.weight(1f))
            Switch(checked = vm.usarPromocion, onCheckedChange = { vm.usarPromocion = it })
        }
        if (vm.usarPromocion) {
            OutlinedTextField(
                vm.fechaPromocion, { vm.fechaPromocion = it },
                label = { Text("Fecha ${Constantes.FORMATO_FECHA_TEXTO}") },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                isError = vm.mostrarErrorFecha && parsearFecha(vm.fechaPromocion) == null
            )
            if (vm.mostrarErrorFecha && parsearFecha(vm.fechaPromocion) == null) {
                Text(Constantes.MENSAJE_FECHA_INVALIDA, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                Constantes.MENSAJE_VIGENCIA_PROMOCION,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    TarjetaDonna {
        Text("Resumen antes de confirmar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        FilaEtiquetaValor("Cliente", vm.nombre.ifBlank { "Sin completar" })
        FilaEtiquetaValor("Destino", vm.zona)
        FilaEtiquetaValor("Pizzas", vm.totalPizzas().toString())
        vm.cantidades.forEach { (producto, cantidad) ->
            if (cantidad > 0) FilaEtiquetaValor("• ${producto.nombre}", "x$cantidad")
        }
    }
    Button(onClick = { vm.confirmarPedido() }, modifier = Modifier.fillMaxWidth()) {
        Text("Confirmar pedido")
    }
    MensajeDonna(vm.aviso)
}
