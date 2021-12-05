package com.github.psurkov.repeservice.repository

import com.github.psurkov.repeservice.model.payment.CreatePaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentStatus

interface PaymentRepository {
    suspend fun findById(paymentId: Long): PaymentModel?
    suspend fun updateStatus(paymentId: Long, paymentStatus: PaymentStatus)
    suspend fun insertPayment(createPaymentModel: CreatePaymentModel): PaymentModel
    suspend fun findPaymentByStatusFor(studentId: Long, paymentStatus: PaymentStatus): List<PaymentModel>
}