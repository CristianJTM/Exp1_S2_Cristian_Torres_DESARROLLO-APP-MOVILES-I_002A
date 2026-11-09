package utils

const val TELEFONO_POR_DEFECTO: String = "+00 (000) 000-0000"
const val CORREO_POR_DEFECTO: String = "correo@invalido.com"

const val PATRON_TELEFONO: String = """^\+?(?=[\d() -]*\d)[\d() -]+$"""
const val PATRON_CORREO: String = """^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"""

const val UMBRAL_DESCUENTO_PIZZAS: Int = 3
const val TASA_DESCUENTO: Double = 0.10
const val TASA_DESCUENTO_PROMOCIONAL: Double = 0.05
const val TASA_IMPUESTO: Double = 0.19
const val DIAS_PROMOCION: Int = 7

val RANGO_CANTIDAD_PERMITIDA: IntRange = 1..100
