package com.example.appB.controller

import com.example.appB.exceptions.BadRequestException
import com.example.appB.exceptions.NotFoundException
import com.example.appB.model.dto.ErrorResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/personas")
class PersonaRestController {

    @Autowired
    lateinit var webClient: WebClient

    /*
    @GetMapping("/get")
    fun get(): List<Persona>? {
        val persona = webClient
            .get()
            .uri("/api/v1/personas/findAll")
            .retrieve()
            .bodyToFlux<Persona>()
        return persona.collectSortedList().block()
    }*/

    @GetMapping("/get")
    fun get(): Mono<ErrorResponse> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/findAll")

            return client.exchangeToMono { response -> response.handleResponse() }
    }

    fun ClientResponse.handleResponse(): Mono<ErrorResponse> {
        return when {
            this.statusCode().is4xxClientError -> handle4xx(this)
            else                               -> handle4xx(this)
        }
    }

    fun handle4xx(response: ClientResponse): Mono<ErrorResponse> =
        response
            .bodyToMono(ErrorResponse::class.java)
            .doOnNext { val message = "Error when invoking external service: ${it.message}"
                println( message )
                throw NotFoundException(message) }

    @GetMapping("/notfound")
    fun notFound(): ResponseEntity<Mono<ErrorResponse>> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/notfound")

        val response: Mono<ErrorResponse> = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    @GetMapping("/exception")
    fun exception(): Mono<ErrorResponse> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/exception")

        return client.exchangeToMono { response -> response.handleResponse() }
    }
}