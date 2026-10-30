
fun main() {
    // ==========================================
    // DONNA LUNA PIZZERIA
    // Semana 1 - Fundamentos de Kotlin
    // ==========================================

    // Símbolo utilizado al mostrar precios en pesos.
    val moneda = '$'

    // 1. DATOS DEL CLIENTE
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

    val telefono: String = if (
        telefonoIngresado.matches(Regex("""\+?[0-9]{8,15}"""))
    ) {
        telefonoIngresado
    } else {
        println(
            "Aviso: teléfono inválido. " +
                    "Se utilizará un valor predeterminado."
        )
        "No informado"
    }

    // 2. CATÁLOGO DE PIZZAS
    val menuPizzas: List<Pair<String, Double>> = listOf(
        "Italiana" to 8500.0,
        "Margarita" to 7500.0,
        "Pepperoni" to 9500.0,
        "Cuatro Quesos" to 10500.0,
        "Hawaiana" to 9000.0
    )

    // Inventario inicial de la pizzería.
    var ingredientesDisponibles = 20
    val stockInicial = ingredientesDisponibles

    // 3. DATOS DEL PEDIDO
    val idPedido = 1001

    // Almacena cada variedad y su cantidad.
    val pizzasPedidas: MutableMap<Int, Int> = linkedMapOf()

    println("\n=================================")
    println("       DONNA LUNA PIZZERIA       ")
    println("=================================")
    println("      REGISTRO DEL PEDIDO        ")

    // 4. SELECCIÓN MÚLTIPLE DE PIZZAS
    while (true) {
        println("\n========= MENÚ DE PIZZAS =========")

        for ((indice, pizza) in menuPizzas.withIndex()) {
            val numero = indice + 1
            val nombrePizza = pizza.first
            val precioUnitario = pizza.second

            println("$numero. $nombrePizza - $moneda$precioUnitario")
        }

        println("---------------------------------")
        println("0. Finalizar pedido")
        println("---------------------------------")

        print("Seleccione una pizza (0-5): ")
        val entradaOpcion = readlnOrNull()

        if (entradaOpcion == null) {
            println("Entrada finalizada.")
            break
        }

        val opcionPizza = entradaOpcion.trim().toIntOrNull()
        if (opcionPizza == 0) {
            break
        }

        // Opción inválida: volver al menú sin añadir pizzas.
        val opcionValida = if (
            opcionPizza != null &&
            opcionPizza in 1..menuPizzas.size
        ) {
            opcionPizza
        } else {
            println(
                "Aviso: opción inválida. " +
                        "Seleccione un número entre 0 y 5."
            )
            continue
        }

        val pizzaSeleccionada = menuPizzas[opcionValida - 1]
        val nombrePizza = pizzaSeleccionada.first
        val precioUnitario = pizzaSeleccionada.second

        println("\nPizza seleccionada: $nombrePizza")
        println("Precio unitario: $moneda$precioUnitario")

        // 5. VALIDACIÓN DE CANTIDADES
        print("Ingrese la cantidad de pizzas: ")
        var cantidad: Int? = null

        while (cantidad == null) {
            val entradaCantidad = readlnOrNull()

            if (entradaCantidad == null) {
                println(
                    "Entrada finalizada. " +
                            "No se agregó esta selección."
                )
                break
            }

            val cantidadIngresada = entradaCantidad.trim().toIntOrNull()
            if (cantidadIngresada != null && cantidadIngresada > 0) {
                cantidad = cantidadIngresada
            } else {
                println(
                    "Aviso: cantidad inválida. " +
                            "Ingrese un número entero mayor que 0."
                )
                print("Ingrese la cantidad de pizzas: ")
            }
        }

        val cantidadConfirmada = cantidad ?: break

        // 6. ACUMULACIÓN DE PIZZAS
        val cantidadAnterior = pizzasPedidas[opcionValida] ?: 0
        if (cantidadConfirmada > Int.MAX_VALUE - cantidadAnterior) {
            println(
                "Cantidad demasiado grande. " +
                        "No se agregaron estas pizzas."
            )
            continue
        }

        pizzasPedidas[opcionValida] = cantidadAnterior + cantidadConfirmada

        println("\n✓ $cantidadConfirmada pizza(s) $nombrePizza agregada(s).")
        println("Puede seleccionar otra variedad.")
    }

    // 7. VERIFICACIÓN DE PEDIDO VACÍO
    if (pizzasPedidas.isEmpty()) {
        println("\nNo se registraron pizzas.")
        println("Pedido finalizado sin productos.")
        return
    }

    // 8. CÁLCULO DEL SUBTOTAL
    val cantidadPizzas: Long = pizzasPedidas.values.sumOf { it.toLong() }

    val subtotal: Double = pizzasPedidas.entries.sumOf { (opcion, cantidad) ->
        val precioUnitario = menuPizzas[opcion - 1].second
        precioUnitario * cantidad
    }

    // 9. CÁLCULO DEL DESCUENTO
    // El 10% aplica cuando se superan 3 pizzas.
    val porcentajeDescuento = if (cantidadPizzas > 3) 10 else 0
    val descuentoAplicado = subtotal * porcentajeDescuento / 100
    val totalPedido: Double = subtotal - descuentoAplicado

    // 10. ESTADO Y REPARTIDOR
    // Se determinan al comprobar el stock.
    val estadoPedido: String
    val repartidorAsignado: String

    // 11. VERIFICACIÓN Y ACTUALIZACIÓN DE STOCK
    // Se considera una unidad de ingrediente por pizza.
    // El inventario solo cambia si hay stock suficiente.
    val mensajeInventario: String = if (
        cantidadPizzas <= ingredientesDisponibles.toLong()
    ) {
        ingredientesDisponibles -= cantidadPizzas.toInt()
        estadoPedido = "Preparación"
        repartidorAsignado = "Carlos"
        "Pedido confirmado. Ingredientes suficientes."
    } else {
        estadoPedido = "Rechazado"
        repartidorAsignado = "Sin asignar"
        "Pedido no completado. Ingredientes insuficientes."
    }

    // 12. RESUMEN DEL PEDIDO
    println("\n=================================")
    println("       DONNA LUNA PIZZERIA       ")
    println("=================================")

    println("\nCLIENTE")
    println("Nombre: $nombre")
    println("Dirección: $direccion")
    println("Teléfono: $telefono")

    println("\nPIZZAS SOLICITADAS")
    println("---------------------------------")
    for ((opcion, cantidad) in pizzasPedidas) {
        val nombrePizza = menuPizzas[opcion - 1].first
        val precioUnitario = menuPizzas[opcion - 1].second
        val subtotalPizza = precioUnitario * cantidad

        println("Pizza: $nombrePizza")
        println("Precio unitario: $moneda$precioUnitario")
        println("Cantidad: $cantidad")
        println("Subtotal: $moneda$subtotalPizza")
        println("---------------------------------")
    }

    println("\nPEDIDO")
    println("ID: $idPedido")
    println("Cantidad total de pizzas: $cantidadPizzas")
    println("Subtotal: $moneda$subtotal")
    println("Descuento: $porcentajeDescuento%")
    println("Monto descontado: $moneda$descuentoAplicado")
    println("Total final: $moneda$totalPedido")
    println("Estado: $estadoPedido")
    println("Repartidor: $repartidorAsignado")

    // 13. RESUMEN DEL INVENTARIO
    println("\nINVENTARIO")
    println("Ingredientes iniciales: $stockInicial")
    println("Ingredientes disponibles: $ingredientesDisponibles")
    println("Resultado: $mensajeInventario")

    if (estadoPedido == "Rechazado") {
        println(
            "Aviso: el total es referencial. " +
                    "El pedido no fue confirmado."
        )
    }

    println("\n=================================")
    println("   GRACIAS POR ELEGIR DONNA LUNA ")
    println("=================================")
}
