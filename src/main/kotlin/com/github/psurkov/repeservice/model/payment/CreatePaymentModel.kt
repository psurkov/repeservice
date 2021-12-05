package com.github.psurkov.repeservice.model.payment

import kotlinx.datetime.LocalDateTime

data class CreatePaymentModel(
    val from: LocalDateTime,
    val to: LocalDateTime,
    val studentId: Long,
    val tutorId: Long,
)