package com.github.psurkov.repeservice.model.user

data class TutorModel(
    override val id: Long,
    override val username: String,
    override val password: String,
) : UserModel