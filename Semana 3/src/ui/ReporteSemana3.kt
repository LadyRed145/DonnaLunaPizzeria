package ui

import java.time.LocalDate
import modelo.Cliente
import modelo.Pedido
import modelo.Producto
import modelo.Usuario
import servicios.CatalogoPizzas
import servicios.PromocionesService
import servicios.ReflexionService
import utils.formatearPesos

/** Demostración sin venta, sin descontar inventario ni registrar pedidos de prueba en la jornada. */
fun imprimirDemostracionSemana3() {
    println("\n========= DEMOSTRACIÓN KOTLIN — SEMANA 3 =========")
    val cliente = Cliente("Cliente de prueba", "Dirección de prueba", "+56 (9) 1234-5678", "demo@donnaluna.cl")
    val pedidoA = Pedido(8101, cliente, mapOf(CatalogoPizzas.margarita to 2))
    val pedidoB = Pedido(8102, cliente, mapOf(CatalogoPizzas.margarita to 2, CatalogoPizzas.pepperoni to 1))
    val combinado = pedidoA + pedidoB
    println("Operador +: pedidos #${pedidoA.id} + #${pedidoB.id} => #${combinado.id}")
    println("Productos combinados: ${combinado.cantidadesPorProducto.entries.joinToString { "${it.key.nombre} x${it.value}" }}")
    println("Total combinado con descuento: ${formatearPesos(combinado.calcularTotal())}")

    // Los métodos component1/2/3 del Cliente se demuestran mediante desestructuración directa.
    val (nombreCliente, correoCliente, telefonoCliente) = cliente
    println("Cliente desestructurado: $nombreCliente | ${correoCliente ?: "Sin correo"} | $telefonoCliente")
    val (clientePedido, productosPedido, totalPedido) = combinado
    println("Pedido desestructurado: ${clientePedido.nombre} | ${productosPedido.size} variedades | ${formatearPesos(totalPedido)}")

    val copiaCliente = Cliente(" CLIENTE DE PRUEBA ", "Otra dirección", "otra", "DEMO@DONNALUNA.CL")
    val copiaProducto = Producto(" MARGARITA ", CatalogoPizzas.margarita.precio, CatalogoPizzas.margarita.ingredientes)
    println("Igualdad de clientes (equals/hashCode): ${cliente == copiaCliente} / ${cliente.hashCode() == copiaCliente.hashCode()}")
    println("Igualdad de productos (equals/hashCode): ${CatalogoPizzas.margarita == copiaProducto} / ${CatalogoPizzas.margarita.hashCode() == copiaProducto.hashCode()}")

    val elegibles = PromocionesService.detectarProductosAnotados()
    println("@Promocionable por reflection: ${elegibles.joinToString { it.nombre }}")
    val promo = PromocionesService.obtenerPromocion(LocalDate.of(2026, 11, 15))
    val conPromo = Pedido(8103, cliente, mapOf(CatalogoPizzas.margarita to 2), promocion = promo)
    println("Ejemplo fecha dentro del rango: ${formatearPesos(conPromo.calcularTotal())} (subtotal ${formatearPesos(conPromo.calcularSubtotal())})")
    println("Fecha fuera del rango: ${PromocionesService.obtenerPromocion(LocalDate.of(2026, 12, 1)) == null}")
    println(ReflexionService.describirClase(Pedido::class.java))
    println(ReflexionService.describirClase(Producto::class.java))
    val roles: List<Usuario> = listOf(cliente)
    println("Polimorfismo: ${roles.joinToString { it.describirRol() }}")
    println("Detalle producto: ${CatalogoPizzas.margarita.detallarProducto()}")
    println("Esta demostración NO altera la jornada ni el inventario.")
}
