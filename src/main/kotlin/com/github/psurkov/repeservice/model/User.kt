package com.github.psurkov.repeservice.model

sealed interface User {
    val id: Long
    val username: String
    val password: String
}

