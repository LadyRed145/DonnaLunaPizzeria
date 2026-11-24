package com.donnaluna.pizzeriaapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

/**
 * Campo controlado: su valor vive en el ViewModel (única fuente de verdad).
 * rememberSaveable solo conserva si el usuario ya lo editó; NO duplica el valor.
 */
@Composable
private fun CampoCliente(
    etiqueta: String,
    valor: String,
    alCambiar: (String) -> Unit,
    error: String?,
    mostrarErrores: Boolean,
    teclado: KeyboardType = KeyboardType.Text
) {
    var editado by rememberSaveable { mutableStateOf(false) }
    val errorVisible = (editado || mostrarErrores) && error != null
    OutlinedTextField(
        value = valor,
        onValueChange = { nuevoValor ->
            editado = true
            alCambiar(nuevoValor)
        },
        label = { Text(etiqueta) },
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        isError = errorVisible,
        supportingText = if (errorVisible) {
            { Text(error.orEmpty(), color = MaterialTheme.colorScheme.error) }
        } else null
    )
}

/** Paso 1: los cuatro campos se validan al editar y el avance se valida nuevamente en VM. */
@Composable
internal fun PantallaCliente(vm: PizzeriaViewModel, resumen: ResumenInicio) {
    val errores = vm.erroresCliente
    ResumenActividad(resumen)
    TituloBloque(
        etiqueta = "PASO 1 DE 3",
        titulo = "Datos del cliente",
        descripcion = "Cuéntanos dónde llevar tus pizzas."
    )
    TarjetaDonna(destacada = true) {
        Text(
            "Datos principales",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        CampoCliente(
            etiqueta = "Nombre *", valor = vm.nombre, alCambiar = { vm.nombre = it },
            error = errores.nombre, mostrarErrores = vm.mostrarErroresCliente
        )
        CampoCliente(
            etiqueta = "Dirección *", valor = vm.direccion, alCambiar = { vm.direccion = it },
            error = errores.direccion, mostrarErrores = vm.mostrarErroresCliente
        )
        CampoCliente(
            etiqueta = "Teléfono (opcional)", valor = vm.telefono, alCambiar = { vm.telefono = it },
            error = errores.telefono, mostrarErrores = vm.mostrarErroresCliente,
            teclado = KeyboardType.Phone
        )
        CampoCliente(
            etiqueta = "Correo (opcional)", valor = vm.correo, alCambiar = { vm.correo = it },
            error = errores.correo, mostrarErrores = vm.mostrarErroresCliente,
            teclado = KeyboardType.Email
        )
    }
    MensajeDonna(vm.aviso)
    Button(onClick = { vm.navegarA(1) }, modifier = Modifier.fillMaxWidth()) {
        Text("Continuar al menú de pizzas")
    }
}
