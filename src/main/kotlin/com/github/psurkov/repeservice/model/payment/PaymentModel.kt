package com.github.psurkov.repeservice.model.payment

import kotlinx.datetime.LocalDateTime

data class PaymentModel(
    val paymentId: Long,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val studentId: Long,
    val tutorId: Long,
    val status: PaymentStatus,
)