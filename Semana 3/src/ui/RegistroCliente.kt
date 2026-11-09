package ui

import modelo.Cliente
import servicios.RegistroClientesService
import utils.esCorreoValido
import utils.esTelefonoValido
import utils.formatearTelefono

/** Solicitud segura: correo incorrecto se repite; Enter permite omitirlo. */
fun registrarCliente(registro: RegistroClientesService): Cliente? {
    println("=== REGISTRO DE CLIENTE ===")
    print("Ingrese su nombre: ")
    val nombre = (readlnOrNull() ?: return cancelarRegistro()).trim().ifBlank { "No informado" }
    var direccion: String
    while (true) {
        print("Ingrese su dirección: ")
        val entrada = (readlnOrNull() ?: return cancelarRegistro()).trim()
        // Permite calles, parcelas y direcciones sin número; nunca acepta correos electrónicos.
        if (entrada.length >= 4 && entrada.any { it.isLetter() } && '@' !in entrada) {
            direccion = entrada
            break
        }
        println("Aviso: dirección inválida. Ingrese una dirección física, no un correo electrónico (ej.: Los Álamos 531 o Parcela S/N).")
    }

    var telefono: String
    while (true) {
        print("Ingrese su teléfono: ")
        val entrada = readlnOrNull() ?: return cancelarRegistro()
        if (entrada.isBlank()) { telefono = "No informado"; break }
        if (esTelefonoValido(entrada.trim())) { telefono = formatearTelefono(entrada.trim()); break }
        println("Aviso: teléfono inválido. Use 9 o 10 dígitos nacionales, +56 y 9 dígitos, o +XX y 10 dígitos.")
    }

    var correo: String?
    while (true) {
        print("Ingrese su correo electrónico (Enter = Sin correo): ")
        val entrada = (readlnOrNull() ?: return cancelarRegistro()).trim()
        if (entrada.isEmpty()) { correo = null; break }
        if (esCorreoValido(entrada)) { correo = entrada; break }
        println("Aviso: correo inválido. Ejemplo: nombre@dominio.com. Inténtelo nuevamente.")
    }

    val cliente = Cliente(nombre, direccion, telefono, correo)
    if (!registro.registrar(cliente)) {
        println("Aviso: cliente existente identificado por nombre y correo; no se agregó duplicado.")
        // No se sustituye el pedido por datos de otro registro: solo se evita duplicidad en el registro.
    }
    return cliente
}

/** Evita repetir solicitudes indefinidamente cuando se cierra la entrada estándar. */
private fun cancelarRegistro(): Cliente? {
    println("\nEntrada cerrada. Registro de cliente cancelado.")
    return null
}
