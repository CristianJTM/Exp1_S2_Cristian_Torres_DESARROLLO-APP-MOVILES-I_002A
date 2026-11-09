import modelo.Bebida
import modelo.CategoriaProducto
import modelo.Cliente
import modelo.ComboPromocionable
import modelo.EstadoPedido
import modelo.ItemPedido
import modelo.Pedido
import modelo.Pizza
import modelo.PizzaPromocionable
import modelo.Producto
import modelo.Repartidor
import modelo.Usuario
import servicio.Catalogo
import servicio.Inventario
import servicio.Pizzeria
import servicio.Promocion
import servicio.Promociones
import utils.RANGO_CANTIDAD_PERMITIDA
import utils.Reflexion
import utils.esCorreoValido
import utils.formatearMonto
import utils.formatearTelefono
import utils.leerCantidad
import utils.leerCorreo
import utils.leerNotas
import utils.leerOpcion
import utils.leerSiNo
import utils.leerTelefono
import utils.leerTexto
import java.time.LocalDate

fun main() {
    val pizzeria: Pizzeria = crearPizzeria()

    while (true) {
        mostrarMenuPrincipal()
        val opcion: Int = leerOpcion(1, 8, "Seleccione una opción: ") ?: return

        when (opcion) {
            1 -> registrarPedido(pizzeria)
            2 -> pizzeria.inventario.mostrar()
            3 -> reabastecerInventario(pizzeria)
            4 -> asignarRepartidor(pizzeria)
            5 -> cambiarEstadoPedido(pizzeria)
            6 -> pizzeria.generarReporte()
            7 -> mostrarResumenAvanzado(pizzeria)
            8 -> {
                println("Gracias por usar el sistema. Hasta luego.")
                return
            }
        }
    }
}

fun crearPizzeria(): Pizzeria {
    val catalogo: Catalogo = Catalogo()

    catalogo.agregar(
        PizzaPromocionable(
            "Pepperoni Familiar",
            12990.0,
            mapOf("Masa" to 1, "Salsa" to 1, "Queso" to 2, "Pepperoni" to 1)
        )
    )
    catalogo.agregar(
        Pizza(
            "Margarita Familiar",
            10990.0,
            mapOf("Masa" to 1, "Salsa" to 1, "Queso" to 2, "Tomate" to 1)
        )
    )
    catalogo.agregar(
        ComboPromocionable(
            "Combo Familiar",
            19990.0,
            mapOf("Masa" to 2, "Salsa" to 2, "Queso" to 4, "Pepperoni" to 2)
        )
    )
    catalogo.agregar(Bebida("Bebida Cola 1.5L", 2990.0))

    val duplicado: Producto = Pizza(
        "Pepperoni Familiar",
        9990.0,
        mapOf("Masa" to 1)
    )

    if (!catalogo.agregar(duplicado)) {
        println("Producto duplicado evitado: ${duplicado.nombre}.")
    }

    val inventario: Inventario = Inventario(
        mapOf(
            "Masa" to 40,
            "Salsa" to 40,
            "Queso" to 100,
            "Pepperoni" to 30,
            "Tomate" to 30
        )
    )

    val repartidores: List<Repartidor> = listOf(
        Repartidor("Camila Rojas", "+56 9 1111 1111", "Centro", 1.2),
        Repartidor("Diego Soto", "+56 9 2222 2222", "Centro", 2.5, disponible = false),
        Repartidor("Valentina Díaz", "+56 9 3333 3333", "Norte", 3.0),
        Repartidor("Martín Silva", "+56 9 4444 4444", "Sur", 2.0)
    )

    return Pizzeria(catalogo.listar(), inventario, repartidores)
}

fun mostrarMenuPrincipal() {
    println()
    println("============================================================")
    println("                     PIZZERÍA")
    println("============================================================")
    println("1. Registrar pedido")
    println("2. Ver inventario")
    println("3. Reabastecer ingrediente")
    println("4. Asignar repartidor")
    println("5. Cambiar estado de pedido")
    println("6. Generar reporte del día")
    println("7. Mostrar resumen avanzado")
    println("8. Salir")
}

