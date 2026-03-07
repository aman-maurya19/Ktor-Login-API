package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.mindrot.jbcrypt.BCrypt

fun Application.configureRouting() {
    routing {

        get("/") {
            call.respondText("Backend is running successfully 🚀")
        }

        // --- SIGNUP API ---
        post("/signup") {
            val request = call.receive<SignupRequest>()

            try {
                DatabaseFactory.dbQuery {
                    UserTable.insert {
                        it[UserTable.email] = request.email
                        it[UserTable.name] = request.name
                        it[UserTable.mobile] = request.mobile
                        it[UserTable.password] = BCrypt.hashpw(request.password, BCrypt.gensalt())
                    }
                }
                call.respond(mapOf("status" to "success", "message" to "User registered!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, mapOf("status" to "error", "message" to "Email already exists"))
            }
        }

        // --- LOGIN API ---
        post("/login") {
            val request = call.receive<LoginRequest>()

            try {
                val user = DatabaseFactory.dbQuery {
                    UserTable.select { UserTable.email eq request.email }
                        .map {
                            mapOf(
                                "password" to it[UserTable.password],
                                "name" to it[UserTable.name]
                            )
                        }.singleOrNull()
                }

                if (user != null && BCrypt.checkpw(request.password, user["password"] as String)) {
                    call.respond(mapOf(
                        "status" to "success",
                        "message" to "Login successful",
                        "name" to user["name"]
                    ))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid email or password"))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to "Something went wrong"))
            }
        }
    }
}