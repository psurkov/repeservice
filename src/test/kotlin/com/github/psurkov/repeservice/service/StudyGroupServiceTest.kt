package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.table.StudyGroupTable
import com.github.psurkov.repeservice.table.initDatabase
import com.github.psurkov.repeservice.table.dbQuery
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.select
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class StudyGroupServiceTest {

    @Autowired
    private lateinit var tutorService: TutorService

    @Autowired
    private lateinit var studyGroupService: StudyGroupService

    @BeforeEach
    fun clearDatabase() {
        initDatabase()
    }

    @Test
    fun createNewStudyGroup() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        Assertions.assertEquals("test name", studyGroup.name)
        Assertions.assertEquals(tutor.id, studyGroup.ownerTutorId)
        Assertions.assertEquals(emptyList<Int>(), studyGroup.participantIds)

        val row = dbQuery { StudyGroupTable.select { StudyGroupTable.id eq studyGroup.id }.single() }
        Assertions.assertEquals(studyGroup.id, row[StudyGroupTable.id])
        Assertions.assertEquals("test name", row[StudyGroupTable.name])
        Assertions.assertEquals(studyGroup.ownerTutorId, row[StudyGroupTable.ownerTutorId])
    }
}