package com.example

import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {

        get("/") {
            call.respondText("Backend is running successfully 🚀")
        }
        post("/login") {

            val request = call.receive<LoginRequest>()

            if (request.email == "admin@gmail.com" &&
                request.password == "1234"
            ) {
                call.respond(
                    mapOf(
                        "status" to "success",
                        "message" to "Login successful"
                    )
                )
            } else {
                call.respond(
                    mapOf(
                        "status" to "error",
                        "message" to "Invalid credentials"
                    )
                )
            }
        }
    }
}

