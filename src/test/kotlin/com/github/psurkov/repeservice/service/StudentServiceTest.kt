package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.repository.impl.StudentRepositoryImpl.StudentTable
import com.github.psurkov.repeservice.repository.impl.dbQuery
import com.github.psurkov.repeservice.repository.impl.initDatabase
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.select
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class StudentServiceTest {

    @Autowired
    private lateinit var studentService: StudentService

    @BeforeEach
    fun prepareDatabase() {
        initDatabase()
    }

    @Test
    fun testCreateNew() = runBlocking {
        val student = studentService.createNew(CreateStudentModel("test username", "12345"))
        Assertions.assertEquals("test username", student.username)
        Assertions.assertEquals("12345", student.password)

        val row = dbQuery { StudentTable.select { StudentTable.id eq student.id }.single() }
        Assertions.assertEquals(student.id, row[StudentTable.id])
        Assertions.assertEquals(student.username, row[StudentTable.username])
        Assertions.assertEquals(student.password, row[StudentTable.password])
    }
}