fun registrarPedido(pizzeria: Pizzeria) {
    println()
    println("=== REGISTRO DE PEDIDO ===")

    val nombre: String = leerTexto("Nombre del cliente: ", "Cliente sin nombre")
    val telefono: String = leerTelefono()
    val correo: String? = leerCorreo()

    println("Zonas: 1. Centro, 2. Norte, 3. Sur")
    val zonaOpcion: Int = leerOpcion(1, 3, "Seleccione zona: ") ?: return
    val zona: String = when (zonaOpcion) {
        1 -> "Centro"
        2 -> "Norte"
        else -> "Sur"
    }

    val notas: String? = leerNotas()
    val cliente: Cliente = Cliente(nombre, telefono, correo, zona)
    val clienteRegistrado: Cliente = pizzeria.registrarCliente(cliente)
    val items: MutableList<ItemPedido> = mutableListOf()

    var agregarOtro: Boolean = true
    while (agregarOtro) {
        pizzeria.mostrarProductos()
        val opcion: Int = leerOpcion(1, pizzeria.productos.size, "Seleccione un producto: ") ?: return
        val producto: Producto? = pizzeria.productos.getOrNull(opcion - 1)
        if (producto == null) {
            println("El producto seleccionado ya no está disponible.")
            return
        }

        val cantidad: Int = leerCantidad("Cantidad de ${producto.nombre}: ")
        items.add(ItemPedido(producto, cantidad))
        agregarOtro = leerSiNo("¿Agregar otro producto? (s/n): ")
    }

    val pedido: Pedido? = pizzeria.registrarPedido(clienteRegistrado, items, notas)

    if (pedido != null) {
        println()
        println(pedido.generarResumen())
        pedido.notificarCliente()
        mostrarParticipantes(pedido)
    }
}

fun reabastecerInventario(pizzeria: Pizzeria) {
    println()
    println("=== REABASTECER INVENTARIO ===")

    val ingredientes: List<String> = pizzeria.inventario.ingredientesDisponibles().sorted()
    if (ingredientes.isEmpty()) {
        println("No hay ingredientes registrados.")
        return
    }

    for ((indice, ingrediente) in ingredientes.withIndex()) {
        println("${indice + 1}. $ingrediente")
    }

    val opcion: Int = leerOpcion(1, ingredientes.size, "Seleccione el ingrediente: ") ?: return
    val ingrediente: String? = ingredientes.getOrNull(opcion - 1)
    if (ingrediente == null) {
        println("El ingrediente seleccionado ya no está disponible.")
        return
    }

    val cantidad: Int = leerCantidad("Cantidad a reponer: ")
    pizzeria.inventario.reponer(ingrediente, cantidad)
}

fun asignarRepartidor(pizzeria: Pizzeria) {
    val pedido: Pedido = seleccionarPedido(pizzeria) ?: return
    pizzeria.asignarRepartidor(pedido)
    println()
    println(pedido.generarResumen())
    mostrarParticipantes(pedido)
}

fun cambiarEstadoPedido(pizzeria: Pizzeria) {
    val pedido: Pedido = seleccionarPedido(pizzeria) ?: return

    println("Estado actual: ${pedido.estado.descripcion}")
    println("1. En preparación")
    println("2. Listo")
    println("Para pasar a En camino, asigne un repartidor desde la opción 4.")

    val opcionEstado: Int = leerOpcion(1, 2, "Seleccione el nuevo estado: ") ?: return
    val nuevoEstado: EstadoPedido = when (opcionEstado) {
        1 -> EstadoPedido.EN_PREPARACION
        else -> EstadoPedido.LISTO
    }

    val aplicado: Boolean = pedido.cambiarEstado(nuevoEstado)
    if (aplicado) {
        println("Estado actualizado: ${pedido.estado.descripcion}")
    } else {
        println("Transición no permitida: ${pedido.estado.descripcion} -> ${nuevoEstado.descripcion}.")
        println("Secuencia permitida: En preparación -> Listo -> En camino.")
    }

    println()
    println(pedido.generarResumen())
}

fun seleccionarPedido(pizzeria: Pizzeria): Pedido? {
    val pedidos: List<Pedido> = pizzeria.listarPedidos()

    if (pedidos.isEmpty()) {
        println("No hay pedidos registrados.")
        return null
    }

    println()
    println("=== PEDIDOS REGISTRADOS ===")
    for ((indice, pedido) in pedidos.withIndex()) {
        println("${indice + 1}. Pedido #${pedido.id} - ${pedido.cliente.nombre} - ${pedido.estado.descripcion}")
    }

    val opcion: Int = leerOpcion(1, pedidos.size, "Seleccione un pedido: ") ?: return null
    val pedido: Pedido? = pizzeria.obtenerPedido(opcion - 1)
    if (pedido == null) {
        println("El pedido seleccionado ya no existe.")
    }
    return pedido
}

fun mostrarParticipantes(pedido: Pedido) {
    val usuarios: List<Usuario> = listOfNotNull<Usuario>(pedido.cliente, pedido.repartidor)
    println()
    println("Participantes del pedido:")
    usuarios.forEach { println("  ${it.describir()}") }
}

