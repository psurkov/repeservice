package com.github.psurkov.repeservice.repository.impl

import com.github.psurkov.repeservice.repository.BasicPriceRepository
import org.jetbrains.exposed.sql.*
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class BasicPriceRepositoryImpl : BasicPriceRepository {
    override suspend fun find(studentId: Long, tutorId: Long): Int? =
        BasicPriceTable
            .select { (BasicPriceTable.studentId eq studentId) and (BasicPriceTable.tutorId eq tutorId) }
            .singleOrNull()
            ?.get(BasicPriceTable.priceInRubles)

    override suspend fun insertBasicPrice(studentId: Long, tutorId: Long, priceInRubles: Int) {
        BasicPriceTable.insert {
            it[BasicPriceTable.studentId] = studentId
            it[BasicPriceTable.tutorId] = tutorId
            it[BasicPriceTable.priceInRubles] = priceInRubles
        }
    }

    override suspend fun updateBasicPrice(studentId: Long, tutorId: Long, priceInRubles: Int) {
        BasicPriceTable.update({ (BasicPriceTable.studentId eq studentId) and (BasicPriceTable.tutorId eq tutorId) }) {
            it[BasicPriceTable.priceInRubles] = priceInRubles
        }
    }

    object BasicPriceTable : AppTable() {
        val studentId = long("studentId").references(
            StudentRepositoryImpl.StudentTable.id,
            onDelete = ReferenceOption.CASCADE
        )
        val tutorId = long("tutorId").references(
            TutorRepositoryImpl.TutorTable.id,
            onDelete = ReferenceOption.CASCADE
        )
        val priceInRubles = integer("priceInRubles")
        override val primaryKey = PrimaryKey(studentId, tutorId)
    }
}
