package com.github.psurkov.repeservice.model

data class Student(
    override val id: Long,
    override val username: String,
    override val password: String,
) : User