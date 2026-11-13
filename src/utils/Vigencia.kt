package utils

import java.time.LocalDate

fun inicioPromocion(hoy: LocalDate = LocalDate.now()): LocalDate =
    hoy.minusDays(DIAS_PROMOCION.toLong())

fun finPromocion(hoy: LocalDate = LocalDate.now()): LocalDate =
    hoy.plusDays(DIAS_PROMOCION.toLong())

// La vigencia se evalúa con la fecha consultada, no con un valor congelado al iniciar.
fun estaEnPeriodoPromocional(fecha: LocalDate, hoy: LocalDate = LocalDate.now()): Boolean {
    val inicio: LocalDate = inicioPromocion(hoy)
    val fin: LocalDate = finPromocion(hoy)
    return fecha.toEpochDay() in inicio.toEpochDay()..fin.toEpochDay()
}
