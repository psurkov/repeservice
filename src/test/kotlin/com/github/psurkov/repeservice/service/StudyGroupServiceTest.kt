package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.exception.*
import com.github.psurkov.repeservice.model.invite.InviteStatus
import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.repository.StudyGroupRepository
import com.github.psurkov.repeservice.table.InviteTable
import com.github.psurkov.repeservice.table.StudyGroupTable
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
class StudyGroupServiceTest {

    @Autowired
    private lateinit var studyGroupRepository: StudyGroupRepository

    @Autowired
    private lateinit var tutorService: TutorService

    @Autowired
    private lateinit var studentService: StudentService

    @Autowired
    private lateinit var studyGroupService: StudyGroupService

    @BeforeEach
    fun clearDatabase() {
        initDatabase()
    }

    @Test
    fun testCreateNewStudyGroup() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        Assertions.assertEquals("test name", studyGroup.name)
        Assertions.assertEquals(tutor.id, studyGroup.ownerTutorId)
        Assertions.assertEquals(emptyList<Int>(), studyGroup.participantIds)

        val row = dbQuery { StudyGroupTable.select { StudyGroupTable.id eq studyGroup.id }.single() }
        Assertions.assertEquals(studyGroup.id, row[StudyGroupTable.id])
        Assertions.assertEquals(studyGroup.name, row[StudyGroupTable.name])
        Assertions.assertEquals(studyGroup.ownerTutorId, row[StudyGroupTable.ownerTutorId])
    }

    @Test
    fun testInvite() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("student", "qwerty"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "group"))

        val invite = studyGroupService.invite(studyGroup.id, student.id)
        Assertions.assertEquals(studyGroup.id, invite.studyGroupId)
        Assertions.assertEquals(student.id, invite.studentId)
        Assertions.assertEquals(InviteStatus.PENDING, invite.status)

        val row = dbQuery { InviteTable.select { InviteTable.id eq invite.id }.single() }
        Assertions.assertEquals(invite.id, row[InviteTable.id])
        Assertions.assertEquals(invite.studyGroupId, row[InviteTable.studyGroupId])
        Assertions.assertEquals(invite.studentId, row[InviteTable.studentId])
        Assertions.assertEquals(invite.status, row[InviteTable.status])
    }

    @Test
    fun testInviteDuplicate(): Unit = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("student", "qwerty"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "group"))

        studyGroupService.invite(studyGroup.id, student.id)
        Assertions.assertThrows(AlreadyPendingInvite::class.java) {
            runBlocking {
                studyGroupService.invite(studyGroup.id, student.id)
            }
        }
    }

    @Test
    fun testInviteNotFoundStudent(): Unit = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("tutor", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "group"))

        Assertions.assertThrows(NotFoundStudent::class.java) {
            runBlocking {
                studyGroupService.invite(studyGroup.id, -1)
            }
        }
    }

    @Test
    fun testInviteNotFoundStudyGroup(): Unit = runBlocking {
        val student = studentService.createNew(CreateStudentModel("student", "qwerty"))

        Assertions.assertThrows(NotFoundStudyGroup::class.java) {
            runBlocking {
                studyGroupService.invite(-1, student.id)
            }
        }
    }

    @Test
    fun testInviteAlreadyInStudyGroup(): Unit = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("student", "qwerty"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "group"))
        val invite = studyGroupService.invite(studyGroup.id, student.id)
        studentService.acceptInvite(invite.id)
        Assertions.assertThrows(StudentAlreadyInStudyGroup::class.java) {
            runBlocking {
                studyGroupService.invite(studyGroup.id, student.id)
            }
        }
    }

    @Test
    fun testExcludeInvite() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("student", "qwerty"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "group"))
        val invite = studyGroupService.invite(studyGroup.id, student.id)
        studentService.acceptInvite(invite.id)
        studyGroupService.exclude(studyGroup.id, student.id)
        val updatedStudyGroup = studyGroupRepository.findById(studyGroup.id)!!
        Assertions.assertEquals(emptyList<Long>(), updatedStudyGroup.participantIds)
    }

    @Test
    fun testExcludeNotFoundStudent(): Unit = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("tutor", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "group"))

        Assertions.assertThrows(NotFoundStudent::class.java) {
            runBlocking {
                studyGroupService.exclude(studyGroup.id, -1)
            }
        }
    }

    @Test
    fun testExcludeNotFoundStudyGroup(): Unit = runBlocking {
        val student = studentService.createNew(CreateStudentModel("student", "qwerty"))

        Assertions.assertThrows(NotFoundStudyGroup::class.java) {
            runBlocking {
                studyGroupService.exclude(-1, student.id)
            }
        }
    }

    @Test
    fun testExcludeStudentAbsentInStudyGroup(): Unit = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("tutor", "12345"))
        val student = studentService.createNew(CreateStudentModel("student", "qwerty"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "group"))

        Assertions.assertThrows(StudentAbsentInStudyGroup::class.java) {
            runBlocking {
                studyGroupService.exclude(studyGroup.id, student.id)
            }
        }
    }
}