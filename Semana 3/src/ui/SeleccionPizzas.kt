package ui

import modelo.Producto
import utils.Constantes
import utils.esCantidadValida
import utils.formatearPesos

/** Captura cantidades dentro del rango [1,100]; null indica cancelación por fin de entrada. */
fun registrarSelecciones(menuPizzas: List<Producto>): Map<Producto, Int>? {
    val pizzasPedidas: MutableMap<Int, Int> = linkedMapOf()
    println("\n=================================")
    println("       DONNA LUNA PIZZERIA       ")
    println("=================================")
    println("      REGISTRO DEL PEDIDO        ")

    while (true) {
        println("\n========= MENÚ DE PIZZAS =========")
        for ((indice, pizza) in menuPizzas.withIndex()) {
            println("${indice + 1}. ${pizza.nombre} - ${formatearPesos(pizza.precio)}")
        }
        println("---------------------------------")
        println("0. Finalizar pedido")
        println("---------------------------------")
        print("Seleccione una pizza (0-${menuPizzas.size}): ")
        val entradaOpcion = readlnOrNull() ?: return cancelarSeleccion()
        val opcion = entradaOpcion.trim().toIntOrNull()
        if (opcion == null) {
            println("Aviso: opción inválida. Ingrese un número de 0 a ${menuPizzas.size}.")
            continue
        }
        if (opcion == 0) break
        if (opcion !in 1..menuPizzas.size) {
            println("Aviso: opción inválida. Elija entre 0 y ${menuPizzas.size}.")
            continue
        }

        val pizza = menuPizzas[opcion - 1]
        println("\nPizza seleccionada: ${pizza.nombre}")
        println("Precio unitario: ${formatearPesos(pizza.precio)}")
        var cantidad: Int? = null
        while (cantidad == null) {
            print("Ingrese la cantidad de pizzas (1-100): ")
            val entrada = readlnOrNull() ?: return cancelarSeleccion()
            val propuesta = entrada.trim().toIntOrNull()
            val actualPorVariedad = pizzasPedidas[opcion] ?: 0
            val totalActual = pizzasPedidas.values.sum()
            if (propuesta != null && esCantidadValida(propuesta) &&
                actualPorVariedad + propuesta <= Constantes.CANTIDAD_MAXIMA &&
                totalActual + propuesta <= Constantes.CANTIDAD_MAXIMA
            ) cantidad = propuesta else println("Aviso: cantidad inválida; total y variedad deben quedar entre 1 y 100.")
        }
        val confirmada = cantidad
        pizzasPedidas[opcion] = (pizzasPedidas[opcion] ?: 0) + confirmada
        println("\n✓ $confirmada pizza(s) ${pizza.nombre} agregada(s).")
        println("Puede seleccionar otra variedad.")
    }
    return linkedMapOf<Producto, Int>().apply {
        pizzasPedidas.forEach { (numero, cantidad) -> put(menuPizzas[numero - 1], cantidad) }
    }
}

/** Descarta las selecciones parciales si la entrada se interrumpe. */
private fun cancelarSeleccion(): Map<Producto, Int>? {
    println("\nEntrada cerrada. Selección cancelada; no se registrará el pedido.")
    return null
}
