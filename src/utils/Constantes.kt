package utils

const val PATRON_TELEFONO: String = """^\+?(?=[\d() -]*\d)[\d() -]+$"""

// El correo exige segmentos válidos: sin puntos iniciales, finales ni consecutivos.
const val PATRON_CORREO: String =
    """^[A-Za-z0-9_%+-]+(\.[A-Za-z0-9_%+-]+)*@([A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?\.)+[A-Za-z]{2,63}$"""

const val UMBRAL_DESCUENTO_PIZZAS: Int = 3
const val TASA_DESCUENTO: Double = 0.10
const val TASA_DESCUENTO_PROMOCIONAL: Double = 0.05
const val TASA_IMPUESTO: Double = 0.19
const val DIAS_PROMOCION: Int = 7

val RANGO_CANTIDAD_PERMITIDA: IntRange = 1..100
