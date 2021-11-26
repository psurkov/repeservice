package com.github.psurkov.repeservice.table

object StudyGroupTable : AppTable() {
    val id = long("id").autoIncrement()
    val name = text("name")
    val ownerTutorId = long("ownerTutorId").references(TutorTable.id)
    override val primaryKey = PrimaryKey(id)
}