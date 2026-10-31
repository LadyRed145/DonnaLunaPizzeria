package utils

import java.math.BigDecimal
import java.math.RoundingMode

/** Los precios publicados en el menú se consideran finales, con IVA del 19% incluido. */
private val FACTOR_IVA = BigDecimal("1.19")

data class DesgloseIva(
    val baseNeta: BigDecimal,
    val ivaIncluido: BigDecimal
)

/** Desglosa el IVA incluido sin sumarlo por segunda vez al precio final. */
fun desglosarIvaIncluido(totalFinal: Double): DesgloseIva {
    require(totalFinal.isFinite() && totalFinal >= 0.0) {
        "El total debe ser un importe válido y no negativo"
    }
    val total = BigDecimal.valueOf(totalFinal).setScale(2, RoundingMode.HALF_UP)
    val neto = total.divide(FACTOR_IVA, 2, RoundingMode.HALF_UP)
    return DesgloseIva(neto, total.subtract(neto))
}
