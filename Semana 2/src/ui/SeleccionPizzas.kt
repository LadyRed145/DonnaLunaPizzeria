package ui

import modelo.Producto
import utils.formatearPesos

/** Captura las pizzas seleccionadas sin cambiar los mensajes ni reglas de S1. */
fun registrarSelecciones(menuPizzas: List<Producto>): Map<Producto, Int> {
    val pizzasPedidas: MutableMap<Int, Int> = linkedMapOf()

    println("\n=================================")
    println("       DONNA LUNA PIZZERIA       ")
    println("=================================")
    println("      REGISTRO DEL PEDIDO        ")

    while (true) {
        println("\n========= MENÚ DE PIZZAS =========")

        for ((indice, pizza) in menuPizzas.withIndex()) {
            val numero = indice + 1
            val nombrePizza = pizza.nombre
            val precioUnitario = pizza.precio
            println("$numero. $nombrePizza - ${formatearPesos(precioUnitario)}")
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
        if (opcionPizza == 0) break

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
        val nombrePizza = pizzaSeleccionada.nombre
        val precioUnitario = pizzaSeleccionada.precio

        println("\nPizza seleccionada: $nombrePizza")
        println("Precio unitario: ${formatearPesos(precioUnitario)}")

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

            // El try-catch controla texto no numérico, desbordamientos y cantidades negativas.
            try {
                val cantidadIngresada = entradaCantidad.trim().toInt()
                require(cantidadIngresada > 0) { "La cantidad debe ser positiva" }
                cantidad = cantidadIngresada
            } catch (e: IllegalArgumentException) {
                println(
                    "Aviso: cantidad inválida. " +
                            "Ingrese un número entero mayor que 0."
                )
                print("Ingrese la cantidad de pizzas: ")
            }
        }

        val cantidadConfirmada = cantidad ?: break
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

    val cantidades = linkedMapOf<Producto, Int>()
    for ((opcion, cantidad) in pizzasPedidas) {
        cantidades[menuPizzas[opcion - 1]] = cantidad
    }
    return cantidades
}
