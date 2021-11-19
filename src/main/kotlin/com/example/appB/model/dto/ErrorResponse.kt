package com.example.appB.model.dto

import java.time.LocalDateTime

data class ErrorResponse(val code: Int,
                    val message: String,
                    val endpoint: String,
                    val timestamp: LocalDateTime)