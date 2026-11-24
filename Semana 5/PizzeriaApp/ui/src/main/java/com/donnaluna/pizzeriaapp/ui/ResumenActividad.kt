package com.donnaluna.pizzeriaapp.ui

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

/** Los valores provienen del LiveData observable del ViewModel, no de textos estáticos. */
@Composable
internal fun ResumenActividad(resumen: ResumenInicio) {
    TarjetaDonna(destacada = true) {
        Text("✦ Resumen de actividad", style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Se actualiza al registrar pedidos desde Entrega.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        HorizontalDivider()
        FilaEtiquetaValor("Clientes registrados", resumen.totalClientes.toString())
        FilaEtiquetaValor("Pedidos realizados", resumen.totalPedidos.toString())
        FilaEtiquetaValor("Último cliente registrado", resumen.ultimoCliente ?: "Aún no hay clientes")
    }
}
