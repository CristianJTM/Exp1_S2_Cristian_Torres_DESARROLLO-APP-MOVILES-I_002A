package modelo

class Cliente(
    nombre: String,
    contacto: String,
    val correo: String,
    val zona: String
) : Usuario(nombre, contacto) {

    operator fun component1(): String = nombre

    operator fun component2(): String = correo

    operator fun component3(): String = contacto

    override fun describir(): String {
        return "Cliente: $nombre - Teléfono: $contacto - Correo: $correo - Zona: $zona"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is Cliente) {
            return false
        }
        return nombre == other.nombre && correo == other.correo
    }

    override fun hashCode(): Int {
        return 31 * nombre.hashCode() + correo.hashCode()
    }
}