fun mostrarResumenAvanzado(pizzeria: Pizzeria) {
    println()
    println("============================================================")
    println("             RESUMEN DE FUNCIONALIDADES AVANZADAS")
    println("============================================================")

    println("--- Rango de promoción ---")
    println("Desde ${Promocion.inicio} hasta ${Promocion.fin}.")
    println("Promoción activa hoy: ${Promocion.estaActiva()}.")
    println("Promoción activa dentro de un año: ${Promocion.estaActiva(LocalDate.now().plusYears(1))}.")
    println("Rango permitido de cantidades: ${RANGO_CANTIDAD_PERMITIDA.first}-${RANGO_CANTIDAD_PERMITIDA.last}.")
    println("Correo válido 'cliente@demo.com': ${esCorreoValido("cliente@demo.com")}.")
    println("Correo inválido 'correo-malo': ${esCorreoValido("correo-malo")}.")
    println("Teléfono formateado de ejemplo: ${formatearTelefono("+56 9 1234 5678")}.")
    println(
        "Repartidores disponibles: ${pizzeria.repartidores.count { it.disponible }} " +
            "de ${pizzeria.repartidores.size}."
    )

    println("--- Productos promocionables (@Promocionable) ---")
    val promocionables: List<Producto> = Promociones.listarPromocionables(pizzeria.productos)
    if (promocionables.isEmpty()) {
        println("No hay productos promocionables.")
    } else {
        for (producto in promocionables) {
            println("- ${producto.nombre}: ${Promociones.descripcion(producto)}")
        }
    }
    println("Productos en catálogo sin duplicados: ${pizzeria.productos.size}.")

    println("--- Reflection ---")
    Reflexion.listarMiembros(Producto::class.java)
    Reflexion.listarMiembros(PizzaPromocionable::class.java)
    Reflexion.listarMiembros(Pedido::class.java)
    Reflexion.listarMiembros(Cliente::class.java)

    if (pizzeria.productos.size < 2) {
        println("No hay suficientes productos para el resumen de pedidos.")
        return
    }

    val pedidosReales: List<Pedido> = pizzeria.listarPedidos()
    val pedidoA: Pedido
    val pedidoB: Pedido

    if (pedidosReales.size >= 2 && pedidosReales[0].cliente == pedidosReales[1].cliente) {
        pedidoA = pedidosReales[0]
        pedidoB = pedidosReales[1]
        println("--- Pedidos reales ---")
        println("Se usarán los dos primeros pedidos de ${pedidoA.cliente.nombre}.")
    } else {
        val clienteDemo: Cliente = Cliente(
            "Cliente Demo",
            formatearTelefono("+56 9 1234 5678") ?: "+56 9 1234 5678",
            "cliente@demo.com",
            "Centro"
        )
        pedidoA = Pedido(
            id = 9001,
            cliente = clienteDemo,
            items = mutableListOf(ItemPedido(pizzeria.productos[0], 2)),
            notas = "Pedido de demostración A",
            enPromocion = true
        )
        pedidoB = Pedido(
            id = 9002,
            cliente = clienteDemo,
            items = mutableListOf(ItemPedido(pizzeria.productos[1], 3)),
            notas = "Pedido de demostración B",
            enPromocion = true
        )
        println("--- Pedidos de demostración ---")
        println("No hay dos pedidos reales del mismo cliente; se usan pedidos de demostración.")
    }

    println("--- Cliente desestructurado ---")
    val (nombreCliente, correoCliente, telefonoCliente) = pedidoA.cliente
    println("Nombre: $nombreCliente")
    println("Correo: $correoCliente")
    println("Teléfono: $telefonoCliente")

    println("--- Pedidos combinados con + ---")
    val pedidoCombinado: Pedido = try {
        pedidoA + pedidoB
    } catch (e: IllegalArgumentException) {
        println("No se pudieron combinar los pedidos: ${e.message}")
        return
    }
    println(pedidoCombinado.generarResumen())

    println("--- Pedido desestructurado ---")
    val (clientePedido, productosPedido, totalPedido) = pedidoCombinado
    println("Cliente: ${clientePedido.nombre}")
    println("Productos: ${productosPedido.joinToString { it.nombre }}")
    println("Total: \$${formatearMonto(totalPedido)}")

    println("--- Igualdad de objetos ---")
    val clienteBase: Cliente = pedidoA.cliente
    val clienteDuplicado: Cliente = Cliente(
        clienteBase.nombre,
        "+56 (9) 0000-0000",
        clienteBase.correo,
        "Sur"
    )
    println("Clientes iguales (nombre y correo): ${clienteBase == clienteDuplicado}")
    println("Clientes únicos en Set: ${setOf(clienteBase, clienteDuplicado).size}")

    val productoBase: Producto? = pizzeria.productos.firstOrNull {
        it.categoria == CategoriaProducto.PIZZA
    }
    if (productoBase != null) {
        val productoDuplicado: Producto = Pizza(
            productoBase.nombre,
            9999.0,
            mapOf("Masa" to 1)
        )
        println("Productos iguales (nombre y categoría): ${productoBase == productoDuplicado}")
        println("Productos únicos en Set: ${setOf(productoBase, productoDuplicado).size}")
    } else {
        println("No hay pizzas para la comparación de igualdad.")
    }
}
