package com.github.psurkov.repeservice.model

data class StudentModel(
    override val id: Long,
    override val username: String,
    override val password: String,
) : UserModel