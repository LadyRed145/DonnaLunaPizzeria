package utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/** Formato de presentación para pesos chilenos. No modifica el cálculo original. */
private val simbolosChilenos = DecimalFormatSymbols(Locale.forLanguageTag("es-CL"))

/** Totales y precios de catálogo expresados en pesos sin centavos. */
fun formatearPesos(monto: Double): String =
    "$" + DecimalFormat("#,##0", simbolosChilenos).format(monto)

/** Desglose del IVA expresado con dos decimales. */
fun formatearPesosConDecimales(monto: BigDecimal): String =
    "$" + DecimalFormat("#,##0.00", simbolosChilenos).format(monto)
