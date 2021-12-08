package com.github.psurkov.repeservice.repository.impl

import com.github.psurkov.repeservice.model.calendar.RepeatType
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.CreateGroupEventTimeModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventModel
import com.github.psurkov.repeservice.model.calendar.groupevent.GroupEventTimeModel
import com.github.psurkov.repeservice.repository.CalendarGroupEventRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.kotlin.datetime.duration
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
class CalendarGroupEventRepositoryImpl : CalendarGroupEventRepository {

    private fun ResultRow.groupEventFromRow() =
        GroupEventModel(
            this[GroupEventTable.id],
            this[GroupEventTable.groupId],
            this[GroupEventTable.name],
            this[GroupEventTable.description],
            this[GroupEventTable.link],
        )

    private fun ResultRow.groupEventTimeFromRow() =
        GroupEventTimeModel(
            this[GroupEventTimeTable.id],
            this[GroupEventTimeTable.eventId],
            this[GroupEventTimeTable.startTime],
            this[GroupEventTimeTable.duration],
            this[GroupEventTimeTable.repeatType],
        )

    override suspend fun findGroupEventById(groupEventId: Long): GroupEventModel? = dbQuery {
        GroupEventTable.select {
            GroupEventTable.id eq groupEventId
        }.singleOrNull()?.groupEventFromRow()
    }

    override suspend fun insertGroupEvent(createGroupEventModel: CreateGroupEventModel): GroupEventModel = dbQuery {
        with(GroupEventTable) {
            insert {
                it[groupId] = createGroupEventModel.groupId
                it[name] = createGroupEventModel.name
                it[description] = createGroupEventModel.description
                it[link] = createGroupEventModel.link
            }.resultedValues!!.first().groupEventFromRow()
        }
    }

    override suspend fun updateGroupEvent(groupEventModel: GroupEventModel): Unit = dbQuery {
        with(GroupEventTable) {
            update({ id eq groupEventModel.groupId }) {
                it[groupId] = groupEventModel.groupId
                it[name] = groupEventModel.name
                it[description] = groupEventModel.description
                it[link] = groupEventModel.link
            }
        }
    }

    override suspend fun deleteGroupEvent(groupEventId: Long): Unit = dbQuery {
        with(GroupEventTable) {
            deleteWhere {
                id eq groupEventId
            }
        }
    }

    override suspend fun findGroupEventTimeById(groupEventTimeId: Long): GroupEventTimeModel? = dbQuery {
        GroupEventTimeTable.select {
            GroupEventTimeTable.id eq groupEventTimeId
        }.singleOrNull()?.groupEventTimeFromRow()
    }

    override suspend fun findEventTimesOfEvent(eventId: Long): List<GroupEventTimeModel> = dbQuery {
        GroupEventTimeTable.select { GroupEventTimeTable.eventId eq eventId }
            .map { it.groupEventTimeFromRow() }
            .toList()
    }

    override suspend fun insertGroupEventTime(createGroupEventTimeModel: CreateGroupEventTimeModel) = dbQuery {
        with(GroupEventTimeTable) {
            insert {
                it[eventId] = createGroupEventTimeModel.eventId
                it[startTime] = createGroupEventTimeModel.startTime
                it[duration] = createGroupEventTimeModel.duration
                it[repeatType] = createGroupEventTimeModel.repeatType
            }.resultedValues!!.first().groupEventTimeFromRow()
        }
    }

    override suspend fun deleteGroupEventTime(groupEventTimeId: Long): Unit = dbQuery {
        with(GroupEventTimeTable) {
            deleteWhere {
                id eq groupEventTimeId
            }
        }
    }

    object GroupEventTable : AppTable() {
        val id = long("id").autoIncrement()
        val groupId = long("groupId").references(
            StudyGroupRepositoryImpl.StudyGroupTable.id,
            onDelete = ReferenceOption.CASCADE
        )
        val name = text("name")
        val description = text("description")
        val link = text("link")

        override val primaryKey = PrimaryKey(id)
    }

    object GroupEventTimeTable : AppTable() {
        val id = long("id").autoIncrement()
        val eventId = long("eventId").references(
            GroupEventTable.id,
            onDelete = ReferenceOption.CASCADE
        )
        val startTime = datetime("start")
        val duration = duration("duration")
        val repeatType = enumeration("repeatType", RepeatType::class)

        override val primaryKey = PrimaryKey(GroupEventTable.id)
    }
}