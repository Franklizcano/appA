package com.example.appB.exceptions

data class BusinessException(val code: Int, override val message: String?): RuntimeException(message)