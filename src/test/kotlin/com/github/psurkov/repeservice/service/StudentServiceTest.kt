package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.exception.NotFoundInvite
import com.github.psurkov.repeservice.model.invite.InviteStatus
import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.repository.StudyGroupRepository
import com.github.psurkov.repeservice.table.InviteTable
import com.github.psurkov.repeservice.table.StudentTable
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
class StudentServiceTest {

    @Autowired
    private lateinit var studyGroupRepository: StudyGroupRepository

    @Autowired
    private lateinit var tutorService: TutorService

    @Autowired
    private lateinit var studentService: StudentService

    @Autowired
    private lateinit var studyGroupService: StudyGroupService

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

    @Test
    fun testAcceptInvite() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("student", "qwerty"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "group"))
        val invite = studyGroupService.invite(studyGroup.id, student.id)
        studentService.acceptInvite(invite.id)

        val updatedStudyGroup = studyGroupRepository.findById(studyGroup.id)!!
        Assertions.assertEquals(listOf(student.id), updatedStudyGroup.participantIds)
        val status = dbQuery {
            InviteTable.select { InviteTable.id eq invite.id }
                .map { it[InviteTable.status] }
                .single()
        }
        Assertions.assertEquals(InviteStatus.ACCEPTED, status)
    }

    @Test
    fun testAcceptNotFoundInvite() {
        Assertions.assertThrows(NotFoundInvite::class.java) {
            runBlocking {
                studentService.acceptInvite(-1)
            }
        }
    }

    @Test
    fun testRejectInvite() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("student", "qwerty"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "group"))
        val invite = studyGroupService.invite(studyGroup.id, student.id)
        studentService.rejectInvite(invite.id)

        val updatedStudyGroup = studyGroupRepository.findById(studyGroup.id)!!
        Assertions.assertEquals(emptyList<Long>(), updatedStudyGroup.participantIds)
        val status = dbQuery {
            InviteTable.select { InviteTable.id eq invite.id }
                .map { it[InviteTable.status] }
                .single()
        }
        Assertions.assertEquals(InviteStatus.REJECTED, status)
    }

    @Test
    fun testRejectNotFoundInvite() {
        Assertions.assertThrows(NotFoundInvite::class.java) {
            runBlocking {
                studentService.rejectInvite(-1)
            }
        }
    }
}