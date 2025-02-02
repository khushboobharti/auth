package com.yogaveda.auth.routing

import com.yogaveda.auth.routing.request.LoginRequest
import com.yogaveda.auth.service.JWTService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.response.respond

fun Route.authRoute (
    jwtService: JWTService
) {
    post {
        val loginRequest = call.receive<LoginRequest>()

        val token = jwtService.createJWTToken(loginRequest)

        token?.let {
            call.respond(hashMapOf("token" to it))
        } ?: call.respond(HttpStatusCode.Unauthorized)
    }
}