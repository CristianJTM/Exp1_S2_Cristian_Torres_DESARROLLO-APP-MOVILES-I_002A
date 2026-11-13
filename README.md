# Pizzería Kotlin — Actividad Sumativa 3

Aplicación de consola en Kotlin para gestionar pedidos, clientes, inventario y repartidores. Integra POO, expresiones regulares, rangos, anotaciones, reflection, sobrecarga de operadores, desestructuración e igualdad de objetos.

## Funcionalidades

- Registro de clientes con correo obligatorio, validado mediante `Regex` y con reingreso si el formato es incorrecto.
- Normalización y formato de teléfonos al estilo `+56 (9) 1234-5678` o `+XX (XXX) XXX-XXXX`.
- Catálogo de pizzas, combos y bebidas.
- Productos promocionables identificados con `@Promocionable`.
- Verificación de ingredientes antes de aceptar un pedido.
- Inventario administrado con un `Map`.
- Cantidades permitidas dentro del rango `1..100`, validadas en la interfaz y en el dominio.
- Descuento del 10% desde 4 pizzas.
- Descuento promocional del 5% dentro del periodo de promociones.
- IVA del 19% aplicado después de los descuentos.
- Asignación del repartidor más cercano según zona y distancia.
- Estados: En preparación, Listo y En camino.
- Reporte diario con arreglo, ciclo `for` y agrupación con `filter`.
- Combinación de pedidos mediante el operador `+`, con resultado controlado e identificador nuevo.
- Desestructuración de clientes y pedidos.
- Igualdad de clientes y productos con `equals`/`hashCode`.
- Resumen avanzado que integra anotaciones, reflection, operadores, desestructuración y validaciones.

## Estructura

```text
src/
├── Main.kt
├── modelo/
│   ├── Usuario.kt
│   ├── Cliente.kt
│   ├── Repartidor.kt
│   ├── CategoriaProducto.kt
│   ├── Producto.kt
│   ├── Pizza.kt
│   ├── Combo.kt
│   ├── Bebida.kt
│   ├── Promocionable.kt
│   ├── ItemPedido.kt
│   ├── EstadoPedido.kt
│   └── Pedido.kt
├── servicio/
│   ├── Catalogo.kt
│   ├── Inventario.kt
│   ├── Pizzeria.kt
│   ├── Promocion.kt
│   └── Promociones.kt
└── utils/
    ├── Constantes.kt
    ├── Validaciones.kt
    ├── Vigencia.kt
    ├── SecuenciaPedidos.kt
    ├── Calculos.kt
    ├── Formato.kt
    └── Reflexion.kt
```

## Menú

1. Registrar pedido.
2. Ver inventario.
3. Reabastecer ingrediente.
4. Asignar repartidor.
5. Cambiar estado de pedido.
6. Generar reporte del día.
7. Mostrar resumen avanzado.
8. Salir.

## Reglas de negocio

- El correo es obligatorio y debe tener formato `nombre@dominio.com`; si está vacío o es inválido, se solicita nuevamente.
- La `Regex` de correo rechaza puntos iniciales, finales o consecutivos, y etiquetas de dominio que empiecen o terminen con guion.
- El teléfono se normaliza a un formato uniforme; si no tiene 9, 11 o 12 dígitos, se solicita nuevamente.
- Si la entrada estándar se cierra (`EOF` o `Ctrl+Z`), la operación en curso se cancela y no se usan valores por defecto.
- La cantidad de productos debe estar entre 1 y 100; la regla se aplica en la interfaz y en `ItemPedido`/`Pedido`.
- El descuento por cantidad es del 10% cuando el pedido tiene más de 3 pizzas de categoría `PIZZA`.
- La promoción está activa desde 7 días antes hasta 7 días después de la fecha de consulta, y se evalúa con la fecha del pedido.
- Si la promoción está activa, se aplica un descuento adicional del 5%.
- El IVA es del 19% y se aplica después de los descuentos.
- Si faltan ingredientes, el pedido no se registra y se muestran los faltantes y los pasos para reabastecer.
- Si no se ingresan notas, se muestra `Sin notas`.
- La secuencia de estados es `En preparación → Listo → En camino`; no se permiten retrocesos.
- Un pedido solo puede pasar a `En camino` cuando se le asigna un repartidor.
- El repartidor se elige por zona y disponibilidad, priorizando la menor distancia.
- Dos clientes son iguales si coinciden en nombre y correo.
- Dos productos son iguales si coinciden en nombre y categoría.
- Un pedido combinado conserva el estado más avanzado y el repartidor si alguno lo tenía.
- El pedido combinado conserva la fecha más reciente y recalcula la promoción con esa fecha.
- Si se intenta combinar pedidos de clientes distintos, `plus()` devuelve `null` y se muestra el motivo; no se lanza una excepción.
- El pedido combinado recibe un identificador nuevo de una secuencia compartida; no reutiliza el del operando izquierdo.
- Si al combinar un producto la suma supera las 100 unidades, se conservan líneas separadas para respetar el rango.
- La colección de ítems se expone como copia de solo lectura; solo `agregarProducto` puede modificarla.

## Conceptos avanzados

