package com.example

import io.ktor.server.application.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)

}

fun Application.module(configureSerialization: () -> Unit) {
    DatabaseFactory.init() // Ye database connect karega
    configureSerialization()
fun Application.module() {

    install(ContentNegotiation) {
        json()
    }

    configureRouting()
}

