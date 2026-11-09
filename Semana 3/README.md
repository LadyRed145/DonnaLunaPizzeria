<div align="center">

# 🍕 DONNA LUNA · PIZZERÍA

### Desarrollo App Móviles I · PMY2201 · Semana 3

**Aplicación de consola en Kotlin para gestionar pedidos de pizzas, inventario por ingredientes, repartidores y reportes de una jornada.**

*Kotlin avanzado · POO · Regex y Ranges · Anotaciones · Reflection · Sobrecarga de operadores*

[📁 Repositorio DonnaLunaPizzeria](https://github.com/LadyRed145/DonnaLunaPizzeria) · [📖 Documentación de Semana 1](../Semana%201/README.md)

</div>

---

## ✦ Visión general

**Donna Luna Pizzería** es un proyecto académico desarrollado en **Kotlin**, ejecutado por consola desde **IntelliJ IDEA**. La **Semana 3** continúa las Semanas 1 y 2: conserva las cinco pizzas y sus precios, el inventario, el descuento por volumen, los comprobantes y los reportes. Incorpora **Regex, Ranges, promociones por fecha, anotaciones personalizadas, reflection, sobrecarga de operadores, desestructuración de objetos y evaluación de igualdad**.

El código está organizado en **modelos, servicios, utilidades, anotaciones y funciones de interfaz de consola**. Además, la versión actual centraliza los estados, valida sus transiciones, controla la ocupación de repartidores y utiliza `BigDecimal` para los importes. Cada pedido confirmado genera un comprobante; un pedido rechazado genera una cotización referencial.

**Es una simulación académica:** no realiza ventas ni cobros, no envía correos reales y no utiliza GPS, bases de datos ni una interfaz gráfica Android.

| 👤 Clientes | 🍕 Catálogo | 📦 Ingredientes | 🛵 Reparto | 🧾 Jornada |
|:---:|:---:|:---:|:---:|:---:|
| Regex y datos opcionales | 5 pizzas y promociones | Stock por ingrediente | 3 zonas y disponibilidad | Pedidos y reportes |

### Funcionalidades implementadas

- 👤 Registro de nombre, dirección física, teléfono normalizado y correo opcional, con **Regex** y avisos de corrección.
- 🍕 Selección de las cinco variedades, acumulación de cantidades y validación del rango **1–100 pizzas por pedido**.
- 💰 Cálculos con **`BigDecimal`**, descuento del **10 % al superar tres pizzas** y desglose del **IVA del 19 % incluido**.
- 📅 Promoción demostrativa del **1 al 30 de noviembre de 2026**: **5 %** en pizzas identificadas mediante `@Promocionable`.
- 📦 Inventario compartido por jornada, comprobación de ingredientes, detección de faltantes y descuento de stock solo al confirmar.
- 🛵 Asignación por prioridad de zonas **Centro, Norte y Costa**, disponibilidad actualizada y reasignación cuando se libera un repartidor.
- 🔄 Estados centralizados en `EstadoPedido`, con transiciones controladas y bloqueo de pedidos entregados o rechazados.
- 🧾 Comprobantes y cotizaciones referenciales sin cobro, separados de los avisos operativos del inventario.
- 📊 Reporte diario con `Array`, `for`, `filter`, detalles de productos y roles mostrados mediante polimorfismo.
- 🧩 `equals()`/`hashCode()`, prevención de duplicados, operador `Pedido + Pedido`, desestructuración de `Cliente` y `Pedido` y reflection sobre clases del dominio.
- 🛡️ Manejo seguro de entradas incorrectas, valores nulos, fechas inválidas y cantidades fuera de rango.

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

## 🛒 Cálculo de pedidos, promociones e IVA

La lógica monetaria está encapsulada en `src/modelo/Pedido.kt`; el redondeo y el desglose tributario se realizan en `src/utils/Calculos.kt`. Se utiliza **`BigDecimal` en los precios, subtotales, descuentos y totales** para evitar errores de precisión binaria.

| Concepto | Regla implementada |
|---|---|
| Subtotal | Suma de `precio unitario × cantidad` por producto |
| Descuento por volumen | **10 %** si se solicitan **más de 3 pizzas** en total; en caso contrario, 0 % |
| Promoción por fecha | **5 %** solo sobre pizzas promocionables y para fechas entre **2026-11-01 y 2026-11-30**, ambos días incluidos |
| Orden de descuentos | Primero descuento por volumen; luego el promocional sobre el importe elegible restante |
| Total | Subtotal menos ambos descuentos aplicables, sin cobros reales |
| IVA | **19 % incluido** en el total; no se agrega nuevamente |
| Base neta | Total final dividido por `1,19`, redondeado al peso entero más cercano |
| IVA incluido | Total final menos base neta |
| Redondeo | `RoundingMode.HALF_UP`; descuentos y base neta en pesos enteros; IVA como diferencia respecto al total |
| Moneda | Pesos chilenos sin decimales y con punto de miles en todo el comprobante |

### Ejemplo: cuatro Margaritas sin promoción

| Concepto | Importe |
|---|---:|
| Subtotal | $30.000 |
| Descuento por volumen (10 %) | −$3.000 |
| Base neta del total final | $22.689 |
| IVA incluido (19 %) | $4.311 |
| **Total final** | **$27.000** |

### Promociones de Semana 3

La aplicación solicita opcionalmente una fecha con el formato **`AAAA-MM-DD`**. Si se deja vacía, utiliza la fecha actual; si la fecha es inválida, solicita otra entrada. Fuera del periodo promocional se mantienen los precios habituales.

La anotación **`@Promocionable`** se detecta mediante reflection en miembros de `CatalogoPizzas` y `SeleccionEspecial`. Para esta demostración, los productos elegibles son **Margarita, Pepperoni y Cuatro Quesos**. Las otras pizzas siguen disponibles a su precio normal.

**Ejemplo:** 2 Margaritas con promoción vigente: subtotal **$15.000**, descuento promocional **$750** y **total $14.250**. La promoción es una regla de simulación académica; no se consulta un servicio externo de ofertas.

---

## 🛵 Repartidores y estados

`RepartidorService.kt` mantiene una lista fija de **tres repartidores**, con disponibilidad que se actualiza durante la jornada. La selección utiliza un **orden de prioridad por zonas cercanas simuladas**: primero la zona solicitada y luego las zonas alternativas configuradas.

| Zona de referencia | Repartidor asociado |
|---|---|
| Centro | Carlos |
| Norte | Paula |
| Costa | Diego |

Si el repartidor de la zona está ocupado, el sistema intenta asignar otro disponible según la prioridad definida. Si los tres están ocupados, el pedido queda **«Sin asignar»** hasta que pueda reservarse uno durante la gestión de estados. Al entregar un pedido, su repartidor vuelve a quedar disponible.

La cercanía **no se calcula con GPS ni distancias reales**: es una prioridad determinista para la simulación. Una zona no reconocida se trata como Centro.

### Ciclo de estados

Los estados están centralizados en el enum `EstadoPedido` y las transiciones se validan dentro de `Pedido`:

```text
En preparación → Listo → En camino → Entregado
        └──────────────→ Rechazado (si no hay stock)
```

El usuario avanza pedidos mediante su ID. Para pasar de **Listo** a **En camino** se necesita un repartidor asignado y reservado; `entregarPedido()` comprueba que sea el correspondiente. Los pedidos **Entregado** o **Rechazado** no pueden avanzar ni volver a estados anteriores.

---

## 🧾 Voucher y cotizaciones

`src/ui/ResumenPedido.kt` genera un recuadro de texto con caracteres de consola y funciones separadas por responsabilidad:

- **Encabezado:** Donna Luna Pizzería e identificador del pedido.
- **Cliente:** nombre, dirección física, teléfono formateado y correo opcional.
- **Pizzas solicitadas:** variedad, cantidad, precio unitario y subtotales.
- **Resumen del pedido:** subtotal, descuento por volumen, promoción si corresponde, base neta, IVA incluido y total.
- **Cierre:** notas, estado y repartidor asignado; el estado aparece en el bloque inferior.

Para un pedido rechazado, el encabezado informa **«COTIZACIÓN REFERENCIAL — VENTA NO CONFIRMADA»** y el importe figura como **«TOTAL REFERENCIAL (SIN COBRO)»**. No se genera un aviso simulado de confirmación.

`src/ui/InventarioConsola.kt` imprime **fuera del voucher** las existencias, faltantes y pasos de reposición. Así se separa la información operativa del comprobante.

El correo es opcional: cuando se omite, se muestra **«Sin correo»**. Si existe un correo válido y el pedido se confirma, aparece un aviso preparado en consola mediante `let`; **no se envía ningún correo real**.

---

## 📊 Reporte de pedidos de la jornada

`JornadaService.kt` mantiene un **`Array<Pedido>` en memoria**. Cada pedido registrado recibe un identificador consecutivo desde `1001` durante la ejecución actual.

El reporte recorre los pedidos con `for` y muestra ID, cliente, variedades, cantidades, total o cotización referencial, estado, repartidor, **roles de usuario** (`describirRol()`) y productos.

Mediante `filter`, clasifica los pedidos:

| Grupo | Estados |
|---|---|
| Pendientes | `En preparación`, `Listo`, `En camino` |
| Completados | `Entregado` |
| Rechazados | `Rechazado` |

El reporte se imprime al finalizar el registro y nuevamente si se cambian estados. Inventario, clientes registrados y disponibilidad de repartidores se mantienen en memoria durante la jornada y **se reinician al volver a ejecutar el programa**.

El registro de clientes utiliza un `LinkedHashSet` para detectar equivalencias por **nombre y correo**, sin distinguir mayúsculas ni espacios extremos. Si un cliente ya está registrado, se informa y **no se incorpora otra entrada al conjunto**; el pedido conserva los datos proporcionados en ese registro de consola.

---

## ✅ Validaciones y manejo de errores

| Entrada o situación | Comportamiento implementado |
|---|---|
| Nombre vacío | Se utiliza `No informado` |
| Dirección vacía, demasiado corta o con `@` | Se rechaza y se vuelve a solicitar una dirección física; admite direcciones sin número como `Parcela S/N` |
| Teléfono válido | Regex de **9 a 12 dígitos**, con `+` opcional; se normaliza mediante `formatearTelefono()` |
| Teléfono inválido | Se muestra un aviso y se solicita nuevamente; Enter permite `No informado` |
| Correo inválido | Se muestra un aviso y se solicita nuevamente un formato válido |
| Correo vacío | Se conserva como `null` y se muestra `Sin correo` |
| Opción de pizza fuera del menú | Se informa el error y se vuelve a solicitar |
| Cantidad no numérica o fuera de **1–100** | Se rechaza y se vuelve a solicitar; también se controla el acumulado por variedad y pedido |
| Fecha promocional imposible o mal escrita | Se rechaza y se vuelve a solicitar una fecha válida (`AAAA-MM-DD`) |
| Fecha fuera de promoción | No se aplica el descuento de temporada |
| Pedido sin pizzas | No se registra un pedido vacío |
| Ingredientes insuficientes | Se rechaza el pedido sin descontar inventario; se detallan faltantes |
| Notas vacías | Se muestra `Sin notas` con `?:` |
| Repartidor ausente | Se muestra `Sin asignar` y no se permite despacho sin asignación |
| Estado finalizado | Se bloquean transiciones posteriores de pedidos entregados o rechazados |

Se emplean `readlnOrNull()`, `?.`, `?:`, `takeIf`, `let`, `require`, `toIntOrNull()`, `try-catch`, expresiones regulares, rangos de números y fechas, y plantillas de strings. Se validan además existencias iniciales no negativas y cantidades positivas de ingredientes.

---

## 🔄 Flujo funcional

```text
Iniciar Donna Luna Pizzería
           ↓
Registrar cliente (nombre, dirección, teléfono, correo opcional)
           ↓
Validar entradas e identificar duplicados
           ↓
Seleccionar variedades y cantidades (1–100)
           ↓
¿El pedido tiene productos?
    ├── No → Terminar esa selección sin pedido
    └── Sí
           ↓
Registrar notas y promoción por fecha (opcional)
           ↓
Calcular descuentos e ingredientes necesarios
           ↓
Validar inventario
    ├── Insuficiente → Rechazar, conservar stock
    │                   y mostrar cotización referencial
    └── Suficiente → Descontar ingredientes,
                      seleccionar zona y asignar repartidor disponible
           ↓
Generar voucher y mostrar inventario por separado
           ↓
¿Registrar otro pedido?
    ├── Sí → Repetir con inventario y repartidores actualizados
    └── No
           ↓
Reporte diario y gestión de estados
           ↓
Reporte actualizado (si hubo cambios)
           ↓
Demostración Kotlin Semana 3 (sin venta ni consumo de stock)
           ↓
Fin
```

---

## 🧩 Arquitectura Kotlin

La versión de **Semana 3** está distribuida en **28 archivos `.kt`**, organizados en modelos, servicios, interfaz de consola, anotaciones y utilidades.

| Archivo o paquete | Responsabilidad |
|---|---|
| `src/Main.kt` | Coordina el registro, inventario, jornada y demostración final |
| `modelo/Usuario.kt` | Clase base y método polimórfico `describirRol()` |
| `modelo/Cliente.kt` | Datos de cliente, correo opcional, desestructuración, `equals()` y `hashCode()` |
| `modelo/Repartidor.kt` | Zona, reserva y liberación de disponibilidad; despacho controlado |
| `modelo/Producto.kt` | Nombre, categoría, precio `BigDecimal`, ingredientes e igualdad por identidad |
| `modelo/CategoriaProducto.kt` | Enum de categorías de productos |
| `modelo/Pedido.kt` | Cantidades, importes, operador `+`, desestructuración, estados y repartidor |
| `modelo/EstadoPedido.kt` | Enum compartido y transiciones autorizadas |
| `modelo/PromocionPedido.kt` | Datos inmutables de una promoción validada |
| `servicios/CatalogoPizzas.kt` | Catálogo, recetas, elementos anotados y selección promocionable |
| `servicios/InventarioService.kt` | Cálculo, validación y descuento de ingredientes |
| `servicios/RepartidorService.kt` | Asignación por prioridades de zona y disponibilidad |
| `servicios/RegistroClientesService.kt` | Conjunto de clientes y prevención de duplicados |
| `servicios/PromocionesService.kt` | Fechas promocionales y detección dinámica de anotaciones |
| `servicios/ReflexionService.kt` | Inspección de métodos y propiedades de clases del dominio |
| `servicios/JornadaService.kt` | Arreglo de pedidos, búsquedas, filtros y reportes |
| `ui/RegistroCliente.kt` | Captura y validación de datos del cliente |
| `ui/SeleccionPizzas.kt` | Menú, cantidades y rangos |
| `ui/GestionJornada.kt` | Notas, zonas, repetición y cambios de estado |
| `ui/PromocionConsola.kt` | Solicitud de fecha para promoción opcional |
| `ui/ResumenPedido.kt` | Formato modular del voucher y cotización referencial |
| `ui/InventarioConsola.kt` | Formato de existencias, faltantes y avisos operativos |
| `ui/ReporteSemana3.kt` | Demostración de Kotlin avanzado sin modificar la jornada |
| `utils/Validaciones.kt` | Regex, rangos, formateo telefónico y parseo de fecha |
| `utils/Calculos.kt` | Redondeo con `BigDecimal` y desglose del IVA incluido |
| `utils/FormatoMoneda.kt` | Representación de importes en pesos chilenos |
| `utils/Constantes.kt` | Parámetros descriptivos de negocio y fechas |
| `anotaciones/Promocionable.kt` | Anotación personalizada detectable en ejecución |

### Recursos de Kotlin aplicados

| Recurso | Aplicación |
|---|---|
| `val`, `var`, tipos y conversiones | Entidades, validaciones y estado controlado |
| `if`, `when`, `while`, `for` | Flujos de entrada, inventario, menús y reportes |
| `Array`, `Map`, `MutableMap`, `List`, `Set` | Pedidos, ingredientes, catálogo, clientes y repartidores |
| `filter`, `find`, `sumOf`, `distinct`, `linkedSetOf` | Consultas, sumas, validación de duplicados y selección |
| Regex y Ranges | Correos, teléfonos, cantidades entre 1 y 100, y fechas promocionales |
| Anotaciones y reflection | `@Promocionable` y listado de miembros de `Pedido` y `Producto` |
| Sobrecarga de operadores | `Pedido + Pedido`; comparación `==` respaldada por `equals()` |
| Desestructuración | `component1()`, `component2()`, `component3()` en `Cliente` y `Pedido` |
| `equals()` y `hashCode()` | Identidad lógica de clientes y productos, sin duplicados de registro |
| Clases, herencia y polimorfismo | `Usuario`, `Cliente`, `Repartidor` y `describirRol()` |
| `BigDecimal` y `RoundingMode.HALF_UP` | Importes, descuentos y precisión monetaria |
| `try-catch`, `require`, `?.`, `?:`, `let` | Errores, restricciones de dominio y valores nulos |
| Plantillas de strings | Menús, avisos, voucher, reportes y demostración final |

### Organización del código fuente

```text
DonnaLunaPizzeria/
├── README.md
├── src/
│   ├── Main.kt
│   ├── anotaciones/
│   │   └── Promocionable.kt
│   ├── modelo/
│   │   ├── Usuario.kt
│   │   ├── Cliente.kt
│   │   ├── Repartidor.kt
│   │   ├── Producto.kt
│   │   ├── CategoriaProducto.kt
│   │   ├── Pedido.kt
│   │   ├── EstadoPedido.kt
│   │   └── PromocionPedido.kt
│   ├── servicios/
│   │   ├── CatalogoPizzas.kt
│   │   ├── InventarioService.kt
│   │   ├── RepartidorService.kt
│   │   ├── RegistroClientesService.kt
│   │   ├── PromocionesService.kt
│   │   ├── ReflexionService.kt
│   │   └── JornadaService.kt
│   ├── ui/
│   │   ├── RegistroCliente.kt
│   │   ├── SeleccionPizzas.kt
│   │   ├── GestionJornada.kt
│   │   ├── PromocionConsola.kt
│   │   ├── ResumenPedido.kt
│   │   ├── InventarioConsola.kt
│   │   └── ReporteSemana3.kt
│   └── utils/
│       ├── Validaciones.kt
│       ├── Calculos.kt
│       ├── FormatoMoneda.kt
│       └── Constantes.kt
└── .gitignore
```

Para la entrega académica se utiliza el proyecto de **Semana 3**. Si también se publica en GitHub, puede ubicarse en la carpeta independiente **`Semana 3/`**, conservando las entregas de las Semanas 1 y 2 sin modificarlas. Las carpetas de compilación y los archivos temporales del IDE no forman parte del código fuente requerido.

---

## ▶️ Ejecución

### Desde IntelliJ IDEA

1. Abrir el proyecto Kotlin de **Semana 3**.
2. Verificar que el proyecto tenga configurado un JDK compatible.
3. Abrir `src/Main.kt`.
4. Ejecutar `main()` y responder a las preguntas de la consola.
5. Registrar pedidos, consultar el reporte y, si se desea, avanzar los estados por ID.
6. Revisar al final la sección **«DEMOSTRACIÓN KOTLIN — SEMANA 3»**.

### Desde terminal Fish, con el compilador Kotlin instalado

Ubicándose en la carpeta del proyecto **`DonnaLunaPizzeria/` de Semana 3**:

```fish
kotlinc src/Main.kt src/anotaciones/*.kt src/modelo/*.kt src/servicios/*.kt src/ui/*.kt src/utils/*.kt -include-runtime -d DonnaLunaS3.jar
java -jar DonnaLunaS3.jar
```

El `.jar` se genera localmente; no forma parte del código fuente que debe entregarse. La aplicación es **Kotlin de consola**, por lo que no usa `npm`, `package.json` ni un comando `npm run build`.

---

## 🧪 Escenarios verificados manualmente

Durante el desarrollo de **Semana 3** se ejecutaron en IntelliJ, entre otros, estos escenarios:

| Escenario | Resultado observado |
|---|---|
| 3 Margaritas sin promoción | Total $22.500; ingredientes compartidos de Margarita quedan en 17 |
| 20 Margaritas y después 5 adicionales | Primera venta simulada confirmada por $135.000; segundo pedido rechazado sin descontar stock |
| Correo inválido (`p@ht.c`) y cantidad `101` | Se informa el error y se solicita corregir la entrada |
| 3 Margaritas y 2 Pepperoni con promoción del 15-11-2026 | Subtotal $41.500; volumen −$4.150; promoción −$1.868; total $35.482 |
| Promoción solicitada para 01-12-2026 | Fuera del periodo; no se aplica descuento promocional |
| Dirección ingresada como `cat@gmail.com` | Se rechaza y solicita una dirección física |
| Correo omitido | Se muestra `Sin correo`, sin aviso simulado de correo |
| Cliente repetido con variación de mayúsculas | Se informa que ya existe y no se duplica su registro |
| Cuatro pedidos de una Margarita para Centro | Carlos, Paula, Diego y cuarto «Sin asignar» mientras todos están ocupados |
| Entrega de pedidos y liberación del repartidor | El cuarto pedido puede recibir a Carlos y completarse |
| Pedido entregado o rechazado | Bloqueo de nuevas transiciones |
| Demostración de Semana 3 | `+`, desestructuración, igualdad, anotaciones, reflection y polimorfismo en consola |

Estas comprobaciones son **ejecuciones manuales**, no una afirmación de cobertura exhaustiva. La demostración integrada no altera el inventario ni los pedidos de la jornada.

---

## 📌 Alcance de la entrega

**Implementado:** gestión de varios pedidos por ejecución; catálogo de cinco pizzas; POO, herencia y polimorfismo; inventario por ingredientes; validaciones con Regex y Ranges; promociones por fechas y anotaciones; reflection de clases; operador `+`, desestructuración, `equals()` y `hashCode()`; cálculos con `BigDecimal`, IVA incluido; asignación y liberación de repartidores; estados seguros, reportes y comprobantes de consola.

**Fuera del alcance actual:** interfaz gráfica Android, ubicación GPS o distancias reales, persistencia entre ejecuciones, pagos o boletas tributarias reales, envío de correos, conexión a base de datos y reposición interactiva de inventario. La disponibilidad de repartidores se actualiza **dentro de la ejecución**, no mediante un servicio de seguimiento externo.

La actividad de Semana 3 se entrega como **proyecto Kotlin en ZIP o RAR**, con demostración funcional en consola. Este README constituye su **documentación básica**, sin necesidad de un informe PDF, Word o propuesta técnica adicional conforme a las instrucciones de la actividad.

La documentación corresponde al código de **Semana 3** y mantiene la continuidad de las Semanas 1 y 2, sin modificar sus entregas estables.

---

<div align="center">

### 🍕 Donna Luna · Pizzería

**Desarrollo App Móviles I · PMY2201 · Semana 3**

Natalia Alvarado · 2026

</div>