| Concepto | Implementación |
|---|---|
| `Regex` | Validación de correo y teléfono en `Validaciones.kt`. |
| `Ranges` | Cantidades `1..100` y rango de fechas evaluado con la fecha del pedido. |
| Anotaciones | `@Promocionable` en `PizzaPromocionable` y `ComboPromocionable`. |
| Reflection | Listado de métodos, campos y anotaciones de las clases del dominio. |
| Operador `+` | `Pedido.plus()` combina ítems, recalcula totales, devuelve `null` si son incompatibles y genera un id nuevo. |
| Secuencia de IDs | `SecuenciaPedidos` entrega identificadores únicos para pedidos registrados y combinados. |
| Operador `==` | `Producto.equals()` por nombre y categoría. |
| Desestructuración | `component1..3` en `Cliente` y `Pedido`. |
| `equals`/`hashCode` | Implementados en `Cliente` y `Producto`. |
| Prevención de duplicados | `Pizzeria.registrarCliente()` y `Catalogo.agregar()`. |
| Resumen integrador | Opción 7 del menú. |

## Responsabilidades

| Capa | Responsabilidad |
|---|---|
| `modelo` | Entidades, reglas del pedido, igualdad, desestructuración y operadores. |
| `servicio` | Catálogo, inventario, promociones, repartidores y reportes. |
| `utils` | Constantes, validaciones, cálculos, formato y reflection. |
| `Main` | Interacción por consola y demostración de funcionalidades. |

## Escenarios de prueba

| Escenario | Condición | Resultado esperado |
|---|---|---|
| Correo inválido | Sin formato `nombre@dominio.com` | Se solicita nuevamente. |
| Correo vacío | Enter en el prompt | Se rechaza y se solicita nuevamente. |
| Correo mal formado | Puntos consecutivos, punto inicial o dominio con guion inicial | La `Regex` lo rechaza y se solicita nuevamente. |
| Entrada cerrada | `EOF` o `Ctrl+Z` durante el registro | Se cancela la operación sin datos por defecto. |
| Descuento por bebidas | Pedido de solo bebidas | No aplica descuento por cantidad. |
| Teléfono local | 9 dígitos | Se normaliza a `+56 (9) 1234-5678`. |
| Teléfono chileno | 11 dígitos | Se formatea como `+56 (9) 1234-5678`. |
| Teléfono internacional | 12 dígitos | Se formatea como `+XX (XXX) XXX-XXXX`. |
| Teléfono inválido | Sin dígitos o longitud no soportada | Se solicita nuevamente. |
| Cantidad inválida | `0`, `101` o texto | Se solicita nuevamente. |
| Cantidad inválida en el dominio | `ItemPedido` o `agregarProducto` con 101 | Se rechaza con un mensaje explícito. |
| Promoción activa | Fecha dentro del rango | Se aplica 5% adicional. |
| Promoción inactiva | Fecha del pedido fuera del rango | No se aplica descuento promocional. |
| Producto promocionable | Clase anotada con `@Promocionable` | Aparece en el resumen avanzado y en el pedido. |
| Reflection | Clases del dominio | Lista métodos, propiedades/campos y anotaciones. |
| Operador `+` | Dos pedidos del mismo cliente | Se combinan ítems y se recalcula el total con un id nuevo. |
| Operador `+` con clientes distintos | Pedidos incompatibles | `plus()` devuelve `null`, se informa el motivo y no hay excepción. |
| Combinación sobre 100 | Dos líneas del mismo producto suman más de 100 | Se conservan líneas separadas y el total permanece correcto. |
| Combinación sin ítems | Ambos pedidos vacíos | `plus()` devuelve `null` con el motivo correspondiente. |
| Colección de ítems | Intento de modificar la lista externa | El pedido no cambia porque `items` es una copia de solo lectura. |
| Resumen con pedidos reales | Dos pedidos del mismo cliente registrados | El resumen usa esos pedidos; si no existen, usa demostración. |
| Igualdad de productos | Mismo nombre y categoría | `==` es verdadero y no se duplica en `Set`. |
| Igualdad de clientes | Mismo nombre y correo | `==` es verdadero y no se registra dos veces. |
| Desestructuración | Cliente y pedido | Se obtienen nombre/correo/teléfono y cliente/productos/total. |
| Stock insuficiente | Pedido mayor al inventario | Se rechaza y no se descuenta stock. |
| Transición inválida | Retroceso desde `En camino` | Se rechaza y se conserva el estado. |

## Decisiones de negocio

- El correo es obligatorio porque es el canal de notificación del pedido; no se permite omitirlo.
- El cierre de la entrada (`EOF`) cancela la operación en lugar de completar datos con valores ficticios.
- La vigencia promocional se evalúa con la fecha del pedido contra la ventana móvil de ±7 días; por eso un pedido antiguo puede quedar fuera de la promoción actual.
- Solo se combinan pedidos del mismo cliente (según `equals` de nombre y correo). La incompatibilidad se informa con `null`, sin excepciones.
- Al fusionar cantidades, cada línea respeta `1..100`; si la suma excede el máximo se conservan líneas separadas.
- Los montos se mantienen en `Double` y se redondean al mostrar. Migrar a `BigDecimal` queda como mejora futura para reducir errores de precisión.

## Requisitos

- JDK 21 o superior.
- IntelliJ IDEA con el plugin de Kotlin, o `kotlinc` instalado.

No requiere Gradle ni librerías externas. La reflection utiliza las clases del JDK.

## Ejecutar en IntelliJ IDEA

1. Abrir la carpeta del proyecto.
2. Verificar que `src` esté marcada como **Sources Root**.
3. Abrir `src/Main.kt` y ejecutar `main()` con el botón Run.

## Ejecutar con kotlinc

```powershell
$archivos = Get-ChildItem src -Recurse -Filter *.kt | ForEach-Object { $_.FullName }
kotlinc $archivos -include-runtime -d pizzeria.jar
java -jar pizzeria.jar
```

Si `kotlinc` no está en el `PATH`, compila desde IntelliJ IDEA o usa el `kotlinc.bat` incluido en la instalación del IDE.
