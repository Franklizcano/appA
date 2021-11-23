package com.example.appB.exceptions

data class GenericException(val code: Int ,override val message: String?): RuntimeException(message)