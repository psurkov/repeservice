package com.github.psurkov.repeservice.table

object StudyGroupStudentParticipantTable : AppTable() {
    val studyGroupId = long("studyGroupId").references(StudyGroupTable.id)
    val studentId = long("studentId").references(StudentTable.id)
}