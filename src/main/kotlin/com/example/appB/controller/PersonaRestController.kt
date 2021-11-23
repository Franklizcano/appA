package com.example.appB.controller

import com.example.appB.exceptions.ClientServerException
import com.example.appB.exceptions.GenericException
import com.example.appB.exceptions.NotFoundException
import com.example.appB.model.Persona
import com.example.appB.model.dto.ErrorResponse
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/personas")
class PersonaRestController(@Autowired private val webClient: WebClient) {

    @GetMapping("/get")
    fun get(): ResponseEntity<List<Persona>> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/findAll")

        val response = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.OK).body(response.flux().collectSortedList().block())
    }

    fun ClientResponse.handleResponse(): Mono<Persona> {
        return when {
            this.statusCode().is2xxSuccessful -> handle200(this)
            this.statusCode().is4xxClientError -> handle4xx(this)
            else                               -> handle5xx(this)
        }
    }

    fun handle200(response: ClientResponse): Mono<Persona> {
        return response.bodyToMono(Persona::class.java)
            .doOnNext { println("API Response: ${jacksonObjectMapper().writeValueAsString(it)}") }
    }

    fun handle4xx(response: ClientResponse): Mono<Persona> {
        response
            .bodyToMono(ErrorResponse::class.java)
            .doOnNext { val message = "Error when invoking external service: ${it.message}"
                println( message ) }

        throw GenericException(response.rawStatusCode(), "Este es un mensaje de error")
        }

    fun handle5xx(response: ClientResponse): Mono<Persona> {
        response
            .bodyToMono(ErrorResponse::class.java)
            .doOnNext {
                val message = "Error when invoking external service: ${it.message}"
                println(message) }

        throw ClientServerException("Esto es un mensaje de error")
    }

    @GetMapping("/notfound")
    fun notFound(): ResponseEntity<Persona?> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/notfound")

        val response = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.block())
    }

    @GetMapping("/badrequest")
    fun badrequest(): ResponseEntity<Persona> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/badrequest")

        val response = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.block())
    }

    @GetMapping("/exception")
    fun exception(): ResponseEntity<Persona> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/exception")

        val response = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.block())
    }
}