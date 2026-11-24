package com.donnaluna.pizzeriaapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import modelo.Cliente
import modelo.EstadoPedido
import modelo.Pedido
import modelo.Producto
import servicios.CatalogoPizzas
import servicios.InventarioService
import servicios.JornadaService
import servicios.PromocionesService
import servicios.RegistroClientesService
import servicios.RepartidorService
import utils.Constantes
import utils.ErroresCliente
import utils.esCantidadValida
import utils.formatearTelefono
import utils.parsearFecha
import utils.revisarDatosCliente

/** Estado reactivo de la sesión; el modelo y la lógica permanecen en :datos y :utilidades. */
data class ResumenInicio(val totalClientes: Int, val totalPedidos: Int, val ultimoCliente: String?)

class PizzeriaViewModel : ViewModel() {
    val inventario = InventarioService(Constantes.STOCK_INICIAL_INGREDIENTE)
    val registroClientes = RegistroClientesService()
    val repartidores = RepartidorService()
    val jornada = JornadaService()
    val menu: List<Producto> = CatalogoPizzas.menu
    val cantidades = mutableStateMapOf<Producto, Int>()

    var nombre by mutableStateOf("")
    var direccion by mutableStateOf("")
    var telefono by mutableStateOf("")
    var correo by mutableStateOf("")
    var notas by mutableStateOf("")
    var zona by mutableStateOf("Centro")
    var usarPromocion by mutableStateOf(false)
    var fechaPromocion by mutableStateOf(Constantes.FECHA_PREDETERMINADA_PROMOCION)
    var aviso by mutableStateOf("Bienvenida a Donna Luna. Completa tus datos para comenzar.")
    var ultimoPedido by mutableStateOf<Pedido?>(null)
        private set
    var revision by mutableIntStateOf(0)
        private set
    var pagina by mutableIntStateOf(0)
    var panelVista by mutableIntStateOf(0)
    var mostrarErroresCliente by mutableStateOf(false)
    var mostrarErrorPizzas by mutableStateOf(false)
    var mostrarErrorFecha by mutableStateOf(false)

    // Validación calculada a partir del estado actual: no conserva errores obsoletos.
    internal val erroresCliente: ErroresCliente
        get() = revisarDatosCliente(nombre, direccion, telefono, correo)

    private val _resumen = MutableLiveData(ResumenInicio(0, 0, null))
    val resumen: LiveData<ResumenInicio> = _resumen

    /** Emisión asíncrona observable por Compose a través de LiveData. */
    private fun publicarResumen() {
        viewModelScope.launch {
            _resumen.value = ResumenInicio(
                totalClientes = registroClientes.cantidadRegistrada(),
                totalPedidos = jornada.cantidadRegistrada(),
                ultimoCliente = registroClientes.ultimoClienteRegistrado()?.nombre
            )
        }
    }

    fun totalPizzas() = cantidades.values.sum()

    fun validarPasoCliente(): Boolean {
        mostrarErroresCliente = true
        val error = erroresCliente.primerError()
        if (error != null) {
            aviso = error
            return false
        }
        return true
    }

    fun validarPasoPizzas(): Boolean {
        mostrarErrorPizzas = true
        if (!esCantidadValida(totalPizzas())) {
            aviso = Constantes.MENSAJE_CANTIDAD_TOTAL
            return false
        }
        return true
    }

    fun navegarA(destino: Int) {
        when (destino) {
            0, 3 -> pagina = destino // El panel es de consulta y no exige un pedido en curso.
            1 -> {
                if (pagina == 2 || validarPasoCliente()) pagina = 1
                else pagina = 0
            }
            2 -> {
                if (!validarPasoCliente()) { pagina = 0; return }
                if (!validarPasoPizzas()) { pagina = 1; return }
                pagina = 2
            }
        }
    }

    fun confirmarPedido() {
        val nombreLimpio = nombre.trim()
        val direccionLimpia = direccion.trim()
        val telefonoLimpio = telefono.trim()
        val correoLimpio = correo.trim()

        if (!validarPasoCliente()) { pagina = 0; return }
        if (!validarPasoPizzas()) { pagina = 1; return }

        val promocion = if (usarPromocion) {
            val fecha = parsearFecha(fechaPromocion)
            if (fecha == null) {
                mostrarErrorFecha = true
                aviso = Constantes.MENSAJE_FECHA_INVALIDA
                return
            }
            mostrarErrorFecha = false
            PromocionesService.obtenerPromocion(fecha)
        } else null

        try {
            val cliente = Cliente(
                nombreLimpio,
                direccionLimpia,
                if (telefonoLimpio.isBlank()) "No informado" else formatearTelefono(telefonoLimpio),
                correoLimpio.takeIf { it.isNotEmpty() }
            )
            registroClientes.registrar(cliente)
            val pedido = Pedido(
                id = Constantes.ID_INICIAL_PEDIDOS + jornada.cantidadRegistrada(),
                cliente = cliente,
                cantidades = cantidades.toMap(),
                notas = notas.trim().takeIf { it.isNotEmpty() },
                promocion = promocion
            )

            val evaluacion = inventario.confirmarPedido(pedido.cantidadesPorProducto)
            if (evaluacion.confirmado) {
                repartidores.asignarMasCercano(zona)?.let { pedido.asignarRepartidor(it) }
                aviso = "Pedido #${pedido.id} confirmado. " +
                    (if (pedido.repartidor == null) "Esperando repartidor."
                    else "Repartidor: ${pedido.repartidor?.nombre}.")
            } else {
                pedido.cambiarEstado(EstadoPedido.RECHAZADO)
                aviso = "Pedido #${pedido.id} rechazado: faltan " +
                    evaluacion.faltantes.entries.joinToString { "${it.key} (${it.value})" }
            }
            jornada.registrar(pedido, if (evaluacion.confirmado) zona else null)
            ultimoPedido = pedido
            cantidades.clear()
            mostrarErroresCliente = false
            mostrarErrorPizzas = false
            mostrarErrorFecha = false
            revision++
            publicarResumen()
            pagina = 3
            panelVista = 0
        } catch (error: IllegalArgumentException) {
            aviso = error.message ?: "El pedido no es válido."
        } catch (error: IllegalStateException) {
            aviso = error.message ?: "No se pudo completar el pedido."
        }
    }

    fun avanzarEstado() {
        val pedido = ultimoPedido ?: return
        try {
            when (pedido.estado) {
                EstadoPedido.EN_PREPARACION -> pedido.cambiarEstado(EstadoPedido.LISTO)
                EstadoPedido.LISTO -> {
                    if (pedido.repartidor == null) {
                        val zonaPedido = jornada.zonaDePedido(pedido.id) ?: "Centro"
                        repartidores.asignarMasCercano(zonaPedido)?.let { pedido.asignarRepartidor(it) }
                    }
                    val asignado = pedido.repartidor
                    if (asignado == null) {
                        aviso = "No hay repartidores disponibles. El pedido sigue listo."
                        return
                    }
                    asignado.entregarPedido(pedido)
                }
                EstadoPedido.EN_CAMINO -> pedido.cambiarEstado(EstadoPedido.ENTREGADO)
                EstadoPedido.ENTREGADO, EstadoPedido.RECHAZADO -> return
            }
            aviso = "Pedido #${pedido.id}: ${pedido.estado.descripcion}"
            revision++
        } catch (error: IllegalArgumentException) {
            aviso = error.message ?: "Transición inválida."
        } catch (error: IllegalStateException) {
            aviso = error.message ?: "No se pudo actualizar el pedido."
        }
    }


}
