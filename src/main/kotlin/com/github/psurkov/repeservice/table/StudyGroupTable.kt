package com.github.psurkov.repeservice.table

object StudyGroupTable : AppTable() {
    val id = long("id").autoIncrement()
    val name = text("username")
    override val primaryKey = PrimaryKey(id)
}