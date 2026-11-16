package com.donnaluna.pizzeriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import utils.desglosarIvaIncluido
import utils.esCorreoValido
import utils.esTelefonoValido
import utils.formatearPesos
import utils.formatearTelefono
import utils.parsearFecha
import utils.esCantidadValida

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colores = if (isSystemInDarkTheme()) {
                darkColorScheme(
                    primary = Color(0xFFE7C8FF),
                    onPrimary = Color(0xFF2C123E),
                    secondary = Color(0xFFF1B27A),
                    onSecondary = Color(0xFF44210A),
                    tertiary = Color(0xFFFFE08C),
                    background = Color(0xFF17111D),
                    surface = Color(0xFF241B2D),
                    surfaceVariant = Color(0xFF32253F),
                    onSurface = Color(0xFFF6EEF8),
                    onSurfaceVariant = Color(0xFFD8C7E2)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF6D4593),
                    onPrimary = Color.White,
                    secondary = Color(0xFFE8A46C),
                    onSecondary = Color(0xFF4B250A),
                    tertiary = Color(0xFFF9D56E),
                    background = Color(0xFFF8F3FA),
                    surface = Color(0xFFFFFCFF),
                    surfaceVariant = Color(0xFFF0E8F5),
                    onSurface = Color(0xFF261D2D),
                    onSurfaceVariant = Color(0xFF6D6177)
                )
            }
            MaterialTheme(colorScheme = colores) {
                PantallaDonnaLunaPremium()
            }
        }
    }
}

