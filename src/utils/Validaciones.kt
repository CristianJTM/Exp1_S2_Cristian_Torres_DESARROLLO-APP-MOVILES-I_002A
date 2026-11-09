package utils

private val FORMATO_TELEFONO: Regex = Regex(PATRON_TELEFONO)
private val FORMATO_CORREO: Regex = Regex(PATRON_CORREO)

fun esCorreoValido(correo: String): Boolean = FORMATO_CORREO.matches(correo)

fun esTelefonoValido(telefono: String): Boolean {
    return FORMATO_TELEFONO.matches(telefono) && formatearTelefono(telefono) != null
}

fun leerTexto(mensaje: String, valorPorDefecto: String): String {
    print(mensaje)
    val entrada: String = readlnOrNull()?.trim().orEmpty()
    return if (entrada.isEmpty()) valorPorDefecto else entrada
}

fun leerTelefono(): String {
    while (true) {
        print("Teléfono: ")
        val entrada: String = readlnOrNull()?.trim() ?: return TELEFONO_POR_DEFECTO

        if (!FORMATO_TELEFONO.matches(entrada)) {
            println("Teléfono inválido. Use dígitos, un + opcional al inicio y separadores simples.")
            continue
        }

        val formateado: String? = formatearTelefono(entrada)
        if (formateado != null) {
            return formateado
        }

        println("El teléfono debe tener 9, 11 o 12 dígitos.")
    }
}

fun leerCorreo(): String? {
    while (true) {
        print("Correo (opcional, Enter para omitir): ")
        val correo: String = readlnOrNull()?.trim() ?: return null

        if (correo.isEmpty()) {
            return null
        }

        if (esCorreoValido(correo)) {
            return correo
        }

        println("Correo inválido. Ingrese un correo con formato nombre@dominio.com.")
    }
}

fun leerNotas(): String? {
    print("Notas del pedido (opcional): ")
    return readlnOrNull()?.trim()?.takeIf { it.isNotEmpty() }
}

fun leerCantidad(mensaje: String): Int {
    while (true) {
        print(mensaje)
        val entrada: String? = readlnOrNull()

        if (entrada == null) {
            println("No se recibió una cantidad. Se usará 1 por defecto.")
            return 1
        }

        val cantidad: Int = try {
            entrada.trim().toInt()
        } catch (_: NumberFormatException) {
            println("Error: ingrese un número entero.")
            continue
        }

        if (cantidad in RANGO_CANTIDAD_PERMITIDA) {
            return cantidad
        }

        println(
            "Error: la cantidad debe estar entre " +
                "${RANGO_CANTIDAD_PERMITIDA.first} y ${RANGO_CANTIDAD_PERMITIDA.last}."
        )
    }
}

fun leerOpcion(minimo: Int, maximo: Int, mensaje: String): Int? {
    while (true) {
        print(mensaje)
        val entrada: String = readlnOrNull()?.trim() ?: return null

        val opcion: Int? = entrada.toIntOrNull()
        if (opcion != null && opcion in minimo..maximo) {
            return opcion
        }

        println("Opción inválida. Ingrese un número entre $minimo y $maximo.")
    }
}

fun leerSiNo(mensaje: String): Boolean {
    while (true) {
        print(mensaje)
        val entrada: String = readlnOrNull()?.trim()?.lowercase() ?: return false

        when (entrada) {
            "s", "si", "sí" -> return true
            "n", "no" -> return false
            else -> println("Ingrese s o n.")
        }
    }
}
