# ☾ Donna Luna Pizzería — Semana 5

**Desarrollo de Aplicaciones Móviles I (PMY2201) · Integrando Kotlin en Android Studio**

Proyecto completo de la Semana 5, construido sobre la entrega funcional de Semana 4. Se mantienen las cuatro pantallas, el catálogo, la gestión de clientes, pedidos, promociones, inventario, repartidores, validaciones y el diseño Material 3 original.

## 📱 Interfaz

| Pantalla | Función |
|---|---|
| 👤 Cliente (inicio) | Formulario validado y **resumen dinámico** de clientes, pedidos y último cliente |
| 🍕 Pizzas | Cinco especialidades, fotos locales y cantidad total de 1 a 100 |
| 🛵 Entrega | Zona, notas, promoción por fecha y confirmación |
| 📊 Panel | **Resumen dinámico**, pedido, jornada e inventario |

El resumen inicia en **0 clientes / 0 pedidos / Aún no hay clientes**. Al confirmar un pedido, `PizzeriaViewModel` actualiza los datos mediante `viewModelScope.launch` y `LiveData`; Compose los observa mediante `observeAsState()`. Los datos de demostración se almacenan **en memoria durante la sesión**, no en una base de datos persistente. El ViewModel conserva la sesión en rotaciones de pantalla.

## 🧩 Arquitectura Gradle

```text
PizzeriaApp/
├── app/              → Android application; MainActivity y tema
├── datos/            → Android library; modelo/, servicios/, anotaciones/
├── utilidades/       → Android library; utils/
├── ui/               → Android library; cuatro pantallas Compose,
│                      widgets, resumen y PizzeriaViewModel
├── evidencias/       → Coloca aquí tus capturas nuevas de Semana 5
├── gradle/           → Wrapper y catálogo de versiones
├── settings.gradle.kts
├── build.gradle.kts
└── README.md
```

**Dependencias:** `app → ui → datos → utilidades` y `ui → utilidades`.

Los archivos `PantallaCliente.kt`, `PantallaPizzas.kt`, `PantallaEntrega.kt`, `PantallaPanel.kt`, `PantallaPrincipal.kt`, `ResumenActividad.kt` y `ComponentesDonnaLuna.kt` se mantienen separados para facilitar mantenimiento. `MainActivity` solo configura el tema y abre Compose.

Las dependencias incluyen Jetpack Compose + Material 3, `lifecycle-viewmodel-compose`, `lifecycle-viewmodel-ktx`, `lifecycle-livedata-ktx`, `compose-runtime-livedata` y `kotlinx-coroutines-android`, declaradas mediante el catálogo de versiones y Gradle Kotlin DSL. Compatible con API mínima 24, superior al requisito mínimo 21.

## 🍕 Comportamiento de negocio preservado

- Cinco pizzas con fotografías locales.
- IVA 19 % ya incorporado a los precios.
- Descuento por volumen de 10 % cuando se superan tres pizzas.
- Promoción 5 % durante noviembre de 2026 para productos elegibles.
- Validación reactiva de cliente (nombre, dirección, teléfono y correo), fecha y cantidades. Campos erróneos muestran mensajes al editar; la navegación y la confirmación vuelven a verificar las mismas reglas.
- Comprobación y descuento atómico del inventario (20 porciones por ingrediente).
- Repartidores y estados de preparación, listo, en camino, entregado y rechazado.
- Reporte de jornada y detalle económico del último pedido.
- Menú inferior que evita saltarse validaciones al avanzar.

## ▶️ Ejecutar y verificar

