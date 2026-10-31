package modelo

/** Datos del cliente; un correo inválido se conserva como null para evitar notificaciones falsas. */
class Cliente(
    override val nombre: String,
    val direccion: String,
    val telefono: String,
    val correo: String? = null
) : Usuario(nombre, telefono) {
    val correoParaMostrar: String get() = correo ?: "correo@invalido.com"

    override fun describirRol(): String = "Cliente"
}
