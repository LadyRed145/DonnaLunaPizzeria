package utils

/** Errores independientes por campo, utilizados tanto por Compose como por el ViewModel. */
data class ErroresCliente(
    val nombre: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val correo: String? = null
) {
    val esValido: Boolean get() = primerError() == null
    fun primerError(): String? = nombre ?: direccion ?: telefono ?: correo
}

/**
 * Punto único de validación del formulario de Cliente.
 * Teléfono y correo son opcionales; cuando se informan deben ser válidos.
 * Los mensajes se usan sin duplicarse en UI, navegación y confirmación.
 */
fun revisarDatosCliente(
    nombre: String,
    direccion: String,
    telefono: String,
    correo: String
): ErroresCliente {
    val nombreLimpio = nombre.trim()
    val direccionLimpia = direccion.trim()
    val telefonoLimpio = telefono.trim()
    val correoLimpio = correo.trim()

    val nombreCorrecto = nombreLimpio.length in 2..80 &&
        nombreLimpio.any(Char::isLetter) &&
        nombreLimpio.all { it.isLetter() || it == ' ' || it == '-' || it == '\'' || it == '’' }
    val direccionCorrecta = direccionLimpia.length in 4..160 &&
        direccionLimpia.any(Char::isLetter) && '@' !in direccionLimpia

    return ErroresCliente(
        nombre = if (nombreCorrecto) null else "Ingresa un nombre válido (2 a 80 caracteres, solo letras).",
        direccion = if (direccionCorrecta) null else "Ingresa una dirección física válida (mínimo 4 caracteres).",
        telefono = if (telefonoLimpio.isBlank() || esTelefonoValido(telefonoLimpio)) null
            else "Teléfono inválido. Usa 9 o 10 dígitos nacionales, o +56 seguido del número.",
        correo = if (correoLimpio.isBlank() || esCorreoValido(correoLimpio)) null
            else Constantes.MENSAJE_CORREO_INVALIDO
    )
}
