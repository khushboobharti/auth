package com.yogaveda.auth.service

import com.auth0.jwt.interfaces.DecodedJWT
import com.yogaveda.auth.repository.UserRepository
import com.yogaveda.auth.model.User
import com.yogaveda.auth.repository.RefreshTokenRepository
import com.yogaveda.auth.routing.request.LoginRequest
import com.yogaveda.auth.routing.response.AuthResponse
import java.util.UUID

class UserService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtService: JWTService
) {

    fun findAll(): List<User> = userRepository.findAll()

    fun findById(id: String): User? = userRepository.findById(UUID.fromString(id))

    fun findByUsername(username: String): User? = userRepository.findByUsername(username)

    fun save(user: User): User? {
        return if (findByUsername(user.username) == null) {
            userRepository.save(user)
            user
        } else null
    }

    fun authenticate(loginRequest: LoginRequest): AuthResponse? {
        // if user name exists
        val foundUser = findByUsername(loginRequest.username)

        return if (foundUser != null && foundUser.password == loginRequest.password) {
            val accessToken = jwtService.createAccessToken(foundUser.username)

            val refreshToken = jwtService.createRefreshToken(foundUser.username)
            refreshTokenRepository.save(foundUser.username, refreshToken)
            return AuthResponse(accessToken, refreshToken)
        } else null
    }

    fun refreshToken(token: String): String? {
        val decodedRefreshToken = verifyRefreshToken(token)
        val persistedUserName = refreshTokenRepository.findUsernameByToken(token)

        return if (decodedRefreshToken != null && persistedUserName != null) {

            val foundUser = userRepository.findByUsername(persistedUserName)
            val userNameFromRefreshToken = decodedRefreshToken.getClaim("username").asString()
            if (foundUser != null && foundUser.username == userNameFromRefreshToken) {
                jwtService.createAccessToken(foundUser.username)
            } else null

        } else null
    }

    private fun verifyRefreshToken(token: String): DecodedJWT? {
        val decodedJWTToken = jwtService.getDecodedJWTToken(token)

        return decodedJWTToken?.let {
            val audienceMatches = jwtService.audienceMatches(it.audience)

            if(audienceMatches) decodedJWTToken else null
        }
    }
}