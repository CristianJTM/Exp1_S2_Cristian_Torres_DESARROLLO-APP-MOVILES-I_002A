package utils

// Fuente única de identificadores para pedidos registrados y combinados.
object SecuenciaPedidos {
    private var ultimoId: Int = 1000

    fun siguiente(): Int {
        ultimoId += 1
        return ultimoId
    }
}
