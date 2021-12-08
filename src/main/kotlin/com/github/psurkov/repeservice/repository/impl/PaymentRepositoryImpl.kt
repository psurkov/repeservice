package com.github.psurkov.repeservice.repository.impl

import com.github.psurkov.repeservice.model.payment.CreatePaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentStatus
import com.github.psurkov.repeservice.repository.PaymentRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class PaymentRepositoryImpl : PaymentRepository {
    private fun ResultRow.fromRow() =
        PaymentModel(
            this[PaymentTable.paymentId],
            this[PaymentTable.from],
            this[PaymentTable.to],
            this[PaymentTable.studentId],
            this[PaymentTable.tutorId],
            this[PaymentTable.status],
        )

    override suspend fun findById(paymentId: Long): PaymentModel? =
        PaymentTable.select { PaymentTable.paymentId eq paymentId }.singleOrNull()?.fromRow()

    override suspend fun updateStatus(paymentId: Long, paymentStatus: PaymentStatus) {
        PaymentTable.update({ PaymentTable.paymentId eq paymentId }) { it[status] = paymentStatus }
    }

    override suspend fun insertPayment(createPaymentModel: CreatePaymentModel): PaymentModel = with(PaymentTable) {
        insert {
            it[from] = createPaymentModel.from
            it[to] = createPaymentModel.to
            it[studentId] = createPaymentModel.studentId
            it[tutorId] = createPaymentModel.tutorId
            it[status] = PaymentStatus.UNPAID
        }.resultedValues!![0].fromRow()
    }

    override suspend fun findPaymentByStatusFor(studentId: Long, paymentStatus: PaymentStatus): List<PaymentModel> =
        PaymentTable.select { (PaymentTable.studentId eq studentId) and (PaymentTable.status eq paymentStatus) }
            .map { it.fromRow() }

    object PaymentTable : AppTable() {
        val paymentId = long("paymentId").autoIncrement()
        val from = datetime("from")
        val to = datetime("to")
        val studentId = long("studentId").references(
            StudentRepositoryImpl.StudentTable.id,
            onDelete = ReferenceOption.CASCADE
        )
        val tutorId = long("tutorId").references(
            TutorRepositoryImpl.TutorTable.id,
            onDelete = ReferenceOption.CASCADE
        )
        val status = enumeration("status", PaymentStatus::class)

        override val primaryKey = PrimaryKey(paymentId)
    }
}
