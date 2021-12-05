package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.exception.AlreadyExistsBasicPrice
import com.github.psurkov.repeservice.exception.NotFoundBasicPrice
import com.github.psurkov.repeservice.exception.NotFoundStudent
import com.github.psurkov.repeservice.exception.NotFoundTutor
import com.github.psurkov.repeservice.model.payment.CreatePaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentModel
import com.github.psurkov.repeservice.model.payment.PaymentStatus
import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.repository.impl.BasicPriceRepositoryImpl
import com.github.psurkov.repeservice.repository.impl.PaymentRepositoryImpl
import com.github.psurkov.repeservice.repository.impl.dbQuery
import com.github.psurkov.repeservice.repository.impl.initDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private lateinit var tutorService: TutorService

    @Autowired
    private lateinit var studentService: StudentService

    @Autowired
    private lateinit var paymentService: PaymentService

    @BeforeEach
    fun prepareDatabase() {
        initDatabase()
    }

    @Test
    fun testCreatePayment() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("test username", "qwerty"))
        val fromDate = LocalDateTime(2000, 1, 2, 1, 10, 10)
        val toDate = LocalDateTime(2000, 2, 2, 1, 10, 10)
        val payment = paymentService.createPayment(
            CreatePaymentModel(
                fromDate,
                toDate,
                student.id,
                tutor.id
            )
        )
        Assertions.assertEquals(fromDate, payment.from)
        Assertions.assertEquals(toDate, payment.to)
        Assertions.assertEquals(student.id, payment.studentId)
        Assertions.assertEquals(tutor.id, payment.tutorId)
        Assertions.assertEquals(PaymentStatus.UNPAID, payment.status)

        with(PaymentRepositoryImpl.PaymentTable) {
            val row = dbQuery {
                select { paymentId eq payment.paymentId }.single()
            }
            Assertions.assertEquals(payment.paymentId, row[paymentId])
            Assertions.assertEquals(payment.from, row[from])
            Assertions.assertEquals(payment.to, row[to])
            Assertions.assertEquals(payment.studentId, row[studentId])
            Assertions.assertEquals(payment.tutorId, row[tutorId])
            Assertions.assertEquals(payment.status, row[status])
        }
    }

    @Test
    fun testPay() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("test username", "qwerty"))
        val fromDate = LocalDateTime(2000, 1, 2, 1, 10, 10)
        val toDate = LocalDateTime(2000, 2, 2, 1, 10, 10)
        val payment = paymentService.createPayment(
            CreatePaymentModel(
                fromDate,
                toDate,
                student.id,
                tutor.id
            )
        )

        paymentService.pay(payment.paymentId)

        with(PaymentRepositoryImpl.PaymentTable) {
            val row = dbQuery {
                select { paymentId eq payment.paymentId }.single()
            }
            Assertions.assertEquals(payment.paymentId, row[paymentId])
            Assertions.assertEquals(payment.from, row[from])
            Assertions.assertEquals(payment.to, row[to])
            Assertions.assertEquals(payment.studentId, row[studentId])
            Assertions.assertEquals(payment.tutorId, row[tutorId])
            Assertions.assertEquals(PaymentStatus.PAID, row[status])
        }
    }

    @Test
    fun testPaymentsByStatusFor() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("test username", "qwerty"))
        val fromDate1 = LocalDateTime(2000, 1, 2, 1, 10, 10)
        val toDate1 = LocalDateTime(2000, 2, 2, 1, 10, 10)
        val fromDate2 = toDate1
        val toDate2 = LocalDateTime(2000, 3, 2, 1, 10, 10)
        val payment1 = paymentService.createPayment(
            CreatePaymentModel(
                fromDate1,
                toDate1,
                student.id,
                tutor.id
            )
        )
        val payment2 = paymentService.createPayment(
            CreatePaymentModel(
                fromDate2,
                toDate2,
                student.id,
                tutor.id
            )
        )

        Assertions.assertEquals(
            emptyList<PaymentModel>(),
            paymentService.paymentsByStatusFor(student.id, PaymentStatus.PAID),
        )

        Assertions.assertEquals(
            setOf(payment1, payment2),
            paymentService.paymentsByStatusFor(student.id, PaymentStatus.UNPAID).toSet(),
        )
    }

    @Test
    fun testSetBasicPrice() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("test username", "qwerty"))
        paymentService.setBasicPrice(student.id, tutor.id, 1000)

        with(BasicPriceRepositoryImpl.BasicPriceTable) {
            val row = dbQuery {
                selectAll().single()
            }
            Assertions.assertEquals(student.id, row[studentId])
            Assertions.assertEquals(tutor.id, row[tutorId])
            Assertions.assertEquals(1000, row[priceInRubles])
        }
    }

    @Test
    fun testSetBasicPriceNotFoundStudent(): Unit = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test tutor", "12345"))
        Assertions.assertThrows(NotFoundStudent::class.java) {
            runBlocking {
                paymentService.setBasicPrice(-1, tutor.id, 1000)
            }
        }
    }

    @Test
    fun testSetBasicPriceNotFoundTutor(): Unit = runBlocking {
        val student = studentService.createNew(CreateStudentModel("test username", "qwerty"))
        Assertions.assertThrows(NotFoundTutor::class.java) {
            runBlocking {
                paymentService.setBasicPrice(student.id, -1, 1000)
            }
        }
    }

    @Test
    fun testSetBasicPriceTwice(): Unit = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("test username", "qwerty"))
        paymentService.setBasicPrice(student.id, tutor.id, 1000)
        Assertions.assertThrows(AlreadyExistsBasicPrice::class.java) {
            runBlocking {
                paymentService.setBasicPrice(student.id, tutor.id, 1000)
            }
        }
    }

    @Test
    fun testUpdateBasicPrice() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("test username", "qwerty"))
        paymentService.setBasicPrice(student.id, tutor.id, 1000)
        paymentService.updateBasicPrice(student.id, tutor.id, 2000)

        with(BasicPriceRepositoryImpl.BasicPriceTable) {
            val row = dbQuery {
                selectAll().single()
            }
            Assertions.assertEquals(student.id, row[studentId])
            Assertions.assertEquals(tutor.id, row[tutorId])
            Assertions.assertEquals(2000, row[priceInRubles])
        }
    }


    @Test
    fun testUpdateBasicPriceNotFound(): Unit = runBlocking {
        Assertions.assertThrows(NotFoundBasicPrice::class.java) {
            runBlocking {
                paymentService.updateBasicPrice(-1, -1, 2000)
            }
        }
    }

    @Test
    fun testGetBasicPrice() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("test username", "qwerty"))
        paymentService.setBasicPrice(student.id, tutor.id, 1000)
        Assertions.assertEquals(1000, paymentService.getBasicPrice(student.id, tutor.id))
    }


    @Test
    fun testGetBasicPriceNotFound(): Unit = runBlocking {
        Assertions.assertThrows(NotFoundBasicPrice::class.java) {
            runBlocking {
                paymentService.getBasicPrice(-1, -1)
            }
        }
    }
}