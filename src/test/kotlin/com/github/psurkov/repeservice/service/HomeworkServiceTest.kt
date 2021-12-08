package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.exception.SolutionAfterDeadline
import com.github.psurkov.repeservice.model.homework.CreateHomeworkModel
import com.github.psurkov.repeservice.model.homework.HomeworkSolutionModel
import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.user.CreateStudentModel
import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.repository.FileStorageRepository
import com.github.psurkov.repeservice.repository.impl.HomeworkRepositoryImpl
import com.github.psurkov.repeservice.repository.impl.HomeworkSolutionRepositoryImpl
import com.github.psurkov.repeservice.repository.impl.dbQuery
import com.github.psurkov.repeservice.repository.impl.initDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.FieldSet
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile

@SpringBootTest
class HomeworkServiceTest {

    @Autowired
    private lateinit var studentService: StudentService

    @Autowired
    private lateinit var tutorService: TutorService

    @Autowired
    private lateinit var studyGroupService: StudyGroupService

    @Autowired
    private lateinit var storageRepository: FileStorageRepository

    @Autowired
    private lateinit var homeworkService: HomeworkService


    @BeforeEach
    fun prepareDatabase() {
        storageRepository.init()
        initDatabase()
    }

    @Test
    fun testCreateHomework() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val deadlineTime = LocalDateTime(10000, 1, 20, 3, 30, 10, 0)
        val taskFile = MockMultipartFile("file", "text.txt", "text/plain", "test text".toByteArray())
        val homeworkModel = homeworkService.createHomework(
            CreateHomeworkModel(
                "test name",
                studyGroup.id,
                deadlineTime
            ),
            taskFile
        )
        Assertions.assertEquals("test name", homeworkModel.name)
        Assertions.assertEquals(studyGroup.id, homeworkModel.studyGroupId)
        Assertions.assertEquals(deadlineTime, homeworkModel.deadline)

        with(HomeworkRepositoryImpl.HomeworkTable) {
            val row = dbQuery {
                select { id eq homeworkModel.id }.single()
            }
            Assertions.assertEquals(homeworkModel.id, row[id])
            Assertions.assertEquals(homeworkModel.name, row[name])
            Assertions.assertEquals(homeworkModel.studyGroupId, row[studyGroupId])
            Assertions.assertEquals(homeworkModel.taskFileId, row[taskFileId])
            Assertions.assertEquals(homeworkModel.deadline, row[deadline])
        }
    }

    @Test
    fun testSubmitSolution() = runBlocking {
        val student = studentService.createNew(CreateStudentModel("test student", "qwerty"))
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val deadlineTime = LocalDateTime(10000, 1, 20, 3, 30, 10, 0)
        val taskFile = MockMultipartFile("file.txt", "file.txt", "text/plain", "test text".toByteArray())
        val homeworkModel = homeworkService.createHomework(
            CreateHomeworkModel(
                "test name",
                studyGroup.id,
                deadlineTime
            ),
            taskFile
        )
        val solutionFile = MockMultipartFile("file.txt", "file.txt", "text/plain", "solution".toByteArray())
        homeworkService.submitSolution(student.id, homeworkModel.id, solutionFile)

        with(HomeworkSolutionRepositoryImpl.HomeworkSolutionTable) {
            val row = dbQuery {
                selectAll().single()
            }
            Assertions.assertEquals(student.id, row[studentId])
            Assertions.assertEquals(homeworkModel.id, row[homeworkId])
        }
    }

    @Test
    fun testSubmitSolutionAfterDeadline() = runBlocking {
        val student = studentService.createNew(CreateStudentModel("test student", "qwerty"))
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val deadlineTime = LocalDateTime(1000, 1, 20, 3, 30, 10, 0)
        val taskFile = MockMultipartFile("file.txt", "file.txt", "text/plain", "test text".toByteArray())
        val homeworkModel = homeworkService.createHomework(
            CreateHomeworkModel(
                "test name",
                studyGroup.id,
                deadlineTime
            ),
            taskFile
        )
        val solutionFile = MockMultipartFile("file.txt", "file.txt", "text/plain", "solution".toByteArray())
        Assertions.assertThrows(SolutionAfterDeadline::class.java) {
            runBlocking {
                homeworkService.submitSolution(student.id, homeworkModel.id, solutionFile)
            }
        }

        with(HomeworkSolutionRepositoryImpl.HomeworkSolutionTable) {
            val row = dbQuery {
                selectAll().toList()
            }
            Assertions.assertEquals(emptyList<FieldSet>(), row)
        }
    }

    @Test
    fun testListSolutions() = runBlocking {
        val student = studentService.createNew(CreateStudentModel("test student", "qwerty"))
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val deadlineTime = LocalDateTime(10000, 1, 20, 3, 30, 10, 0)
        val taskFile = MockMultipartFile("file.txt", "file.txt", "text/plain", "test text".toByteArray())
        val homeworkModel = homeworkService.createHomework(
            CreateHomeworkModel(
                "test name",
                studyGroup.id,
                deadlineTime
            ),
            taskFile
        )
        val solutionFile = MockMultipartFile("file.txt", "file.txt", "text/plain", "solution".toByteArray())
        homeworkService.submitSolution(student.id, homeworkModel.id, solutionFile)
        val actualSolution = homeworkService.listSolutions(homeworkModel.id).single()
        Assertions.assertEquals(student.id, actualSolution.studentId)
        Assertions.assertEquals(homeworkModel.id, actualSolution.homeworkId)
        Assertions.assertEquals(emptyList<HomeworkSolutionModel>(), homeworkService.listSolutions(-1))
    }

    @Test
    fun testGetTaskResource() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val deadlineTime = LocalDateTime(10000, 1, 20, 3, 30, 10, 0)
        val taskFile = MockMultipartFile("file.txt", "file.txt", "text/plain", "test text".toByteArray())
        val homeworkModel = homeworkService.createHomework(
            CreateHomeworkModel(
                "test name",
                studyGroup.id,
                deadlineTime
            ),
            taskFile
        )
        val taskResource = homeworkService.getTaskResource(homeworkModel.taskFileId).file
        Assertions.assertEquals("file.txt", taskResource.name)
        Assertions.assertArrayEquals("test text".toByteArray(), taskResource.readBytes())
    }


    @Test
    fun testGetSolutionResource() = runBlocking {
        val student = studentService.createNew(CreateStudentModel("test student", "qwerty"))
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val deadlineTime = LocalDateTime(10000, 1, 20, 3, 30, 10, 0)
        val taskFile = MockMultipartFile("file.txt", "file.txt", "text/plain", "test text".toByteArray())
        val homeworkModel = homeworkService.createHomework(
            CreateHomeworkModel(
                "test name",
                studyGroup.id,
                deadlineTime
            ),
            taskFile
        )
        val solutionFile = MockMultipartFile("file.txt", "file.txt", "text/plain", "solution".toByteArray())
        homeworkService.submitSolution(student.id, homeworkModel.id, solutionFile)
        val actualSolution = homeworkService.listSolutions(homeworkModel.id).single()
        val taskResource = homeworkService.getSolutionResource(actualSolution.solutionFileId).file
        Assertions.assertEquals("file.txt", taskResource.name)
        Assertions.assertArrayEquals("solution".toByteArray(), taskResource.readBytes())
    }
}