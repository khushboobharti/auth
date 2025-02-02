package com.yogaveda.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.yogaveda.auth.routing.request.LoginRequest
import io.ktor.server.application.Application
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import java.time.Instant

class JWTService(
    private val application: Application,
    private val userService: UserService
) {

    private val secret = getConfigProperty("jwt.secret")
    private val issuer = getConfigProperty("jwt.issuer")
    private val audience = getConfigProperty("jwt.audience")
    val realm = getConfigProperty("jwt.realm")
    /*private val expirationTime = getConfigProperty("jwt.expirationTime").toLong()
    private val refreshTokenExpirationTime = getConfigProperty("jwt.refreshTokenExpirationTime").toLong()
    private val algorithm = getConfigProperty("jwt.algorithm")
    private val accessTokenExpirationTime = getConfigProperty("jwt.accessTokenExpirationTime").toLong()*/

    val jwtVerifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun createJWTToken(loginRequest: LoginRequest): String? {
        val foundUser = userService.findByUsername(loginRequest.username)
        return if (foundUser != null && foundUser.password == loginRequest.password) {
            JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("username", foundUser.username)
                .withExpiresAt(Instant.now().plusMillis(3600000))
                .sign(Algorithm.HMAC256(secret))
        } else null
    }

    fun customValidator(credentials: JWTCredential): JWTPrincipal? {

        val username = extractUsername(credentials)//credentials.payload.getClaim("username").asString()

        val foundUser = username?.let(userService::findByUsername)

        return foundUser?.let {
            if(audienceMatches(credentials) && issuerMatches(credentials) && expirationTimeNotPassed(credentials)) {
                JWTPrincipal(credentials.payload)
            } else null
        }
    }

    private fun expirationTimeNotPassed(credentials: JWTCredential): Boolean =
        credentials.expiresAt?.toInstant()?.isAfter(Instant.now()) ?: false

    private fun issuerMatches(credentials: JWTCredential): Boolean =
        credentials.payload.issuer == issuer

    private fun audienceMatches(credentials: JWTCredential): Boolean  =
        credentials.payload.audience.contains(audience)

    private fun extractUsername(credentials: JWTCredential): String? =
        credentials.payload.getClaim("username").asString()

    private fun getConfigProperty(path: String) =
        application.environment.config.property(path).getString()
}