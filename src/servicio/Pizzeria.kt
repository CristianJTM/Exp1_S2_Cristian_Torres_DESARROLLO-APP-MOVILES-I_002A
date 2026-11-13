package servicio

import modelo.Cliente
import modelo.EstadoPedido
import modelo.ItemPedido
import modelo.Pedido
import modelo.Producto
import modelo.Repartidor
import utils.SecuenciaPedidos
import utils.detallarProducto
import utils.formatearMonto
import java.time.LocalDate

class Pizzeria(
    val productos: List<Producto>,
    val inventario: Inventario,
    val repartidores: List<Repartidor>
) {
    private val pedidos: MutableList<Pedido> = mutableListOf()
    private val clientes: MutableList<Cliente> = mutableListOf()

    fun mostrarProductos() {
        println()
        println("=== CATÁLOGO DE PIZZAS ===")
        for ((indice, producto) in productos.withIndex()) {
            println("${indice + 1}. ${producto.detallarProducto()}")
        }
    }

    fun registrarCliente(cliente: Cliente): Cliente {
        val existente: Cliente? = clientes.find { it == cliente }

        if (existente != null) {
            println("El cliente ya está registrado: ${existente.nombre}.")
            return existente
        }

        clientes.add(cliente)
        return cliente
    }

    fun registrarPedido(cliente: Cliente, items: List<ItemPedido>, notas: String?): Pedido? {
        if (items.isEmpty()) {
            println("El pedido debe tener al menos un producto.")
            return null
        }

        val faltantes: Map<String, Int> = verificarDisponibilidad(items)

        return when {
            faltantes.isNotEmpty() -> {
                informarFaltantes(faltantes)
                null
            }
            else -> {
                inventario.actualizarInventario(items)
                crearYRegistrarPedido(cliente, items, notas)
            }
        }
    }

    private fun verificarDisponibilidad(items: List<ItemPedido>): Map<String, Int> {
        return inventario.ingredientesFaltantes(items)
    }

    private fun informarFaltantes(faltantes: Map<String, Int>) {
        println()
        println("No hay ingredientes suficientes para preparar el pedido:")
        for ((ingrediente, cantidad) in faltantes) {
            println("- $ingrediente: faltan $cantidad unidades.")
        }
        println("Pasos para reabastecer:")
        println("1. Revisar el ingrediente faltante.")
        println("2. Registrar la compra o entrada al inventario.")
        println("3. Reponerlo desde la opción de inventario.")
        println("4. Verificar nuevamente la disponibilidad.")
    }

    private fun crearYRegistrarPedido(cliente: Cliente, items: List<ItemPedido>, notas: String?): Pedido {
        val fecha: LocalDate = LocalDate.now()
        val pedido: Pedido = Pedido(
            id = SecuenciaPedidos.siguiente(),
            cliente = cliente,
            itemsIniciales = items,
            notas = notas,
            fecha = fecha
        )
        pedidos.add(pedido)
        println("Pedido #${pedido.id} registrado. Inventario actualizado.")
        return pedido
    }

    fun asignarRepartidor(pedido: Pedido): Repartidor? {
        return when (pedido.estado) {
            EstadoPedido.EN_CAMINO -> {
                println("El pedido #${pedido.id} ya está en camino.")
                null
            }
            EstadoPedido.EN_PREPARACION -> {
                println("El pedido #${pedido.id} debe estar Listo antes de asignar un repartidor.")
                null
            }
            EstadoPedido.LISTO -> {
                val repartidor: Repartidor? = repartidores
                    .filter { it.zona == pedido.cliente.zona && it.disponible }
                    .minByOrNull { it.distanciaKm }

                repartidor?.let {
                    it.entregarPedido(pedido)
                    println("Repartidor asignado: ${it.describir()}")
                } ?: println("No hay repartidores disponibles en la zona ${pedido.cliente.zona}.")

                repartidor
            }
        }
    }

    fun listarPedidos(): List<Pedido> = pedidos.toList()

    fun obtenerPedido(indice: Int): Pedido? = pedidos.getOrNull(indice)

    fun generarReporte() {
        val pedidosDelDia: Array<Pedido> = pedidos.toTypedArray()

        if (pedidosDelDia.isEmpty()) {
            println("No hay pedidos registrados hoy.")
            return
        }

        println()
        println("============================================================")
        println("                 REPORTE DEL DÍA")
        println("============================================================")

        for (pedido in pedidosDelDia) {
            val detallePizzas: String = pedido.items.joinToString { "${it.producto.nombre} x${it.cantidad}" }
            println(
                "Pedido #${pedido.id} | Cliente: ${pedido.cliente.nombre} | " +
                    "Pizzas: $detallePizzas | " +
                    "Total: \$${formatearMonto(pedido.calcularTotal())} | " +
                    "Estado: ${pedido.estado.descripcion}"
            )
        }

        val completados: List<Pedido> = pedidosDelDia.filter {
            it.estado == EstadoPedido.LISTO || it.estado == EstadoPedido.EN_CAMINO
        }
        val pendientes: List<Pedido> = pedidosDelDia.filter {
            it.estado == EstadoPedido.EN_PREPARACION
        }

        println("------------------------------------------------------------")
        println("Pedidos listos o en camino: ${completados.size}")
        for (pedido in completados) {
            println("  #${pedido.id} - ${pedido.cliente.nombre} - ${pedido.estado.descripcion}")
        }

        println("Pedidos pendientes: ${pendientes.size}")
        for (pedido in pendientes) {
            println("  #${pedido.id} - ${pedido.cliente.nombre} - ${pedido.estado.descripcion}")
        }
        println("============================================================")
    }
}
