package edu.ucne.registroocupaciones.data.mapper

import edu.ucne.registroocupaciones.data.local.entities.HoraExtraEntity
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra

fun HoraExtraEntity.toDomain(): HoraExtra =
    HoraExtra(
        horaExtraId = horaExtraId,
        empleadoId = empleadoId,
        fechaDesde = fechaDesde,
        fechaHasta = fechaHasta,
        horasTotales = horasTotales,
        horasNocturnas = horasNocturnas,
        horasNormales = horasNormales,
        horasAl35 = horasAl35,
        horasAl100 = horasAl100,
        monto35 = monto35,
        monto100 = monto100,
        montoNocturno = montoNocturno,
        totalAPagar = totalAPagar
    )

fun HoraExtra.toEntity(): HoraExtraEntity =
    HoraExtraEntity(
        horaExtraId = horaExtraId,
        empleadoId = empleadoId,
        fechaDesde = fechaDesde,
        fechaHasta = fechaHasta,
        horasTotales = horasTotales,
        horasNocturnas = horasNocturnas,
        horasNormales = horasNormales,
        horasAl35 = horasAl35,
        horasAl100 = horasAl100,
        monto35 = monto35,
        monto100 = monto100,
        montoNocturno = montoNocturno,
        totalAPagar = totalAPagar
    )