package com.example.appB.exceptions

data class ClientServerException(val code: Int, override val message: String?): RuntimeException(message)