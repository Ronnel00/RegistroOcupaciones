package edu.ucne.registroocupaciones.domain.horasextras.usecase

import javax.inject.Inject

class CalcularHorasExtrasUseCase @Inject constructor() {

    data class ResultadoCalculo(
        val sueldoPorDia: Double,
        val sueldoPorHora: Double,
        val horasNormales: Double,
        val horasExtrasTotales: Double,
        val horasAl35: Double,
        val horasAl100: Double,
        val monto35: Double,
        val monto100: Double,
        val montoNocturno: Double,
        val totalAPagar: Double
    )

    operator fun invoke(
        sueldoMensual: Double,
        horasTotales: Double,
        horasNocturnas: Double
    ): ResultadoCalculo {

        val sueldoPorDia = sueldoMensual / 23.53
        val valorHoraNormal = sueldoPorDia / 8

        val valorHora35 = valorHoraNormal * 1.35
        val valorHora100 = valorHoraNormal * 2.0
        val plusNocturno = valorHoraNormal * 0.15

        val horasNormales = if (horasTotales > 44.0) 44.0 else horasTotales
        val horasExtrasTotales = if (horasTotales > 44.0) horasTotales - 44.0 else 0.0

        val horasAl35 = if (horasExtrasTotales > 24.0) 24.0 else horasExtrasTotales
        val horasAl100 = if (horasExtrasTotales > 24.0) horasExtrasTotales - 24.0 else 0.0

        val monto35 = horasAl35 * valorHora35
        val monto100 = horasAl100 * valorHora100
        val montoNocturno = horasNocturnas * plusNocturno
        val totalAPagar = redondear(monto35 + monto100 + montoNocturno)

        return ResultadoCalculo(
            sueldoPorDia = redondear(sueldoPorDia),
            sueldoPorHora = redondear(valorHoraNormal),
            horasNormales = horasNormales,
            horasExtrasTotales = horasExtrasTotales,
            horasAl35 = horasAl35,
            horasAl100 = horasAl100,
            monto35 = redondear(monto35),
            monto100 = redondear(monto100),
            montoNocturno = redondear(montoNocturno),
            totalAPagar = totalAPagar
        )
    }

    private fun redondear(valor: Double): Double =
        Math.round(valor * 100.0) / 100.0
}