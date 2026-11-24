package com.donnaluna.pizzeriaapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import utils.Constantes
import utils.esCantidadValida

@Composable
internal fun PantallaPizzas(vm: PizzeriaViewModel) {
    TituloBloque(
        etiqueta = "PASO 2 DE 3",
        titulo = "Elige tus pizzas",
        descripcion = "Elige entre nuestras cinco especialidades."
    )
    TarjetaDonna(destacada = true) {
        Text(
            "✦ Especialidades de Donna Luna",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Precios con IVA incluido. Más de 3 pizzas = 10 % de descuento.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    for (pizza in vm.menu) {
        val cantidad = vm.cantidades[pizza] ?: 0
        TarjetaPizza(pizza = pizza, cantidad = cantidad,
            onMenos = { if (cantidad <= 1) vm.cantidades.remove(pizza) else vm.cantidades[pizza] = cantidad - 1 },
            onMas = {
                if (esCantidadValida(vm.totalPizzas() + 1)) vm.cantidades[pizza] = cantidad + 1
            },
            puedeSumar = esCantidadValida(vm.totalPizzas() + 1)
        )
    }
    TarjetaDonna(destacada = true) {
        FilaEtiquetaValor("Pizzas seleccionadas", "${vm.totalPizzas()} / ${Constantes.CANTIDAD_MAXIMA}")
        if (vm.mostrarErrorPizzas && !esCantidadValida(vm.totalPizzas())) {
            Text(Constantes.MENSAJE_CANTIDAD_TOTAL, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            if (vm.totalPizzas() > 3) "Descuento por volumen aplicado al confirmar."
            else "Suma más de 3 pizzas para activar el descuento.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
    Button(onClick = { vm.navegarA(2) }, modifier = Modifier.fillMaxWidth()) {
        Text("Continuar a entrega y confirmación")
    }
}
