package com.example.appB.controller

import com.example.appB.exceptions.GenericException
import com.example.appB.exceptions.NotFoundException
import com.example.appB.model.Persona
import com.example.appB.model.dto.ErrorResponse
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
class PersonaRestController {

    @Autowired
    lateinit var webClient: WebClient

    @GetMapping("/get")
    fun get(): ResponseEntity<List<Persona>> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/findAll")

        val response: MutableList<Persona>? = client.retrieve().bodyToFlux<Persona>().collectList().block()
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    fun ClientResponse.handleResponse(): Mono<ErrorResponse> {
        return when {
            this.statusCode().is4xxClientError -> handle4xx(this)
            this.statusCode().is5xxServerError -> handle5xx(this)
            else                               -> handle5xx(this)
        }
    }

    fun handle4xx(response: ClientResponse): Mono<ErrorResponse> =
        response
            .bodyToMono(ErrorResponse::class.java)
            .doOnNext { val message = "Error when invoking external service: ${it.message}"
                println( message )
                throw NotFoundException(message) }

    fun handle5xx(response: ClientResponse): Mono<ErrorResponse> =
        response
            .bodyToMono(ErrorResponse::class.java)
            .doOnNext { val message = "Error when invoking external service: ${it.message}"
                println( message )
                throw GenericException(message) }

    @GetMapping("/notfound")
    fun notFound(): ResponseEntity<Mono<ErrorResponse>> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/notfound")

        val response: Mono<ErrorResponse> = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @GetMapping("/badrequest")
    fun badrequest(): ResponseEntity<Mono<ErrorResponse>> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/badrequest")

        val response: Mono<ErrorResponse> = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @GetMapping("/exception")
    fun exception(): ResponseEntity<Mono<ErrorResponse>> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/exception")

        val response: Mono<ErrorResponse> = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}