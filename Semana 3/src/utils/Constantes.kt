package utils

import java.math.BigDecimal

/** Reglas compartidas; precios del catálogo e IVA incluido se mantienen. */
object Constantes {
    const val STOCK_INICIAL_INGREDIENTE = 20
    const val ID_INICIAL_PEDIDOS = 1001
    const val ID_INICIAL_COMBINADOS = 9001
    const val UMBRAL_DESCUENTO_VOLUMEN = 3
    const val PORCENTAJE_DESCUENTO_VOLUMEN = 10
    const val PORCENTAJE_PROMOCION = 5
    const val PORCENTAJE_IVA = 19
    const val CANTIDAD_MINIMA = 1
    const val CANTIDAD_MAXIMA = 100
    const val FECHA_INICIO_PROMOCION = "2026-11-01"
    const val FECHA_FIN_PROMOCION = "2026-11-30"
    val CIEN: BigDecimal = BigDecimal("100")
    val FACTOR_IVA: BigDecimal = BigDecimal.ONE.add(BigDecimal.valueOf(PORCENTAJE_IVA.toLong()).divide(CIEN))
}
