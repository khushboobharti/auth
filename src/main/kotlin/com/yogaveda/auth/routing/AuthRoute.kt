package com.yogaveda.auth.routing

import com.yogaveda.auth.routing.request.LoginRequest
import com.yogaveda.auth.routing.request.RefreshTokenRequest
import com.yogaveda.auth.routing.response.AuthResponse
import com.yogaveda.auth.routing.response.RefreshTokenResponse
import com.yogaveda.auth.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.response.respond

fun Route.authRoute (
    userService: UserService
) {
    post {
        val loginRequest = call.receive<LoginRequest>()

        val authResponse: AuthResponse? = userService.authenticate(loginRequest)

        authResponse?.let {
            call.respond(it)
        } ?: call.respond(HttpStatusCode.Unauthorized)
    }

    post("/refresh") {
        //val refreshToken = call.request.headers["Refresh-Token"] ?: ""
        val request = call.receive<RefreshTokenRequest>()

        val newAccessToken: String? = userService.refreshToken(request.token)

        //val authResponse: AuthResponse? = userService.refreshToken(refreshToken)

        newAccessToken?.let {
            call.respond(RefreshTokenResponse(it))
        } ?: call.respond(HttpStatusCode.Unauthorized)
    }
}