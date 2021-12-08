package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.payment.CreatePaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentStatus

interface PaymentService {
    suspend fun pay(paymentId: Long)
    suspend fun createPayment(createPaymentModel: CreatePaymentModel): PaymentModel
    suspend fun paymentsByStatusFor(studentId: Long, paymentStatus: PaymentStatus): List<PaymentModel>
    suspend fun setBasicPrice(studentId: Long, tutorId: Long, priceInRubles: Int)
    suspend fun updateBasicPrice(studentId: Long, tutorId: Long, priceInRubles: Int)
    suspend fun getBasicPrice(studentId: Long, tutorId: Long): Int
}