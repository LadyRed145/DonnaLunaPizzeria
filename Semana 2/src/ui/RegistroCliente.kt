package ui

import modelo.Cliente
import utils.esCorreoValido
import utils.esTelefonoValido

/** Solicita datos y usa valores predeterminados sin detener un pedido por datos inválidos. */
fun registrarCliente(): Cliente {
    println("=== REGISTRO DE CLIENTE ===")

    print("Ingrese su nombre: ")
    val nombre: String = readlnOrNull()
        ?.trim()
        .orEmpty()
        .ifBlank { "No informado" }

    print("Ingrese su dirección: ")
    val direccion: String = readlnOrNull()
        ?.trim()
        .orEmpty()
        .ifBlank { "No informada" }

    print("Ingrese su teléfono: ")
    val telefonoIngresado = readlnOrNull()
        ?.trim()
        .orEmpty()

    val telefono: String = if (esTelefonoValido(telefonoIngresado)) {
        telefonoIngresado
    } else {
        println("Aviso: teléfono inválido. Se utilizará un valor predeterminado.")
        "No informado"
    }

    print("Ingrese su correo electrónico: ")
    val correoIngresado = readlnOrNull()?.trim().orEmpty()
    val correo = correoIngresado.takeIf(::esCorreoValido)
    if (correo == null) {
        println("Aviso: correo inválido. Se mostrará correo@invalido.com como predeterminado.")
    }

    return Cliente(nombre, direccion, telefono, correo)
}
