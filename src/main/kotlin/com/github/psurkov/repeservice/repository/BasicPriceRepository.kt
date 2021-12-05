package com.github.psurkov.repeservice.repository

interface BasicPriceRepository {
    suspend fun find(studentId: Long, tutorId: Long): Int?
    suspend fun insertBasicPrice(studentId: Long, tutorId: Long, priceInRubles: Int)
    suspend fun updateBasicPrice(studentId: Long, tutorId: Long, priceInRubles: Int)
}