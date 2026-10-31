<div align="center">

# 🍕 DONNA LUNA · PIZZERÍA

### Desarrollo App Móviles I · PMY2201 · Semana 1

**Aplicación de consola en Kotlin para registrar un pedido de pizzas, validar datos y controlar un inventario básico.**

*Fundamentos de Kotlin · Versión de entrega de Semana 1*

[📁 Repositorio DonnaLunaPizzeria](https://github.com/LadyRed145/DonnaLunaPizzeria) · [🏷️ Versión estable](https://github.com/LadyRed145/DonnaLunaPizzeria/tree/semana-1-estable)

</div>

---

## ✦ Visión general

**Donna Luna Pizzería** es un proyecto académico desarrollado en Kotlin. Simula el registro de un cliente y la preparación de **un pedido por ejecución**, que puede contener distintas variedades de pizza y cantidades acumuladas.

La aplicación utiliza entradas y salidas de consola: solicita los datos del cliente, muestra un catálogo, valida la selección, calcula los importes y comprueba la disponibilidad antes de confirmar el pedido. **No es una aplicación Android con interfaz gráfica ni un sistema de pedidos reales.**

| 👤 Cliente | 🍕 Catálogo | 🛒 Pedido | 📦 Inventario |
|:---:|:---:|:---:|:---:|
| Nombre, dirección y teléfono | 5 variedades | Selección múltiple y descuento | 20 unidades genéricas iniciales |

### Funcionalidades implementadas

- 👤 Registro de **nombre**, **dirección** y **teléfono** del cliente.
- ✅ Validación del teléfono con expresión regular: **8 a 15 dígitos**, con `+` inicial opcional.
- 📝 Valores predeterminados cuando faltan datos o el teléfono es inválido.
- 🍕 Menú de **cinco pizzas**, con nombre y precio unitario.
- ➕ Selección de varias variedades y acumulación de cantidades por variedad.
- 🛡️ Rechazo de opciones inexistentes, cantidades no positivas y entradas no numéricas.
- 🔢 Comprobación para evitar desbordamiento de `Int` al acumular cantidades.
- 💰 Cálculo del subtotal y **descuento del 10 % al superar tres pizzas** en total.
- 📦 Comprobación de stock antes de aceptar el pedido; descuento de unidades solo si alcanza.
- 🧾 Resumen del cliente, pizzas solicitadas, importes, estado, repartidor y stock resultante.

---

## 🍕 Catálogo de pizzas

Los precios están definidos en `src/Main.kt` como una lista de pares (`List<Pair<String, Double>>`).

| N.º | Pizza | Precio unitario |
|---:|---|---:|
| 1 | Italiana | $8.500 |
| 2 | Margarita | $7.500 |
| 3 | Pepperoni | $9.500 |
| 4 | Cuatro Quesos | $10.500 |
| 5 | Hawaiana | $9.000 |

El usuario puede elegir una pizza, indicar su cantidad y regresar al menú para agregar otra variedad. La opción **0** finaliza la selección.

---

## 🛒 Registro y cálculo del pedido

El programa utiliza un `linkedMapOf<Int, Int>()` para asociar el número de cada pizza con la cantidad solicitada. Si la misma variedad se selecciona varias veces, las unidades se **acumulan** en la misma entrada.

### Reglas de cálculo

| Concepto | Regla implementada |
|---|---|
| Subtotal | Suma de `precio unitario × cantidad` por variedad |
| Cantidad total | Suma de las cantidades mediante `sumOf`, convirtiendo a `Long` |
| Descuento | **10 %** cuando la cantidad total de pizzas es **mayor que 3** |
| Total final | `subtotal − monto del descuento` |
| Identificador del pedido | `1001` (valor fijo en esta versión) |

**Alcance de los importes:** esta versión no calcula ni desglosa IVA. Los importes se representan con `Double` y se imprimen en consola con el símbolo `$`.

---

## 📦 Control de inventario y estado

La Semana 1 modela un **inventario genérico de 20 unidades**, no un inventario separado por ingrediente. Por cada pizza solicitada se requiere una unidad de ese stock.

| Condición | Resultado | Estado | Repartidor |
|---|---|---|---|
| Hay unidades suficientes | Confirma y descuenta las unidades solicitadas | `Preparación` | `Carlos` |
| No alcanzan las unidades | No descuenta stock | `Rechazado` | `Sin asignar` |

Cuando el pedido es rechazado, el resumen **conserva el importe calculado como referencia**, pero informa que la compra no fue confirmada.

El stock parte nuevamente de 20 unidades al iniciar otra ejecución: **no existe persistencia** entre ejecuciones.

---

## ✅ Validaciones de entrada

| Campo o acción | Validación y comportamiento |
|---|---|
| Nombre vacío o ausente | Se utiliza `No informado` |
| Dirección vacía o ausente | Se utiliza `No informada` |
| Teléfono inválido | Se utiliza `No informado` y se muestra un aviso |
| Opción fuera del menú | Se informa el error y vuelve a solicitar una selección |
| Cantidad no numérica, cero o negativa | Se vuelve a solicitar una cantidad positiva |
| Cantidades acumuladas mayores que `Int.MAX_VALUE` | Se rechaza esa incorporación sin alterar el pedido |
| Pedido sin productos | Termina sin generar un pedido confirmado |
| Stock insuficiente | Pedido rechazado sin disminuir las existencias |

Para las entradas se utilizan `readlnOrNull()`, `trim()`, `toIntOrNull()` y expresiones seguras para tratar valores nulos.

---

## 🔄 Flujo funcional

```text
Inicio de la aplicación
         ↓
Registrar cliente y validar teléfono
         ↓
Mostrar catálogo de cinco pizzas
         ↓
Seleccionar variedades y cantidades
         ↓
Finalizar selección (opción 0)
         ↓
¿Se seleccionaron pizzas?
   ├── No → Finalizar sin productos
   └── Sí
         ↓
Calcular subtotal, descuento y total
         ↓
Comprobar inventario genérico
   ├── Suficiente → Confirmar y descontar stock
   └── Insuficiente → Rechazar sin descontar
         ↓
Imprimir resumen del pedido e inventario
```

---

## 🧩 Organización y recursos de Kotlin

### Estructura publicada en GitHub

```text
DonnaLunaPizzeria/
├── .gitignore
└── Semana 1/
    ├── README.md
    └── src/
        └── Main.kt
```

> El árbol muestra la organización con este README incorporado. El código de Semana 1 permanece completo en `src/Main.kt`, tal como se entregó.

| Recurso | Uso real en Semana 1 |
|---|---|
| `val` y `var` | Datos inmutables y estado variable del inventario |
| `List<Pair<String, Double>>` y `listOf` | Catálogo y precios |
| `MutableMap` y `linkedMapOf` | Variedades seleccionadas y cantidades |
| `if`, `while`, `for`, `break`, `continue` | Decisiones, menús y validaciones |
| `Regex` | Formato del teléfono |
| `readlnOrNull` y `toIntOrNull` | Captura segura y conversión de entradas |
| `sumOf` | Cantidades y subtotales |
| `Int.MAX_VALUE` | Protección contra desbordamiento de cantidades |
| Plantillas de strings | Menú, mensajes y resumen |

---

## ▶️ Ejecución

### Desde IntelliJ IDEA

1. Abrir el proyecto Kotlin de la Semana 1 en IntelliJ IDEA.
2. Abrir `src/Main.kt`.
3. Ejecutar `main()` y completar los datos solicitados en la consola.

### Desde terminal, con el compilador Kotlin instalado

Ubicándose en la carpeta **`Semana 1/`**:

```sh
kotlinc src/Main.kt -include-runtime -d DonnaLunaS1.jar
java -jar DonnaLunaS1.jar
```

El archivo `.jar` es una salida de compilación local y no necesita subirse al repositorio.

---

## 📌 Alcance de esta entrega

**Implementado:** un pedido por ejecución, catálogo, registro de cliente, validaciones, selección múltiple, descuento, stock genérico y resumen.

**No implementado en Semana 1:** correo electrónico, stock individual por ingredientes, múltiples pedidos del día, cálculo explícito de IVA, selección de repartidores por cercanía, almacenamiento persistente, interfaz gráfica Android o integración con servicios externos.

La reorganización en paquetes y la introducción de clases se abordan **en Semana 2**. La etiqueta `semana-1-estable` conserva la versión original publicada de esta entrega.

---

<div align="center">

### 🍕 Donna Luna · Pizzería

**Desarrollo App Móviles I · PMY2201 · Semana 1**

Natalia Alvarado · 2026

</div>
