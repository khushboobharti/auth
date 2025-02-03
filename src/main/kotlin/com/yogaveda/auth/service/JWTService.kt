package com.yogaveda.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.yogaveda.auth.repository.UserRepository
import io.ktor.server.application.Application
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import java.time.Instant

class JWTService(
    private val application: Application,
    private val userRepository: UserRepository
) {

    private val secret = getConfigProperty("jwt.secret")
    private val issuer = getConfigProperty("jwt.issuer")
    private val audience = getConfigPropertyList ("jwt.audience").toTypedArray()
    val realm = getConfigProperty("jwt.realm")
    /*private val expirationTime = getConfigProperty("jwt.expirationTime").toLong()
    private val refreshTokenExpirationTime = getConfigProperty("jwt.refreshTokenExpirationTime").toLong()
    private val algorithm = getConfigProperty("jwt.algorithm")
    private val accessTokenExpirationTime = getConfigProperty("jwt.accessTokenExpirationTime").toLong()*/

    val jwtVerifier = JWT
        .require(Algorithm.HMAC256(secret))
        .withAudience(*audience)
        .withIssuer(issuer)
        .build()

    fun getDecodedJWTToken(token: String): DecodedJWT? {
        return try {
            jwtVerifier.verify(token)
        } catch (e: Exception) {
            throw Exception("Invalid token")
        }
    }

    fun createAccessToken(username: String): String =
        createJWTToken(username, 3_600_000)

    fun createRefreshToken(username: String): String =
        createJWTToken(username, 86_400_000)

    private fun createJWTToken(username: String, expireIn: Long): String =
        JWT.create()
            .withAudience(*audience)
            .withIssuer(issuer)
            .withClaim("username", username)
            .withExpiresAt(Instant.now().plusMillis(expireIn))
            .sign(Algorithm.HMAC256(secret))

    fun customValidator(credentials: JWTCredential): JWTPrincipal? {

        val username = extractUsername(credentials)//credentials.payload.getClaim("username").asString()

        val foundUser = username?.let(userRepository::findByUsername)

        return foundUser?.let {
            if(audienceMatches(credentials) && issuerMatches(credentials) && expirationTimeNotPassed(credentials)) {
                JWTPrincipal(credentials.payload)
            } else null
        }
    }

    /**
     * Check if the audience matches the expected audience,
     * even 1 audience from the JWT should match expected audience/s on this server
     * @param audience the expected audience
     */
    fun audienceMatches(audience: List<String>): Boolean =
        audience.containsAll(this.audience.asList())

    private fun expirationTimeNotPassed(credentials: JWTCredential): Boolean =
        credentials.expiresAt?.toInstant()?.isAfter(Instant.now()) ?: false

    private fun issuerMatches(credentials: JWTCredential): Boolean =
        credentials.payload.issuer == issuer

    private fun audienceMatches(credentials: JWTCredential): Boolean  =
        credentials.payload.audience.find {
            this.audience.contains(it)
        }?.let { true } ?: false

    private fun extractUsername(credentials: JWTCredential): String? =
        credentials.payload.getClaim("username").asString()

    private fun getConfigProperty(path: String) =
        application.environment.config.property(path).getString()

    private fun getConfigPropertyList(path: String) =
        application.environment.config.property(path).getList()
}