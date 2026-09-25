package com.plazaorbita.backend.dto

import java.time.LocalDate
import java.time.LocalTime

data class AppointmentRequest(
    val businessId: Long,
    val serviceName: String,
    val apptDate: LocalDate,
    val apptTime: LocalTime
)
