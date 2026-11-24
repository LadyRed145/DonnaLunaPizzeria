package modelo

import java.util.Locale

/** Clientes iguales cuando coinciden nombre y correo, sin distinguir mayúsculas ni espacios extremos. */
class Cliente(
    override val nombre: String,
    val direccion: String,
    val telefono: String,
    val correo: String? = null
) : Usuario(nombre, telefono) {
    val correoParaMostrar: String get() = correo ?: "Sin correo"
    override fun describirRol(): String = "Cliente"
    operator fun component1(): String = nombre
    operator fun component2(): String? = correo
    operator fun component3(): String = telefono

    private fun nombreClave() = nombre.trim().lowercase(Locale.ROOT)
    private fun correoClave() = correo?.trim()?.lowercase(Locale.ROOT)

    override fun equals(other: Any?): Boolean = this === other ||
        (other is Cliente && nombreClave() == other.nombreClave() && correoClave() == other.correoClave())

    override fun hashCode(): Int = 31 * nombreClave().hashCode() + (correoClave()?.hashCode() ?: 0)
}
