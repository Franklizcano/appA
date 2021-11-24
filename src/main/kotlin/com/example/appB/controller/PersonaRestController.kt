package com.example.appB.controller

import com.example.appB.exceptions.shared.handleResponse
import com.example.appB.model.Persona
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient

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

    @GetMapping("/notfound")
    fun notFound(): ResponseEntity<Persona?> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/notfound")

        val response = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.OK).body(response.block())
    }

    @GetMapping("/badrequest")
    fun badrequest(): ResponseEntity<Persona> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/badrequest")

        val response = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.OK).body(response.block())
    }

    @GetMapping("/exception")
    fun exception(): ResponseEntity<Persona> {
        val client = webClient
            .get()
            .uri("/api/v1/personas/exception")

        val response = client.exchangeToMono { response -> response.handleResponse() }
        return ResponseEntity.status(HttpStatus.OK).body(response.block())
    }
}