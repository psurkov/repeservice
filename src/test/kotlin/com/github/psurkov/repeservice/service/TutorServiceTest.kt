package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.table.TutorTable
import com.github.psurkov.repeservice.table.dbQuery
import com.github.psurkov.repeservice.table.initDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.select
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TutorServiceTest {

    @Autowired
    private lateinit var tutorService: TutorService

    @BeforeEach
    fun prepareDatabase() {
        initDatabase()
    }

    @Test
    fun testCreateNew() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        Assertions.assertEquals("test username", tutor.username)
        Assertions.assertEquals("12345", tutor.password)

        val row = dbQuery { TutorTable.select { TutorTable.id eq tutor.id }.single() }
        Assertions.assertEquals(tutor.id, row[TutorTable.id])
        Assertions.assertEquals(tutor.username, row[TutorTable.username])
        Assertions.assertEquals(tutor.password, row[TutorTable.password])
    }
}