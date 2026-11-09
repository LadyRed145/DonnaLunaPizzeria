package utils

import java.math.BigDecimal
import java.math.RoundingMode

/** Los precios publicados incluyen IVA; el desglose se expresa en pesos chilenos enteros. */
data class DesgloseIva(val baseNeta: BigDecimal, val ivaIncluido: BigDecimal)

fun redondearPesos(monto: BigDecimal): BigDecimal = monto.setScale(0, RoundingMode.HALF_UP)

fun porcentajeDe(monto: BigDecimal, porcentaje: Int): BigDecimal =
    redondearPesos(monto.multiply(BigDecimal.valueOf(porcentaje.toLong()))
        .divide(Constantes.CIEN, 8, RoundingMode.HALF_UP))

fun desglosarIvaIncluido(totalFinal: BigDecimal): DesgloseIva {
    require(totalFinal.signum() >= 0) { "El importe debe ser no negativo" }
    val totalEnPesos = redondearPesos(totalFinal)
    val neto = totalEnPesos.divide(Constantes.FACTOR_IVA, 0, RoundingMode.HALF_UP)
    // IVA como diferencia para garantizar que neto + IVA = total mostrado.
    return DesgloseIva(neto, totalEnPesos.subtract(neto))
}
