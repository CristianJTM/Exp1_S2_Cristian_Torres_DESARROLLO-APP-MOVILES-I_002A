package modelo

import utils.RANGO_CANTIDAD_PERMITIDA
import utils.SecuenciaPedidos
import utils.TASA_DESCUENTO_PROMOCIONAL
import utils.calcularDescuento as calcularDescuentoUtil
import utils.calcularDescuentoPromocional as calcularDescuentoPromocionalUtil
import utils.calcularImpuesto as calcularImpuestoUtil
import utils.calcularPorcentajeDescuento
import utils.calcularSubtotal as calcularSubtotalUtil
import utils.calcularTotal as calcularTotalUtil
import utils.estaEnPeriodoPromocional
import utils.formatearMonto
import utils.formatearPorcentaje
import java.time.LocalDate

class Pedido(
    val id: Int,
    val cliente: Cliente,
    itemsIniciales: List<ItemPedido>,
    val notas: String? = null,
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION,
    var repartidor: Repartidor? = null,
    val fecha: LocalDate = LocalDate.now()
) {
    // La colección interna solo se modifica mediante agregarProducto.
    private val itemsInternos: MutableList<ItemPedido> = itemsIniciales.toMutableList()

    val items: List<ItemPedido>
        get() = itemsInternos.toList()

    // Se calcula con la fecha del pedido para mantener coherencia temporal.
    val enPromocion: Boolean
        get() = estaEnPeriodoPromocional(fecha)

    val cantidadPizzas: Int
        get() = items
            .filter { it.producto.categoria == CategoriaProducto.PIZZA }
            .sumOf { it.cantidad }

    operator fun component1(): Cliente = cliente

    operator fun component2(): List<Producto> = items.map { it.producto }

    operator fun component3(): Double = calcularTotal()

    fun agregarProducto(producto: Producto, cantidad: Int) {
        require(cantidad in RANGO_CANTIDAD_PERMITIDA) {
            "La cantidad debe estar entre ${RANGO_CANTIDAD_PERMITIDA.first} y ${RANGO_CANTIDAD_PERMITIDA.last}."
        }
        itemsInternos.add(ItemPedido(producto, cantidad))
    }

    fun calcularSubtotal(): Double = calcularSubtotalUtil(items)

    fun calcularDescuentoPorCantidad(): Double {
        return calcularDescuentoUtil(calcularSubtotal(), cantidadPizzas)
    }

    fun calcularDescuentoPromocional(): Double {
        return calcularDescuentoPromocionalUtil(calcularSubtotal(), enPromocion)
    }

    fun calcularDescuento(): Double {
        return calcularDescuentoPorCantidad() + calcularDescuentoPromocional()
    }

    fun calcularImpuesto(): Double {
        return calcularImpuestoUtil(calcularSubtotal() - calcularDescuento())
    }

    fun calcularTotal(): Double {
        return calcularTotalUtil(calcularSubtotal(), calcularDescuento(), calcularImpuesto())
    }

    fun cambiarEstado(nuevoEstado: EstadoPedido): Boolean {
        if (!esTransicionPermitida(nuevoEstado)) {
            return false
        }

        estado = nuevoEstado
        return true
    }

    private fun esTransicionPermitida(nuevoEstado: EstadoPedido): Boolean {
        return when (estado) {
            EstadoPedido.EN_PREPARACION -> nuevoEstado == EstadoPedido.LISTO
            EstadoPedido.LISTO -> nuevoEstado == EstadoPedido.EN_CAMINO && repartidor != null
            EstadoPedido.EN_CAMINO -> false
        }
    }

    fun asignarRepartidor(nuevoRepartidor: Repartidor) {
        repartidor = nuevoRepartidor
    }

    fun notificarCliente() {
        println("Notificación enviada a ${cliente.nombre} <${cliente.correo}>.")
    }

    fun motivoIncompatibilidad(otro: Pedido): String? {
        if (items.isEmpty() && otro.items.isEmpty()) {
            return "No hay ítems para combinar."
        }
        if (cliente != otro.cliente) {
            return "Los pedidos pertenecen a clientes distintos."
        }
        return null
    }

    operator fun plus(otro: Pedido): Pedido? {
        // Un resultado nulo informa la incompatibilidad sin interrumpir la ejecución.
        if (motivoIncompatibilidad(otro) != null) {
            return null
        }

        val itemsCombinados: MutableList<ItemPedido> = mutableListOf()
        for (item in items + otro.items) {
            val indice: Int = itemsCombinados.indexOfFirst { it.producto == item.producto }

            if (indice >= 0) {
                val actual: ItemPedido = itemsCombinados[indice]
                val cantidadFusionada: Int = actual.cantidad + item.cantidad

                // Si la fusión supera el máximo, se conserva una línea separada.
                if (cantidadFusionada <= RANGO_CANTIDAD_PERMITIDA.last) {
                    itemsCombinados[indice] = actual.copy(cantidad = cantidadFusionada)
                    continue
                }
            }

            itemsCombinados.add(item.copy())
        }

        val notasCombinadas: String = listOfNotNull(
            "Pedido combinado #$id + #${otro.id}",
            notas,
            otro.notas
        ).joinToString(" | ")

        val estadoCombinado: EstadoPedido = if (estado.ordinal >= otro.estado.ordinal) {
            estado
        } else {
            otro.estado
        }
        val repartidorCombinado: Repartidor? = repartidor ?: otro.repartidor

        return Pedido(
            id = SecuenciaPedidos.siguiente(),
            cliente = cliente,
            itemsIniciales = itemsCombinados,
            notas = notasCombinadas,
            estado = estadoCombinado,
            repartidor = repartidorCombinado,
            fecha = if (fecha.isAfter(otro.fecha)) fecha else otro.fecha
        )
    }

    fun generarResumen(): String = buildString {
        val subtotal: Double = calcularSubtotal()
        val descuentoPorCantidad: Double = calcularDescuentoPorCantidad()
        val descuentoPromocional: Double = calcularDescuentoPromocional()
        val descuentoTotal: Double = calcularDescuento()
        val impuesto: Double = calcularImpuesto()
        val total: Double = calcularTotal()
        val porcentajeCantidad: String = formatearPorcentaje(calcularPorcentajeDescuento(cantidadPizzas))
        val porcentajePromocional: String = formatearPorcentaje(TASA_DESCUENTO_PROMOCIONAL)

        appendLine("============================================================")
        appendLine("                 RESUMEN DEL PEDIDO #$id")
        appendLine("============================================================")
        appendLine(cliente.describir())
        appendLine("Fecha: $fecha")
        appendLine("Promoción activa: ${if (enPromocion) "Sí" else "No"}")
        appendLine("Notas: ${notas ?: "Sin notas"}")
        appendLine("------------------------------------------------------------")
        appendLine("Productos:")
        for (item in items) {
            val promocion: String = if (item.producto.esPromocionable()) {
                " | Promoción: ${item.producto.descripcionPromocion()}"
            } else {
                ""
            }
            appendLine(
                "  - ${item.producto.nombre} | Precio: \$${formatearMonto(item.producto.precio)} | " +
                    "Cantidad: ${item.cantidad} | Subtotal: \$${formatearMonto(item.subtotal)}$promocion"
            )
        }
        appendLine("------------------------------------------------------------")
        appendLine("Subtotal: \$${formatearMonto(subtotal)}")
        appendLine("Descuento por cantidad ($porcentajeCantidad): \$${formatearMonto(descuentoPorCantidad)}")
        appendLine("Descuento promocional ($porcentajePromocional): \$${formatearMonto(descuentoPromocional)}")
        appendLine("Descuento total: \$${formatearMonto(descuentoTotal)}")
        appendLine("Impuesto (19%): \$${formatearMonto(impuesto)}")
        appendLine("Total final: \$${formatearMonto(total)}")
        appendLine("Estado: ${estado.descripcion}")
        appendLine("Repartidor: ${repartidor?.nombre ?: "Sin asignar"}")
        appendLine("============================================================")
    }
}
