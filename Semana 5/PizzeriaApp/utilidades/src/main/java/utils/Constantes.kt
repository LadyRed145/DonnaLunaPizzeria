package utils

import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

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
    const val FECHA_PREDETERMINADA_PROMOCION = "2026-11-15"
    const val FECHA_INICIO_PROMOCION = "2026-11-01"
    const val FECHA_FIN_PROMOCION = "2026-11-30"
    const val FORMATO_FECHA_TEXTO = "AAAA-MM-DD"
    // uuuu + STRICT evita aceptar fechas imposibles, como 2026-02-30.
    val FORMATEADOR_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
        .withResolverStyle(ResolverStyle.STRICT)

    val MENSAJE_CORREO_INVALIDO = "Correo inválido: usa nombre@dominio.extensión; el dominio no puede comenzar ni terminar con guion y la extensión debe contener solo letras."
    val MENSAJE_FECHA_INVALIDA = "Fecha inválida: usa $FORMATO_FECHA_TEXTO (ejemplo: $FECHA_INICIO_PROMOCION)."
    val MENSAJE_VIGENCIA_PROMOCION = "Vigencia demostrativa: $FECHA_INICIO_PROMOCION a $FECHA_FIN_PROMOCION; solo pizzas elegibles."
    val MENSAJE_CANTIDAD_PRODUCTO = "Cada cantidad debe estar entre $CANTIDAD_MINIMA y $CANTIDAD_MAXIMA."
    val MENSAJE_CANTIDAD_TOTAL = "El pedido debe tener entre $CANTIDAD_MINIMA y $CANTIDAD_MAXIMA pizzas en total."

    val CIEN: BigDecimal = BigDecimal("100")
    val FACTOR_IVA: BigDecimal = BigDecimal.ONE.add(BigDecimal.valueOf(PORCENTAJE_IVA.toLong()).divide(CIEN))
}
