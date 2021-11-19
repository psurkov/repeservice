package com.github.psurkov.repeservice.table

import org.jetbrains.exposed.sql.Table

object Tutors : Table() {
    val id = long("id").autoIncrement()

    override val primaryKey = PrimaryKey(id)
}