@Composable
private fun PantallaDonnaLunaPremium() {
    val inventario = remember { InventarioService(Constantes.STOCK_INICIAL_INGREDIENTE) }
    val registroClientes = remember { RegistroClientesService() }
    val repartidores = remember { RepartidorService() }
    val jornada = remember { JornadaService() }
    val menu = remember { CatalogoPizzas.menu }
    val cantidades = remember { mutableStateMapOf<Producto, Int>() }

    var nombre by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var notas by rememberSaveable { mutableStateOf("") }
    var zona by rememberSaveable { mutableStateOf("Centro") }
    var usarPromocion by rememberSaveable { mutableStateOf(false) }
    var fechaPromocion by rememberSaveable { mutableStateOf(Constantes.FECHA_PREDETERMINADA_PROMOCION) }
    var aviso by remember { mutableStateOf("Bienvenida a Donna Luna. Completa tus datos para comenzar.") }
    var ultimoPedido by remember { mutableStateOf<Pedido?>(null) }
    var revision by remember { mutableIntStateOf(0) }
    var pagina by rememberSaveable { mutableIntStateOf(0) }
    var panelVista by rememberSaveable { mutableIntStateOf(0) }

    fun totalPizzas() = cantidades.values.sum()

    fun confirmarPedido() {
        val nombreLimpio = nombre.trim()
        val direccionLimpia = direccion.trim()
        val telefonoLimpio = telefono.trim()
        val correoLimpio = correo.trim()
        val total = totalPizzas()

        if (nombreLimpio.isBlank()) { aviso = "Ingresa el nombre del cliente."; return }
        if (direccionLimpia.length < 4 || direccionLimpia.none { it.isLetter() } || '@' in direccionLimpia) {
            aviso = "Ingresa una dirección física válida (mínimo 4 caracteres)."; return
        }
        if (telefonoLimpio.isNotBlank() && !esTelefonoValido(telefonoLimpio)) {
            aviso = "Teléfono inválido. Usa 9 o 10 dígitos nacionales, o +56 seguido del número."; return
        }
        if (correoLimpio.isNotBlank() && !esCorreoValido(correoLimpio)) {
            aviso = Constantes.MENSAJE_CORREO_INVALIDO; return
        }
        if (!esCantidadValida(total)) {
            aviso = Constantes.MENSAJE_CANTIDAD_TOTAL; return
        }

        val promocion = if (usarPromocion) {
            val fecha = parsearFecha(fechaPromocion)
            if (fecha == null) { aviso = Constantes.MENSAJE_FECHA_INVALIDA; return }
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
            revision++
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


    val secciones = listOf(
        BarraItem("Cliente", "👤"),
        BarraItem("Pizzas", "🍕"),
        BarraItem("Entrega", "🛵"),
        BarraItem("Panel", "📊")
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "☾ Donna Luna Pizzeria",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                "Bienvenidos a la mejor pizzería de la ciudad",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            "✦ ☾ ✦",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                secciones.forEachIndexed { indice, item ->
                    NavigationBarItem(
                        selected = pagina == indice,
                        onClick = { pagina = indice },
                        icon = { Text(item.icono, style = MaterialTheme.typography.titleLarge) },
                        label = { Text(item.titulo, maxLines = 1) },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { margenes ->
        key(pagina, panelVista, revision) {
            Column(
                modifier = Modifier.fillMaxSize().padding(margenes)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BarraDePaso(pagina)
                when (pagina) {
                    0 -> {
                        TituloBloque(
                            etiqueta = "PASO 1 DE 3",
                            titulo = "Datos del cliente",
                            descripcion = "Cuéntanos dónde llevar tus pizzas."
                        )
                        TarjetaDonna(destacada = true) {
                            Text(
                                "Datos principales",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(direccion, { direccion = it }, label = { Text("Dirección *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(
                                telefono, { telefono = it }, label = { Text("Teléfono (opcional)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true, modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                correo, { correo = it }, label = { Text("Correo (opcional)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true, modifier = Modifier.fillMaxWidth()
                            )
                        }
                        MensajeDonna(aviso)
                        Button(onClick = { pagina = 1 }, modifier = Modifier.fillMaxWidth()) {
                            Text("Continuar al menú de pizzas")
                        }
                    }

                    1 -> {
                        TituloBloque(
                            etiqueta = "PASO 2 DE 3",
                            titulo = "Elige tus pizzas",
                            descripcion = "Elige entre nuestras cinco especialidades."
                        )
                        TarjetaDonna(destacada = true) {
                            Text(
                                "✦ Especialidades de Donna Luna",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Precios con IVA incluido. Más de 3 pizzas = 10 % de descuento.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        for (pizza in menu) {
                            val cantidad = cantidades[pizza] ?: 0
                            TarjetaPizza(pizza = pizza, cantidad = cantidad,
                                onMenos = { if (cantidad <= 1) cantidades.remove(pizza) else cantidades[pizza] = cantidad - 1 },
                                onMas = {
                                    if (esCantidadValida(totalPizzas() + 1)) cantidades[pizza] = cantidad + 1
                                },
                                puedeSumar = esCantidadValida(totalPizzas() + 1)
                            )
                        }
                        TarjetaDonna(destacada = true) {
                            FilaEtiquetaValor("Pizzas seleccionadas", "${totalPizzas()} / ${Constantes.CANTIDAD_MAXIMA}")
                            Text(
                                if (totalPizzas() > 3) "Descuento por volumen aplicado al confirmar."
                                else "Suma más de 3 pizzas para activar el descuento.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Button(onClick = { pagina = 2 }, modifier = Modifier.fillMaxWidth()) {
                            Text("Continuar a entrega y confirmación")
                        }
                    }

                    2 -> {
                        TituloBloque(
                            etiqueta = "PASO 3 DE 3",
                            titulo = "Entrega y confirmación",
                            descripcion = "Ajusta los últimos detalles antes de enviar tu pedido."
                        )
                        TarjetaDonna(destacada = true) {
                            Text("Zona de entrega", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                listOf("Centro", "Norte", "Costa").forEach { opcion ->
                                    FilterChip(selected = zona == opcion, onClick = { zona = opcion }, label = { Text(opcion) })
                                }
                            }
                            OutlinedTextField(notas, { notas = it }, label = { Text("Notas para el pedido (opcional)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Evaluar promoción del 5 % por fecha", modifier = Modifier.weight(1f))
                                Switch(checked = usarPromocion, onCheckedChange = { usarPromocion = it })
                            }
                            if (usarPromocion) {
                                OutlinedTextField(fechaPromocion, { fechaPromocion = it }, label = { Text("Fecha ${Constantes.FORMATO_FECHA_TEXTO}") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                Text(
                                    Constantes.MENSAJE_VIGENCIA_PROMOCION,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        TarjetaDonna {
                            Text("Resumen antes de confirmar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            FilaEtiquetaValor("Cliente", nombre.ifBlank { "Sin completar" })
                            FilaEtiquetaValor("Destino", zona)
                            FilaEtiquetaValor("Pizzas", totalPizzas().toString())
                            cantidades.forEach { (producto, cantidad) ->
                                if (cantidad > 0) FilaEtiquetaValor("• ${producto.nombre}", "x$cantidad")
                            }
                        }
                        Button(onClick = { confirmarPedido() }, modifier = Modifier.fillMaxWidth()) {
                            Text("Confirmar pedido")
                        }
                        MensajeDonna(aviso)
                    }

                    else -> {
                        TituloBloque(
                            etiqueta = "PANEL DE CONTROL",
                            titulo = "Seguimiento y gestión",
                            descripcion = "Consulta el pedido, la jornada y el inventario."
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            listOf("Pedido", "Jornada", "Stock").forEachIndexed { indice, titulo ->
                                FilterChip(selected = panelVista == indice, onClick = { panelVista = indice }, label = { Text(titulo) })
                            }
                        }
                        when (panelVista) {
                            0 -> {
                                MensajeDonna(aviso)
                                val pedido = ultimoPedido
                                if (pedido == null) {
                                    TarjetaDonna {
                                        Text("Todavía no hay pedidos registrados.")
                                        Text("Crea uno desde las pestañas Cliente, Pizzas y Entrega.", style = MaterialTheme.typography.bodySmall)
                                    }
                                } else {
                                    TarjetaDonna(destacada = true) {
                                        Text("Pedido #${pedido.id}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                        EstadoVisualPedido(pedido.estado)
                                        Text("Cliente: ${pedido.cliente.nombre}")
                                        HorizontalDivider()
                                        pedido.cantidadesPorProducto.forEach { (producto, cantidad) ->
                                            FilaEtiquetaValor(producto.nombre, "x$cantidad")
                                        }
                                        val importes = pedido.detalleImportes()
                                        HorizontalDivider()
                                        FilaEtiquetaValor("Subtotal", formatearPesos(importes.subtotal))
                                        FilaEtiquetaValor("Descuento por volumen", "−${formatearPesos(importes.descuentoVolumen)}")
                                        FilaEtiquetaValor("Descuento promocional", "−${formatearPesos(importes.descuentoPromocion)}")
                                        val iva = desglosarIvaIncluido(importes.total)
                                        FilaEtiquetaValor("Neto", formatearPesos(iva.baseNeta))
                                        FilaEtiquetaValor("IVA incluido", formatearPesos(iva.ivaIncluido))
                                        HorizontalDivider()
                                        Text(
                                            if (pedido.estado == EstadoPedido.RECHAZADO) "TOTAL REFERENCIAL (SIN COBRO)" else "TOTAL FINAL",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            formatearPesos(importes.total),
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text("Repartidor: ${pedido.repartidor?.nombre ?: "Sin asignar"}")
                                        if (pedido.estado in listOf(EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO, EstadoPedido.EN_CAMINO)) {
                                            Button(onClick = { avanzarEstado() }, modifier = Modifier.fillMaxWidth()) {
                                                Text("Avanzar estado del pedido")
                                            }
                                        }
                                    }
                                }
                            }
                            1 -> {
                                TarjetaDonna(destacada = true) {
                                    Text("Reporte del día", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    ResumenJornada(
                                        pedidos = jornada.cantidadRegistrada(),
                                        completados = jornada.completados().size,
                                        pendientes = jornada.pendientes().size,
                                        rechazados = jornada.rechazados().size
                                    )
                                    HorizontalDivider()
                                    val pedidos = jornada.pendientes() + jornada.completados() + jornada.rechazados()
                                    if (pedidos.isEmpty()) Text("Aún no hay pedidos en esta jornada.")
                                    pedidos.forEach { pedido ->
                                        Text("#${pedido.id} · ${pedido.cliente.nombre} · ${pedido.estado.descripcion}")
                                    }
                                }
                            }
                            else -> {
                                TarjetaDonna(destacada = true) {
                                    Text("Inventario disponible", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Text(
                                        "Stock inicial: ${inventario.stockInicial} porciones por ingrediente",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    HorizontalDivider()
                                    inventario.consultarExistencias().forEach { (ingrediente, unidades) ->
                                        ItemInventario(ingrediente, unidades, inventario.stockInicial)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private data class BarraItem(val titulo: String, val icono: String)

@Composable
private fun BarraDePaso(pagina: Int) {
    if (pagina > 2) return
    val pasos = listOf("Cliente", "Pizzas", "Entrega")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pasos.forEachIndexed { indice, titulo ->
            val activo = indice == pagina
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(999.dp),
                color = if (activo) MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (activo) MaterialTheme.colorScheme.secondary else Color.Transparent
                )
            ) {
                Text(
                    text = if (activo) "✦ ${indice + 1}. $titulo" else "${indice + 1}. $titulo",
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (activo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TituloBloque(etiqueta: String, titulo: String, descripcion: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = "✦ $etiqueta",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TarjetaDonna(destacada: Boolean = false, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (destacada) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface
        ),
        border = if (destacada) androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f)
        ) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (destacada) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = contenido
        )
    }
}

@Composable
private fun TarjetaPizza(
    pizza: Producto,
    cantidad: Int,
    onMenos: () -> Unit,
    onMas: () -> Unit,
    puedeSumar: Boolean
) {
    TarjetaDonna(destacada = cantidad > 0) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(id = imagenPizza(pizza.nombre)),
                contentDescription = "Fotografía ilustrativa de pizza ${pizza.nombre}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(92.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.75f), RoundedCornerShape(14.dp))
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(pizza.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    resumenIngredientes(pizza),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    formatearPesos(pizza.precio),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Unidades", style = MaterialTheme.typography.labelLarge)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalIconButton(modifier = Modifier.size(40.dp), onClick = onMenos, enabled = cantidad > 0) {
                    Text("−", fontWeight = FontWeight.Bold)
                }
                Text(cantidad.toString(), modifier = Modifier.widthIn(min = 26.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                FilledTonalIconButton(modifier = Modifier.size(40.dp), onClick = onMas, enabled = puedeSumar) {
                    Text("+", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MensajeDonna(texto: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EstadoVisualPedido(estado: EstadoPedido) {
    val pasos = listOf(
        EstadoPedido.EN_PREPARACION to "Preparación",
        EstadoPedido.LISTO to "Listo",
        EstadoPedido.EN_CAMINO to "En camino",
        EstadoPedido.ENTREGADO to "Entregado"
    )
    if (estado == EstadoPedido.RECHAZADO) {
        Surface(color = Color(0xFFF9E5E2), shape = RoundedCornerShape(12.dp)) {
            Text("Pedido rechazado", color = Color(0xFF953B2B), modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
        }
        return
    }
    val indiceActual = pasos.indexOfFirst { it.first == estado }.coerceAtLeast(0)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Estado: ${estado.descripcion}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            pasos.forEachIndexed { indice, item ->
                val alcanzado = indice <= indiceActual
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = if (alcanzado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(999.dp)
                    ) {}
                    Text(
                        if (indice == indiceActual) "✦ ${item.second}" else item.second,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (alcanzado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        fontWeight = if (indice == indiceActual) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun ResumenJornada(pedidos: Int, completados: Int, pendientes: Int, rechazados: Int) {
    TarjetaMiniStat("Pedidos", pedidos.toString())
    TarjetaMiniStat("Completados", completados.toString())
    TarjetaMiniStat("Pendientes", pendientes.toString())
    TarjetaMiniStat("Rechazados", rechazados.toString())
}

@Composable
private fun TarjetaMiniStat(titulo: String, valor: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(titulo, fontWeight = FontWeight.Medium)
            Text(valor, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ItemInventario(ingrediente: String, unidades: Long, stockInicial: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        FilaEtiquetaValor(ingrediente, "$unidades porciones")
        BarraStock(unidades, stockInicial)
    }
}

@Composable
private fun BarraStock(actual: Long, maximo: Int) {
    val proporcion = if (maximo <= 0) 0f else (actual.toFloat() / maximo.toFloat()).coerceIn(0f, 1f)
    val color = when {
        proporcion > 0.66f -> Color(0xFF58A55C)
        proporcion > 0.33f -> MaterialTheme.colorScheme.secondary
        else -> Color(0xFFC55B5B)
    }
    Box(
        modifier = Modifier.fillMaxWidth().height(8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(999.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(proporcion).height(8.dp)
                .background(color, RoundedCornerShape(999.dp))
        )
    }
}

@Composable
private fun FilaEtiquetaValor(titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(titulo, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End)
    }
}

private fun imagenPizza(nombre: String): Int = when (nombre.lowercase()) {
    "italiana" -> R.drawable.pizza_italiana
    "margarita" -> R.drawable.pizza_margarita
    "pepperoni" -> R.drawable.pizza_pepperoni
    "cuatro quesos" -> R.drawable.pizza_cuatro_quesos
    "hawaiana" -> R.drawable.pizza_hawaiana
    else -> R.drawable.pizza_margarita
}

/** Se describe la receta real del catálogo para no inventar ingredientes en la interfaz. */
private fun resumenIngredientes(pizza: Producto): String =
    pizza.ingredientes.keys.filterNot { it == "Masa" || it == "Salsa de tomate" }
        .joinToString(" · ")
