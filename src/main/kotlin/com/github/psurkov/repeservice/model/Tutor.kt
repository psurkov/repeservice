package com.github.psurkov.repeservice.model

data class Tutor(
    override val id: Long,
    override val username: String,
    override val password: String,
) : User