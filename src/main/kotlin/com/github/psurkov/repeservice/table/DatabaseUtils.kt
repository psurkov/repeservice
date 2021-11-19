package com.github.psurkov.repeservice.table

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

val tables = arrayOf<Table>(Tutors) // todo use reflekt

suspend fun <T> dbQuery(block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction { block() }
    }

fun clearDatabaseAndCreateEmpty() {
    transaction {
        SchemaUtils.drop(*tables)
        SchemaUtils.create(*tables)
    }
}
