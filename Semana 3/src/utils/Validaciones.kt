package utils

import java.time.LocalDate
import java.time.format.DateTimeParseException

private val FORMATO_TELEFONO = Regex("""\+?[0-9]{9,12}""")
private val FORMATO_CORREO = Regex("""[A-Za-z0-9_%+\-]+(?:\.[A-Za-z0-9_%+\-]+)*@[A-Za-z0-9-]+(?:\.[A-Za-z0-9-]+)+""")

/** Admite números nacionales usados en la simulación y formatos internacionales interpretables. */
fun esTelefonoValido(telefono: String): Boolean {
    if (!telefono.matches(FORMATO_TELEFONO)) return false
    val digitos = telefono.removePrefix("+")
    return when (digitos.length) {
        9, 10 -> !telefono.startsWith("+")
        11 -> digitos.startsWith("56")
        12 -> true // Código de país de 2 dígitos + número local de 10 dígitos.
        else -> false
    }
}
fun esCorreoValido(correo: String): Boolean =
    correo.length <= 254 && correo.matches(FORMATO_CORREO) && correo.substringAfterLast('.').length >= 2

fun esCantidadValida(cantidad: Int): Boolean = cantidad in Constantes.CANTIDAD_MINIMA..Constantes.CANTIDAD_MAXIMA

/** Cada entrada aceptada se representa con grupos legibles, sin salida genérica ambigua. */
fun formatearTelefono(entrada: String): String {
    require(esTelefonoValido(entrada)) { "Teléfono inválido o prefijo internacional incompleto" }
    val digitos = entrada.removePrefix("+")
    return when {
        // Móvil chileno de nueve dígitos: +56 (9) XXXX-XXXX.
        digitos.length == 9 -> "+56 (${digitos.take(1)}) ${digitos.substring(1, 5)}-${digitos.substring(5)}"
        digitos.length == 11 -> {
            val numero = digitos.drop(2)
            "+56 (${numero.take(1)}) ${numero.substring(1, 5)}-${numero.substring(5)}"
        }
        // Número de diez dígitos usado por la simulación: +56 (XXX) XXX-XXXX.
        digitos.length == 10 -> "+56 (${digitos.take(3)}) ${digitos.substring(3, 6)}-${digitos.substring(6)}"
        // Número internacional: +XX (XXX) XXX-XXXX.
        else -> "+${digitos.take(2)} (${digitos.substring(2, 5)}) ${digitos.substring(5, 8)}-${digitos.substring(8)}"
    }
}

fun parsearFecha(texto: String): LocalDate? = try {
    LocalDate.parse(texto.trim())
} catch (_: DateTimeParseException) {
    null
}
