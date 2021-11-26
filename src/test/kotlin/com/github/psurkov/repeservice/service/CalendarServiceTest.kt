package com.github.psurkov.repeservice.service

import com.github.psurkov.repeservice.model.calendar.RepeatType
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventTimeModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventModel
import com.github.psurkov.repeservice.model.studygroup.CreateStudyGroupModel
import com.github.psurkov.repeservice.model.user.CreateTutorModel
import com.github.psurkov.repeservice.repository.impl.CalendarGroupEventRepositoryImpl
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
import kotlin.time.Duration.Companion.seconds

@SpringBootTest
class CalendarServiceTest {

    @Autowired
    private lateinit var tutorService: TutorService

    @Autowired
    private lateinit var studyGroupService: StudyGroupService

    @Autowired
    private lateinit var calendarService: CalendarService

    @BeforeEach
    fun prepareDatabase() {
        initDatabase()
    }

    @Test
    fun testCreateGroupEvent() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val groupEvent = calendarService.createGroupEvent(
            CreateGroupEventModel(
                studyGroup.id,
                "test name",
                "test description",
                "test link"
            )
        )
        Assertions.assertEquals(studyGroup.id, groupEvent.groupId)
        Assertions.assertEquals("test name", groupEvent.name)
        Assertions.assertEquals("test description", groupEvent.description)
        Assertions.assertEquals("test link", groupEvent.link)

        with(CalendarGroupEventRepositoryImpl.GroupEventTable) {
            val row = dbQuery {
                select { id eq groupEvent.eventId }.single()
            }
            Assertions.assertEquals(groupEvent.groupId, row[groupId])
            Assertions.assertEquals(groupEvent.name, row[name])
            Assertions.assertEquals(groupEvent.description, row[description])
            Assertions.assertEquals(groupEvent.link, row[link])
        }
    }

    @Test
    fun testUpdateGroupEvent() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val groupEvent = calendarService.createGroupEvent(
            CreateGroupEventModel(
                studyGroup.id,
                "test name",
                "test description",
                "test link"
            )
        )
        val updateGroupEvent = GroupEventModel(
            groupEvent.eventId,
            studyGroup.id,
            "new name",
            "new description",
            "new link"
        )
        calendarService.updateGroupModel(updateGroupEvent)

        with(CalendarGroupEventRepositoryImpl.GroupEventTable) {
            val row = dbQuery {
                select { id eq groupEvent.eventId }.single()
            }
            Assertions.assertEquals(updateGroupEvent.groupId, row[groupId])
            Assertions.assertEquals(updateGroupEvent.name, row[name])
            Assertions.assertEquals(updateGroupEvent.description, row[description])
            Assertions.assertEquals(updateGroupEvent.link, row[link])
        }
    }

    @Test
    fun testDeleteGroupEvent() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val groupEvent = calendarService.createGroupEvent(
            CreateGroupEventModel(
                studyGroup.id,
                "test name",
                "test description",
                "test link"
            )
        )
        calendarService.deleteGroupEvent(groupEvent.eventId)

        with(CalendarGroupEventRepositoryImpl.GroupEventTable) {
            Assertions.assertTrue(dbQuery { selectAll().toList() }.isEmpty())
        }
    }

    @Test
    fun testAddEventTime() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val groupEvent = calendarService.createGroupEvent(
            CreateGroupEventModel(
                studyGroup.id,
                "test name",
                "test description",
                "test link"
            )
        )
        val testStartTime = LocalDateTime(2000, 1, 20, 3, 30, 10, 0)
        val eventTime = calendarService.addEventTime(
            CreateGroupEventTimeModel(
                groupEvent.eventId,
                testStartTime,
                10.seconds,
                RepeatType.EVERY_DAY
            )
        )
        Assertions.assertEquals(groupEvent.eventId, eventTime.eventId)
        Assertions.assertEquals(testStartTime, eventTime.startTime)
        Assertions.assertEquals(10.seconds, eventTime.duration)
        Assertions.assertEquals(RepeatType.EVERY_DAY, eventTime.repeatType)

        with(CalendarGroupEventRepositoryImpl.GroupEventTimeTable) {
            val row = dbQuery {
                select { id eq eventTime.eventTimeId }.single()
            }
            Assertions.assertEquals(eventTime.eventId, row[eventId])
            Assertions.assertEquals(eventTime.startTime, row[startTime])
            Assertions.assertEquals(eventTime.duration, row[duration])
            Assertions.assertEquals(eventTime.repeatType, row[repeatType])
        }
    }

    @Test
    fun testDeleteGroupEventTime() = runBlocking {
        val tutor = tutorService.createNew(CreateTutorModel("test username", "12345"))
        val studyGroup = studyGroupService.createNewStudyGroup(CreateStudyGroupModel(tutor.id, "test name"))
        val groupEvent = calendarService.createGroupEvent(
            CreateGroupEventModel(
                studyGroup.id,
                "test name",
                "test description",
                "test link"
            )
        )
        val testStartTime = LocalDateTime(2000, 1, 20, 3, 30, 10, 0)
        val eventTime = calendarService.addEventTime(
            CreateGroupEventTimeModel(
                groupEvent.eventId,
                testStartTime,
                10.seconds,
                RepeatType.EVERY_DAY
            )
        )
        calendarService.deleteGroupEventTime(eventTime.eventTimeId)

        with(CalendarGroupEventRepositoryImpl.GroupEventTimeTable) {
            Assertions.assertTrue(dbQuery { selectAll().toList() }.isEmpty())
        }
    }
}