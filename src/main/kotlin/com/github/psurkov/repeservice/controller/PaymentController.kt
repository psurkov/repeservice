package com.github.psurkov.repeservice.controller

import com.github.psurkov.repeservice.model.payment.CreatePaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentStatus
import com.github.psurkov.repeservice.service.PaymentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class PaymentController(private val paymentService: PaymentService) {

    @PutMapping("/pay")
    suspend fun pay(
        @RequestParam paymentId: Long,
    ): ResponseEntity<Unit> {
        paymentService.pay(paymentId)
        return ResponseEntity(null, HttpStatus.OK)
    }

    @PostMapping("/payment")
    suspend fun createPayment(
        @RequestBody createPaymentModel: CreatePaymentModel,
    ): ResponseEntity<PaymentModel> {
        val payment = paymentService.createPayment(createPaymentModel)
        return ResponseEntity(payment, HttpStatus.OK)
    }

    @GetMapping("/paid")
    suspend fun getPaid(
        @RequestParam studentId: Long,
    ): ResponseEntity<List<PaymentModel>> {
        val payments = paymentService.paymentsByStatusFor(studentId, PaymentStatus.PAID)
        return ResponseEntity(payments, HttpStatus.OK)
    }

    @GetMapping("/unpaid")
    suspend fun getUnpaid(
        @RequestParam studentId: Long,
    ): ResponseEntity<List<PaymentModel>> {
        val payments = paymentService.paymentsByStatusFor(studentId, PaymentStatus.UNPAID)
        return ResponseEntity(payments, HttpStatus.OK)
    }

    @PostMapping("/basic-price")
    suspend fun setBasicPrice(
        @RequestParam studentId: Long,
        @RequestParam tutorId: Long,
        @RequestParam priceInRubles: Int
    ): ResponseEntity<Unit> {
        paymentService.setBasicPrice(studentId, tutorId, priceInRubles)
        return ResponseEntity(Unit, HttpStatus.OK)
    }

    @PutMapping("/basic-price")
    suspend fun updateBasicPrice(
        @RequestParam studentId: Long,
        @RequestParam tutorId: Long,
        @RequestParam priceInRubles: Int
    ): ResponseEntity<Unit> {
        paymentService.updateBasicPrice(studentId, tutorId, priceInRubles)
        return ResponseEntity(Unit, HttpStatus.OK)
    }

    @GetMapping("/basic-price")
    suspend fun getBasicPrice(
        @RequestParam studentId: Long,
        @RequestParam tutorId: Long,
    ): ResponseEntity<Int> {
        val price = paymentService.getBasicPrice(studentId, tutorId)
        return ResponseEntity(price, HttpStatus.OK)
    }
}