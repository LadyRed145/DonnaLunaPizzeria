package utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val simbolosChilenos = DecimalFormatSymbols(Locale.forLanguageTag("es-CL"))

fun formatearPesos(monto: BigDecimal): String =
    "$" + DecimalFormat("#,##0", simbolosChilenos).format(monto)

