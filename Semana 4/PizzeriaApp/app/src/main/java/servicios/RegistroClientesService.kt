package servicios

import modelo.Cliente

/** Prevención efectiva de duplicados usando el contrato equals/hashCode. */
class RegistroClientesService {
    private val clientes = linkedSetOf<Cliente>()
    fun registrar(cliente: Cliente): Boolean = clientes.add(cliente)
    fun cantidadRegistrada(): Int = clientes.size
}
