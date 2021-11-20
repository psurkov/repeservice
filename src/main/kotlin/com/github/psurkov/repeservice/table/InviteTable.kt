package com.github.psurkov.repeservice.table

import com.github.psurkov.repeservice.model.invite.InviteStatus

object InviteTable : AppTable() {
    val id = long("id").autoIncrement()
    val studyGroupId = long("studyGroupId").references(StudyGroupTable.id)
    val studentId = long("studentId").references(StudentTable.id)
    val status = enumeration("status", InviteStatus::class)

    override val primaryKey = PrimaryKey(id)
}