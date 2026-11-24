package com.donnaluna.pizzeriaapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import modelo.Cliente

/** Navegación Compose de Donna Luna, manteniendo las cuatro pantallas originales. */
@Composable
fun PantallaDonnaLunaPremium(vm: PizzeriaViewModel = viewModel()) {
    val resumen by vm.resumen.observeAsState(ResumenInicio(0, 0, null))
    val secciones = listOf(
        BarraItem("Cliente", "👤"),
        BarraItem("Pizzas", "🍕"),
        BarraItem("Entrega", "🛵"),
        BarraItem("Panel", "📊")
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "☾ Donna Luna Pizzeria",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                "Bienvenidos a la mejor pizzería de la ciudad",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            "✦ ☾ ✦",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                secciones.forEachIndexed { indice, item ->
                    NavigationBarItem(
                        selected = vm.pagina == indice,
                        onClick = { vm.navegarA(indice) },
                        icon = { Text(item.icono, style = MaterialTheme.typography.titleLarge) },
                        label = { Text(item.titulo, maxLines = 1) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { margenes ->
        key(vm.pagina, vm.panelVista, vm.revision) {
            Column(
                modifier = Modifier.fillMaxSize().padding(margenes)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BarraDePaso(vm.pagina)
                when (vm.pagina) {
                    0 -> PantallaCliente(vm, resumen)
                    1 -> PantallaPizzas(vm)
                    2 -> PantallaEntrega(vm)
                    else -> PantallaPanel(vm, resumen)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
