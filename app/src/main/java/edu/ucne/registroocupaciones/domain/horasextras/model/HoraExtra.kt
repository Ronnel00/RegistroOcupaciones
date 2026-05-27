package edu.ucne.registroocupaciones.domain.horasextras.model

data class HoraExtra(
    val horaExtraId: Int = 0,
    val empleadoId: Int = 0,
    val fechaDesde: Long = 0L,
    val fechaHasta: Long = 0L,
    val horasTotales: Double = 0.0,
    val horasNocturnas: Double = 0.0,
    val horasNormales: Double = 0.0,
    val horasAl35: Double = 0.0,
    val horasAl100: Double = 0.0,
    val monto35: Double = 0.0,
    val monto100: Double = 0.0,
    val montoNocturno: Double = 0.0,
    val totalAPagar: Double = 0.0
)