1. Abre **la carpeta raíz de este ZIP** en Android Studio (la que contiene `settings.gradle.kts`).
2. Sincroniza Gradle. El equipo debe contar con JDK, Android SDK API 37 y acceso a las dependencias Gradle si aún no están descargadas.
3. Abre el emulador AVD de Semana 4 o cualquier dispositivo con Android API 24 o superior.
4. Selecciona la configuración `app` y ejecuta la aplicación.
5. En **Cliente** comprueba el resumen 0/0 inicial. Introduce nombre y dirección y continúa.
6. En **Pizzas**, selecciona al menos una unidad y avanza a **Entrega**.
7. Confirma un pedido y comprueba que en **Panel** el resumen indique 1 cliente, 1 pedido y el nombre registrado.
8. Registra otro cliente y otro pedido, vuelve a **Panel** o **Cliente** y verifica que las cifras y el último nombre cambian.
9. Comprueba el flujo del pedido, el inventario, las promociones, los botones de validación y las cuatro vistas.

## 🧪 Pruebas automatizadas incluidas

La entrega incluye pruebas JUnit de reglas de negocio, validación del formulario y navegación: `datos/src/test/java/servicios/ReglasPizzeriaTest.kt`, `utilidades/src/test/java/utils/{ValidacionesTest,ValidacionClienteTest}.kt` y `ui/src/test/java/com/donnaluna/pizzeriaapp/ui/NavegacionClienteTest.kt`. Cubren descuentos, promociones, stock, registro, correo, teléfono, nombre/dirección y bloqueo del avance.

Para ejecutarlas en una terminal **Fish** desde la raíz del proyecto:

```fish
./gradlew :utilidades:testDebugUnitTest :datos:testDebugUnitTest :ui:testDebugUnitTest :app:assembleDebug
```

**Nota de validación:** se prepararon y se verificó la compilación de lógica Kotlin independiente; la ejecución completa de Gradle/AVD se debe confirmar en Android Studio, porque el entorno de preparación no dispone de Android SDK ni de las dependencias Android descargadas.

## 📸 Evidencias exigidas — Semana 5

Generar **nuevas** capturas (no utilizar las de Semana 4):

1. `evidencias/01_modulos_gradle.png` — Vista del proyecto Android Studio con `app`, `datos`, `ui`, `utilidades`, y sus archivos Gradle.
2. `evidencias/02_resumen_inicial.png` — Emulador ejecutando la pantalla Cliente, con resumen de clientes/pedidos/último cliente.
3. `evidencias/03_actualizacion_dinamica.png` — Emulador ejecutando Cliente o Panel después de registrar pedidos, con nuevos totales y último nombre. Puedes mostrar también catálogo/panel con capturas adicionales si lo consideras útil.

Una vez verificadas las pruebas, añade las capturas a `evidencias/` y vuelve a comprimir la carpeta completa del proyecto Android Studio como ZIP/RAR para la entrega.

**Aclaración sobre la pauta:** el texto de instrucciones menciona veterinaria, mascotas y consultas en algunos párrafos, pero el objetivo y criterios concretos de la actividad solicitan clientes, productos, pedidos y resumen de clientes/pedidos/último cliente. Esta entrega conserva el caso de negocio real de la Semana 4 (Donna Luna Pizzería).

## 🔧 Revisión de calidad (corrección de Semana 5)

- Validaciones de Cliente centralizadas en `:utilidades` para no duplicar reglas entre la UI y el ViewModel.
- `CampoCliente` usa estado local `rememberSaveable` solamente para mostrar errores al editar; los valores viven en `PizzeriaViewModel`, que actúa como fuente única de verdad. Esto es el equivalente funcional a un patrón de hooks en React, pero **Kotlin/Compose no utiliza React Hooks**.
- Se eliminaron los tests de ejemplo generados automáticamente y un servicio de reflexión sin referencias (código muerto); se mantuvieron los tests de negocio.
- Se limpiaron imports no usados en pantallas Compose. No se alteraron las otras tres pantallas ni los cálculos de pedidos e inventario.
- **Prueba manual obligatoria:** intentar avanzar desde Cliente con nombre vacío, dirección incorrecta, teléfono malformado y correo malformado; comprobar los mensajes y que no avance. Corregir datos y confirmar que se puede avanzar.
