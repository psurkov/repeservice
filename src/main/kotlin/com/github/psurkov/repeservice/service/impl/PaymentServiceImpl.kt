package com.github.psurkov.repeservice.service.impl

import com.github.psurkov.repeservice.exception.*
import com.github.psurkov.repeservice.model.payment.CreatePaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentStatus
import com.github.psurkov.repeservice.repository.BasicPriceRepository
import com.github.psurkov.repeservice.repository.PaymentRepository
import com.github.psurkov.repeservice.repository.StudentRepository
import com.github.psurkov.repeservice.repository.TutorRepository
import com.github.psurkov.repeservice.service.PaymentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val basicPriceRepository: BasicPriceRepository,
    private val studentRepository: StudentRepository,
    private val tutorRepository: TutorRepository,
) : PaymentService {
    override suspend fun pay(paymentId: Long) {
        val payment = paymentRepository.findById(paymentId) ?: throw NotFoundPayment()
        if (payment.status == PaymentStatus.PAID) {
            throw AlreadyPaid()
        }
        paymentRepository.updateStatus(paymentId, PaymentStatus.PAID)
    }

    override suspend fun createPayment(createPaymentModel: CreatePaymentModel): PaymentModel {
        studentRepository.findById(createPaymentModel.studentId) ?: throw NotFoundStudent()
        tutorRepository.findById(createPaymentModel.tutorId) ?: throw NotFoundTutor()
        return paymentRepository.insertPayment(createPaymentModel)
    }

    override suspend fun paymentsByStatusFor(studentId: Long, paymentStatus: PaymentStatus): List<PaymentModel> {
        studentRepository.findById(studentId) ?: throw NotFoundStudent()
        return paymentRepository.findPaymentByStatusFor(studentId, paymentStatus)
    }

    override suspend fun setBasicPrice(studentId: Long, tutorId: Long, priceInRubles: Int) {
        studentRepository.findById(studentId) ?: throw NotFoundStudent()
        tutorRepository.findById(tutorId) ?: throw NotFoundTutor()
        if (basicPriceRepository.find(studentId, tutorId) != null) {
            throw AlreadyExistsBasicPrice()
        }
        basicPriceRepository.insertBasicPrice(studentId, tutorId, priceInRubles)
    }

    override suspend fun updateBasicPrice(studentId: Long, tutorId: Long, priceInRubles: Int) {
        basicPriceRepository.find(studentId, tutorId) ?: throw NotFoundBasicPrice()
        basicPriceRepository.updateBasicPrice(studentId, tutorId, priceInRubles)
    }

    override suspend fun getBasicPrice(studentId: Long, tutorId: Long): Int {
        return basicPriceRepository.find(studentId, tutorId) ?: throw NotFoundBasicPrice()
    }
}