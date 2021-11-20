package com.github.psurkov.repeservice.table

object StudyGroupTutorParticipantTable : AppTable() {
    val studyGroupId = long("studyGroupId").references(StudyGroupTable.id)
    val tutorId = long("tutorId").references(TutorTable.id)
}