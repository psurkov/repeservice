package com.github.psurkov.repeservice.model

sealed interface UserModel {
    val id: Long
    val username: String
    val password: String
}

