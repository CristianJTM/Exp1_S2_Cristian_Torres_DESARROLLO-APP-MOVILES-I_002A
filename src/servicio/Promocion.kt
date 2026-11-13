package servicio

import utils.estaEnPeriodoPromocional
import utils.finPromocion
import utils.inicioPromocion
import java.time.LocalDate

object Promocion {
    val inicio: LocalDate
        get() = inicioPromocion()

    val fin: LocalDate
        get() = finPromocion()

    fun estaActiva(fecha: LocalDate = LocalDate.now()): Boolean {
        return estaEnPeriodoPromocional(fecha)
    }
}
