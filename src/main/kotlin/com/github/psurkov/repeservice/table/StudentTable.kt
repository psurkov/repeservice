package com.github.psurkov.repeservice.table

object StudentTable : AppTable() {
    val id = long("id").autoIncrement()
    val username = text("username")
    val password = text("password")

    override val primaryKey = PrimaryKey(id)
}