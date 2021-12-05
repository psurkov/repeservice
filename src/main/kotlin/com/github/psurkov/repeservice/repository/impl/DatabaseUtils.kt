package com.github.psurkov.repeservice.repository.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

sealed class AppTable : Table()

val tables = arrayOf(
    StudentRepositoryImpl.StudentTable,
    StudyGroupRepositoryImpl.StudyGroupParticipantTable,
    StudyGroupRepositoryImpl.StudyGroupTable,
    TutorRepositoryImpl.TutorTable,
    InviteRepositoryImpl.InviteTable,
    CalendarGroupEventRepositoryImpl.GroupEventTable,
    CalendarGroupEventRepositoryImpl.GroupEventTimeTable,
    PaymentRepositoryImpl.PaymentTable,
    BasicPriceRepositoryImpl.BasicPriceTable,
)

fun initDatabase() {
    transaction {
        SchemaUtils.drop(*tables)
        SchemaUtils.create(*tables)
    }
}

suspend fun <T> dbQuery(block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction { block() }
    }

