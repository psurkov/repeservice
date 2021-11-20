package com.github.psurkov.repeservice.model.user

sealed interface UserModel {
    val id: Long
    val username: String
    val password: String
}

