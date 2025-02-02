package com.yogaveda.auth.routing

import com.yogaveda.auth.service.JWTService
import com.yogaveda.auth.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting(
    userService: UserService,
    jwtService: JWTService
) {
    routing {
        route("api/auth") {
            authRoute(jwtService)
        }
        route("api/user") {
            userRoute(userService)
        }
    }
}