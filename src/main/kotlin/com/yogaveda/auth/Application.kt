package com.yogaveda.auth

import com.yogaveda.auth.plugins.configureSecurity
import com.yogaveda.auth.repository.UserRepository
import com.yogaveda.auth.plugins.configureSerialization
import com.yogaveda.auth.routing.configureRouting
import com.yogaveda.auth.service.JWTService
import com.yogaveda.auth.service.UserService
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val userRepository = UserRepository()
    val userService = UserService(userRepository)
    val jwtService = JWTService(this, userService)

    configureSerialization()
    configureSecurity(jwtService)
    configureRouting(userService, jwtService)
}