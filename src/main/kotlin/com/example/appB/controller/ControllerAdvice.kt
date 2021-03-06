package com.example.appB.controller

import com.example.appB.exceptions.BadRequestException
import com.example.appB.exceptions.BusinessException
import com.example.appB.exceptions.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@RestControllerAdvice
class ControllerAdvice: ResponseEntityExceptionHandler() {

    @ExceptionHandler(NotFoundException::class)
    fun handle(exception: NotFoundException, webRequest: WebRequest): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage(HttpStatus.NOT_FOUND.value(), exception.message!!, webRequest.getDescription(false), LocalDateTime.now())
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handle(exception: BadRequestException, webRequest: WebRequest): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage(HttpStatus.BAD_REQUEST.value(), exception.message!!, webRequest.getDescription(false), LocalDateTime.now())
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BusinessException::class)
    fun handle(exception: BusinessException, webRequest: WebRequest): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage(exception.code, exception.message!!, webRequest.getDescription(false), LocalDateTime.now())
        return ResponseEntity(error, HttpStatus.valueOf(exception.code))
    }

    @ExceptionHandler(Exception::class)
    fun handle(exception: Exception, webRequest: WebRequest): ResponseEntity<ErrorMessage> {
        val error = ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.message!!, webRequest.getDescription(false), LocalDateTime.now())
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    data class ErrorMessage(val code: Int, val message: String, val endpoint: String, val timestamp: LocalDateTime)
}