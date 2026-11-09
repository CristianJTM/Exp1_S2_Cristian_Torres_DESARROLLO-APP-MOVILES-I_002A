package modelo

class Repartidor(
    nombre: String,
    contacto: String,
    val zona: String,
    val distanciaKm: Double,
    val disponible: Boolean = true
) : Usuario(nombre, contacto) {

    fun entregarPedido(pedido: Pedido) {
        when (pedido.estado) {
            EstadoPedido.EN_CAMINO -> {
                println("El pedido #${pedido.id} ya está en camino.")
                return
            }
            EstadoPedido.EN_PREPARACION -> {
                println("No se puede entregar el pedido #${pedido.id}: primero debe estar Listo.")
                return
            }
            EstadoPedido.LISTO -> {
                pedido.asignarRepartidor(this)

                if (pedido.cambiarEstado(EstadoPedido.EN_CAMINO)) {
                    println("$nombre está entregando el pedido #${pedido.id}.")
                } else {
                    println("No se pudo actualizar el estado del pedido #${pedido.id}.")
                }
            }
        }
    }

    override fun describir(): String {
        return "Repartidor: $nombre - Teléfono: $contacto - Zona: $zona - Distancia: $distanciaKm km"
    }
}
