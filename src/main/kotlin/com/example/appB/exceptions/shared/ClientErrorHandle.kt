package com.example.appB.exceptions.shared

import com.example.appB.exceptions.ClientServerException
import com.example.appB.exceptions.BusinessException
import com.example.appB.model.Persona
import com.example.appB.model.dto.ErrorResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

fun ClientResponse.handleResponse(): Mono<Persona> {
    return when {
        this.statusCode().is2xxSuccessful -> handle200(this)
        this.statusCode().is4xxClientError -> handle4xx(this)
        else                               -> handle5xx(this)
    }
}

fun handle200(response: ClientResponse): Mono<Persona> {
    return response.bodyToMono(Persona::class.java)
        .doOnNext { log.info { "API Response: ${jacksonObjectMapper().writeValueAsString(it)}" } }
}

fun handle4xx(response: ClientResponse): Mono<Persona> {
    return response.bodyToMono(ErrorResponse::class.java)
        .doOnNext { log.error { "Error when invoking external service: ${it.message}" } }
        .map { throw BusinessException(it.code, it.message) }
}

fun handle5xx(response: ClientResponse): Mono<Persona> {
    return response.bodyToMono(ErrorResponse::class.java)
        .doOnNext { log.error { "Error when invoking external service: ${it.message}" } }
        .map { throw ClientServerException(it.code, it.message) }
}