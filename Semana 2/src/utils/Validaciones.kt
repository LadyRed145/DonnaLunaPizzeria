package utils

private val FORMATO_TELEFONO = Regex("""\+?[0-9]{8,15}""")
private val FORMATO_CORREO = Regex("""[A-Za-z0-9_%+\-]+(?:\.[A-Za-z0-9_%+\-]+)*@[A-Za-z0-9-]+(?:\.[A-Za-z0-9-]+)+""")

fun esTelefonoValido(telefono: String): Boolean = telefono.matches(FORMATO_TELEFONO)

/** Comprueba formato, no existencia de la casilla. */
fun esCorreoValido(correo: String): Boolean =
    correo.length <= 254 && correo.matches(FORMATO_CORREO) &&
            correo.substringAfterLast('.').length >= 2
