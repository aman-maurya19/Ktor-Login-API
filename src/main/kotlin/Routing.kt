package com.example

import com.example.UserTable.name
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

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
                        it[email] = request.email
                        it[name] = request.name
                        it[mobile] = request.mobile
                        // Password ko hash karke save kar rahe hain
                        it[password] = org.mindrot.jbcrypt.BCrypt.hashpw(request.password, org.mindrot.jbcrypt.BCrypt.gensalt())
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

            val user = DatabaseFactory.dbQuery {
                UserTable.select { UserTable.email eq request.email }.singleOrNull()
            }

            if (user != null && org.mindrot.jbcrypt.BCrypt.checkpw(request.password, user[UserTable.password])) {
                call.respond(mapOf(
                    "status" to "success",
                    "message" to "Login successful",
                    "data" to mapOf("name" to user[UserTable.name], "mobile" to user[UserTable.mobile])
                ))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("status" to "error", "message" to "Invalid email or password"))
            }
        }
    }
}