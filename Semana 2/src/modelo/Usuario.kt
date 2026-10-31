package modelo

/** Datos comunes de quienes participan en los pedidos. */
open class Usuario(
    open val nombre: String,
    open val contacto: String
) {
    open fun describirRol(): String = "Usuario"
}
