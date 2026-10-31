<div align="center">

# 🍕 DONNA LUNA · PIZZERÍA

### Desarrollo App Móviles I · PMY2201 · Semana 2

**Aplicación de consola en Kotlin para gestionar pedidos de pizzas, inventario por ingredientes, repartidores y reportes de una jornada.**

*Aplicando utilidades de Kotlin · Programación orientada a objetos y manejo de errores*

[📁 Repositorio DonnaLunaPizzeria](https://github.com/LadyRed145/DonnaLunaPizzeria) · [📖 Documentación de Semana 1](../Semana%201/README.md)

</div>

---

## ✦ Visión general

**Donna Luna Pizzería** es un proyecto académico desarrollado en **Kotlin**, ejecutado por consola desde **IntelliJ IDEA**. La Semana 2 continúa la aplicación de la Semana 1: conserva las cinco pizzas, sus precios y la regla de descuento, y amplía el sistema para registrar **varios pedidos por ejecución**, verificar ingredientes, asignar repartidores por zonas demostrativas y administrar estados.

El código fue reorganizado en **clases, servicios, utilidades y funciones de interfaz de consola**. Los pedidos pueden confirmarse o rechazarse por falta de ingredientes; cada uno genera un comprobante o una cotización referencial.

**Es una simulación académica:** no realiza ventas ni cobros, no envía correos reales y no utiliza GPS, bases de datos ni una interfaz gráfica Android.

| 👤 Clientes | 🍕 Catálogo | 📦 Ingredientes | 🛵 Reparto | 🧾 Jornada |
|:---:|:---:|:---:|:---:|:---:|
| Datos y validaciones | 5 pizzas | Stock por ingrediente | 3 zonas de prueba | Pedidos y reportes |

### Funcionalidades implementadas

- 👤 Registro de nombre, dirección, teléfono y correo electrónico, con validaciones y valores de respaldo.
- 🍕 Selección de distintas variedades, acumulación de cantidades y control de entradas inválidas.
- 💰 Subtotal, **descuento del 10 % al superar tres pizzas**, total y desglose del **IVA del 19 % incluido** en el precio final.
- 📦 Inventario en memoria con ingredientes por pizza, cálculo de porciones necesarias, detección de faltantes y descuento solo si el pedido puede confirmarse.
- 🛵 Repartidores asociados a las zonas demostrativas **Centro, Norte y Costa**.
- 🧾 Comprobante de consola con bordes, importes en formato chileno y distinción entre pedido confirmado y **cotización referencial sin cobro**.
- 🔄 Registro de varios pedidos durante la jornada, identificadores consecutivos y avance controlado de estados.
- 📊 Reporte con pedidos pendientes, entregados y rechazados.
- 🛡️ Manejo de entradas numéricas erróneas, valores nulos y cantidades fuera del rango admitido.

---

## 🍕 Catálogo de pizzas

Los productos se definen en `src/servicios/CatalogoPizzas.kt` como objetos `Producto`.

| N.º | Pizza | Precio unitario |
|---:|---|---:|
| 1 | Italiana | $8.500 |
| 2 | Margarita | $7.500 |
| 3 | Pepperoni | $9.500 |
| 4 | Cuatro Quesos | $10.500 |
| 5 | Hawaiana | $9.000 |

El usuario elige una pizza, indica su cantidad y puede seguir agregando variedades antes de finalizar con la opción **0**. Si repite una variedad, se acumulan sus unidades.

### Ingredientes de demostración

Las recetas utilizan **una porción por ingrediente y pizza**, como unidad didáctica. **No son recetas gastronómicas ni cantidades reales**.

| Pizza | Ingredientes registrados |
|---|---|
| Italiana | Masa, salsa de tomate, mozzarella, jamón, tomate |
| Margarita | Masa, salsa de tomate, mozzarella, albahaca |
| Pepperoni | Masa, salsa de tomate, mozzarella, pepperoni |
| Cuatro Quesos | Masa, salsa de tomate, mozzarella, cheddar, queso azul, parmesano |
| Hawaiana | Masa, salsa de tomate, mozzarella, jamón, piña |

---

## 📦 Inventario por ingredientes

`InventarioService.kt` inicializa **20 porciones de cada ingrediente** del catálogo. El inventario utiliza un `MutableMap<String, Long>` de manera interna y expone consultas mediante copias de sus existencias.

La función `calcularIngredientesNecesarios()` suma las porciones requeridas para **todo el pedido**, incluidas las que comparten diferentes pizzas. `confirmarPedido()` evalúa el lote completo utilizando `when`:

| Condición | Acción |
|---|---|
| Todos los ingredientes alcanzan | Confirmar y descontar porciones |
| Falta uno o más ingredientes | Rechazar sin descontar ninguna porción |
| Hay faltantes | Informar disponibles, necesarios y faltantes; mostrar pasos sugeridos de reposición |

**Ejemplo:** si se piden 4 Margaritas con stock inicial de 20, quedan 16 porciones de masa, salsa, mozzarella y albahaca. Si se solicitan 21 Margaritas con el inventario completo, se informa la falta de una porción de cada uno de esos cuatro ingredientes y no se modifica el stock.

Los pasos para reabastecer se **muestran como instrucciones**: la aplicación no implementa una operación interactiva de reposición.

---

## 🛒 Cálculo de pedidos e IVA

La lógica monetaria principal está encapsulada en `modelo/Pedido.kt`; el desglose tributario se realiza en `utils/Calculos.kt`.

| Concepto | Regla implementada |
|---|---|
| Subtotal | Suma de `precio unitario × cantidad` por producto |
| Descuento | 10 % si se solicitan **más de 3 pizzas** en total; en caso contrario, 0 % |
| Total | Subtotal menos descuento |
| IVA | 19 % **incluido** en el total, no sumado otra vez |
| Base neta | Total final dividido por `1,19`, redondeado a dos decimales |
| IVA incluido | Total final menos base neta |
| Moneda | Pesos chilenos con punto de miles; desglose tributario con coma decimal |

### Ejemplo: cuatro Margaritas

| Concepto | Importe |
|---|---:|
| Subtotal | $30.000 |
| Descuento (10 %) | −$3.000 |
| Base neta del total final | $22.689,08 |
| IVA incluido (19 %) | $4.310,92 |
| **Total final** | **$27.000** |

La aplicación conserva los precios y totales de la Semana 1. El desglose usa `BigDecimal` y `RoundingMode.HALF_UP`; los cálculos originales del pedido utilizan `Double`.

---

## 🛵 Repartidores y estados

`RepartidorService.kt` registra una lista fija de tres repartidores disponibles. Utiliza **`listOf` y `find`** para seleccionar uno cuya zona coincida con la elegida por el usuario.

| Zona demostrativa | Repartidor |
|---|---|
| Centro | Carlos |
| Norte | Paula |
| Costa | Diego |

La asignación **no calcula distancias reales** ni rastrea ubicaciones. Si la zona introducida no se reconoce, el programa utiliza Centro como valor predeterminado.

### Ciclo de estados

```text
En preparación → Listo → En camino → Entregado
```

El usuario puede avanzar un pedido indicando su ID al terminar de registrar la jornada. La clase `Repartidor` implementa `entregarPedido()`, que permite pasar de `Listo` a `En camino`.

Un pedido con ingredientes insuficientes pasa a **`Rechazado`**, queda sin repartidor y **no puede despacharse**. Los pedidos ya `Entregado` tampoco avanzan nuevamente.

---

## 🧾 Voucher y cotizaciones

`src/ui/ResumenPedido.kt` genera un recuadro de texto con caracteres de consola y secciones diferenciadas:

- **Encabezado:** Donna Luna Pizzería e identificador del pedido.
- **Cliente:** nombre, dirección, teléfono y correo.
- **Pizzas solicitadas:** variedad, cantidad, precio unitario y subtotales.
- **Resumen del pedido:** subtotal, descuento, base neta, IVA incluido y total.
- **Cierre:** notas, estado y repartidor; el estado aparece en este bloque inferior.

Para un pedido rechazado, el encabezado informa **«COTIZACIÓN REFERENCIAL — VENTA NO CONFIRMADA»** y el importe se presenta como **«TOTAL REFERENCIAL (SIN COBRO)»**. No se genera un aviso simulado de confirmación.

El detalle de inventario y los posibles faltantes se imprimen **debajo del voucher**, como información operativa de la pizzería, no como parte del comprobante entregado al cliente.

La notificación a un correo válido es solo un mensaje en consola con `let`: **no existe envío real de mensajes**. Cuando el correo es inválido, se muestra `correo@invalido.com`, sin activar ese aviso.

---

## 📊 Reporte de pedidos de la jornada

`JornadaService.kt` mantiene un **`Array<Pedido>` en memoria**. Cada pedido registrado recibe un ID consecutivo a partir de `1001`, durante la ejecución actual.

El reporte recorre los pedidos con un ciclo `for` y muestra identificador, cliente, pizzas, total o cotización referencial, estado y repartidor.

Mediante `filter`, los clasifica así:

| Grupo | Estados |
|---|---|
| Pendientes | `En preparación`, `Listo`, `En camino` |
| Completados | `Entregado` |
| Rechazados | `Rechazado` |

El reporte se imprime al cerrar el registro de pedidos y vuelve a imprimirse si el usuario modifica algún estado. El inventario se comparte entre los pedidos de la misma jornada, pero **se reinicia al volver a ejecutar el programa**.

---

## ✅ Validaciones y manejo de errores

| Entrada o situación | Comportamiento implementado |
|---|---|
| Nombre o dirección vacíos | Valores `No informado` o `No informada` |
| Teléfono inválido | Valor `No informado`; admite 8 a 15 dígitos y `+` opcional |
| Correo inválido | Se conserva `null` y se muestra `correo@invalido.com` |
| Opción de pizza fuera del menú | Se informa el error y se vuelve a solicitar |
| Cantidad no numérica, negativa o cero | `try-catch` y nueva solicitud de cantidad positiva |
| Número fuera del rango `Int` | Se controla la conversión sin cerrar inesperadamente |
| Acumulación superior a `Int.MAX_VALUE` | Se rechaza esa incorporación |
| Pedido sin pizzas | No se registra un pedido con productos |
| Ingredientes insuficientes | Se rechaza sin descontar inventario |
| Notas vacías | Se muestra `Sin notas` mediante el operador `?:` |
| Repartidor ausente | Se muestra `Sin asignar` mediante acceso seguro |

Se utilizan `readlnOrNull()`, `?.`, `?:`, `takeIf`, `let`, `require`, `try-catch` y plantillas de strings. El programa contiene verificaciones de stock inicial no negativo y cantidades de ingredientes positivas.

---

## 🔄 Flujo funcional

```text
Iniciar Donna Luna Pizzería
           ↓
Registrar cliente (nombre, dirección, teléfono, correo)
           ↓
Seleccionar variedades y cantidades
           ↓
¿El pedido tiene productos?
    ├── No → Terminar esa selección sin pedido
    └── Sí
           ↓
Registrar notas opcionales
           ↓
Calcular ingredientes necesarios y validar inventario
    ├── Insuficiente → Rechazar, conservar stock
    │                   y mostrar cotización referencial
    └── Suficiente → Descontar ingredientes,
                      seleccionar zona y repartidor
           ↓
Generar voucher y mostrar inventario
           ↓
¿Registrar otro pedido?
    ├── Sí → Repetir con el inventario actualizado
    └── No
           ↓
Reporte diario y gestión opcional de estados
           ↓
Reporte actualizado (si hubo cambios) y fin
```

---

## 🧩 Arquitectura Kotlin

La versión de Semana 2 está separada en **17 archivos `.kt`**:

| Archivo o paquete | Responsabilidad |
|---|---|
| `src/Main.kt` | Coordina el registro de pedidos, el inventario y la jornada |
| `modelo/Usuario.kt` | Clase base con nombre, contacto y método `describirRol()` |
| `modelo/Cliente.kt` | Datos del cliente, correo opcional y rol especializado |
| `modelo/Repartidor.kt` | Zona, disponibilidad, rol y método `entregarPedido()` |
| `modelo/Producto.kt` | Nombre, precio, ingredientes y `detallarProducto()` |
| `modelo/Pedido.kt` | Productos, cantidades, descuentos, total, estado y repartidor |
| `servicios/CatalogoPizzas.kt` | Catálogo, precios y recetas demostrativas |
| `servicios/InventarioService.kt` | Cálculo, validación y descuento de ingredientes |
| `servicios/RepartidorService.kt` | Lista y selección de repartidores por zona |
| `servicios/JornadaService.kt` | Arreglo de pedidos, búsquedas, filtros y reportes |
| `ui/RegistroCliente.kt` | Lectura y validación de datos de cliente |
| `ui/SeleccionPizzas.kt` | Menú de pizzas, cantidades y validaciones de entrada |
| `ui/GestionJornada.kt` | Notas, zonas, repetición de pedidos y cambios de estado |
| `ui/ResumenPedido.kt` | Voucher, cotizaciones y detalle de inventario |
| `utils/Validaciones.kt` | Expresiones regulares de teléfono y correo |
| `utils/Calculos.kt` | Desglose del IVA incluido |
| `utils/FormatoMoneda.kt` | Formato de pesos chilenos |

### Recursos de Kotlin aplicados

| Recurso | Aplicación |
|---|---|
| `val`, `var`, tipos y conversiones | Datos de entidades y estado mutable |
| `if`, `when`, `while`, `for` | Validaciones, inventario, menús y reportes |
| `Array`, `Map`, `MutableMap`, `List`, `listOf`, `setOf` | Pedidos, ingredientes, catálogo y repartidores |
| `filter`, `find`, `sumOf`, `distinct`, `associateWith` | Consultas y transformación de colecciones |
| Clases, objetos, herencia y polimorfismo | `Usuario`, `Cliente`, `Repartidor`, `Pedido` y `Producto` |
| Métodos y funciones reutilizables | Totales, inventario, estados y formato |
| `try-catch`, `require`, `?.`, `?:`, `let` | Manejo de errores y nulos |
| Plantillas de strings | Menús, avisos, voucher y reporte diario |

### Organización del código fuente

```text
DonnaLunaPizzeria/
├── README.md
├── src/
│   ├── Main.kt
│   ├── modelo/
│   │   ├── Usuario.kt
│   │   ├── Cliente.kt
│   │   ├── Repartidor.kt
│   │   ├── Producto.kt
│   │   └── Pedido.kt
│   ├── servicios/
│   │   ├── CatalogoPizzas.kt
│   │   ├── InventarioService.kt
│   │   ├── RepartidorService.kt
│   │   └── JornadaService.kt
│   ├── ui/
│   │   ├── RegistroCliente.kt
│   │   ├── SeleccionPizzas.kt
│   │   ├── GestionJornada.kt
│   │   └── ResumenPedido.kt
│   └── utils/
│       ├── Validaciones.kt
│       ├── Calculos.kt
│       └── FormatoMoneda.kt
└── .gitignore
```

En el repositorio de GitHub, la documentación y `src/` de esta versión se ubicarán dentro de **`Semana 2/`**, junto a la carpeta independiente `Semana 1/`. Los archivos de IntelliJ y compilados no son necesarios para leer el código publicado.

---

## ▶️ Ejecución

### Desde IntelliJ IDEA

1. Abrir el proyecto Kotlin de **Semana 2**.
2. Abrir `src/Main.kt`.
3. Ejecutar `main()` y responder a las preguntas de la consola.
4. Tras registrar los pedidos, consultar el reporte y, si se desea, avanzar estados mediante sus identificadores.

### Desde terminal, con el compilador Kotlin instalado

Ubicándose en la carpeta del proyecto **`DonnaLunaPizzeria/` de Semana 2**:

```sh
kotlinc src/Main.kt src/modelo/*.kt src/servicios/*.kt src/ui/*.kt src/utils/*.kt -include-runtime -d DonnaLunaS2.jar
java -jar DonnaLunaS2.jar
```

El `.jar` es un archivo generado localmente, no parte del código fuente que debe versionarse.

---

## 🧪 Escenarios verificados manualmente

Durante el desarrollo se ejecutaron en IntelliJ, entre otros, estos escenarios:

| Escenario | Resultado observado |
|---|---|
| 4 Margaritas | Subtotal $30.000, descuento $3.000 y total $27.000 |
| Pedido de 21 Margaritas con stock inicial de 20 | Rechazo, cuatro ingredientes faltantes, stock sin descuento |
| Pedidos sucesivos con ingredientes compartidos | Stock acumulado durante una misma ejecución |
| Texto, cantidad negativa y número muy grande | Aviso de error y posibilidad de corregir la cantidad |
| Correo válido e inválido | Aviso simulado solo para pedido confirmado con correo válido |
| Intento de despachar pedido rechazado | Cambio de estado impedido |
| Pedido en zona Costa o Norte | Asignación demostrativa a Diego o Paula |
| Voucher de pedido rechazado | Cotización referencial sin cobro, con total y estado claramente indicados |

Estos son **escenarios de ejecución manual**, no una suite automatizada de pruebas incluida en el proyecto.

---

## 📌 Alcance de la entrega

**Implementado:** múltiples pedidos durante una ejecución, POO con herencia, inventario por ingredientes, validación de entradas, cálculo y desglose de IVA incluido, repartidores por zonas, estados, reporte de jornada y vouchers de consola.

**Fuera del alcance actual:** interfaz gráfica Android, ubicación GPS real, actualización de repartidores en tiempo real, persistencia entre ejecuciones, pagos o boletas tributarias reales, correos enviados, conexión a base de datos y reposición interactiva de inventario.

La documentación corresponde al código de **Semana 2** y no modifica la entrega estable de **Semana 1**.

---

<div align="center">

### 🍕 Donna Luna · Pizzería

**Desarrollo App Móviles I · PMY2201 · Semana 2**

Natalia Alvarado · 2026

</div>
