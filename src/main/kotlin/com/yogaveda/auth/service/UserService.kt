package com.yogaveda.auth.service

import com.auth0.jwt.interfaces.DecodedJWT
import com.yogaveda.auth.repository.UserRepository
import com.yogaveda.auth.model.User
import com.yogaveda.auth.repository.TokensRepository
import com.yogaveda.auth.routing.request.VerifiedUserRequest
import com.yogaveda.auth.routing.response.AuthResponse
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import java.util.UUID

class UserService(
    private val userRepository: UserRepository,
    private val tokensRepository: TokensRepository,
    private val jwtService: JWTService,
    private val coroutineScope: CoroutineScope
) {

    suspend fun findAll(): List<User> = userRepository.findAll()

    suspend fun findById(id: String): User? = userRepository.findById(UUID.fromString(id))

    suspend fun findByEmail(email: String): User? = userRepository.findByEmail(email)

    /**
     * Register a new user and return access token and refresh token
     */
    suspend fun registerUser(user: VerifiedUserRequest):  AuthResponse? {

        // Check if access token works, make a call to Google's servers to access token info
        //coroutineScope.async {  }.await()
        //getAccessTokenInfo(user.accessToken)

        val retrievedUser = userRepository.findByEmail(user.email)

        // and get other information if required
        return retrievedUser?.let {
            // throw exception as email already exists
            // throw retrievedUser.email.alreadyExistException("email")
            null
        } ?: run {
            // If null then create a new user and generate access ton and refresh token
            userRepository.save(user.toVerifiedUser())?.let { userId ->
                val accessToken = jwtService.createAccessToken(user.email)
                val refreshToken = jwtService.createRefreshToken(user.email)

                // Store refresh token for further use.
                // For now saving access token serves no security feature
                tokensRepository.save(userId, refreshToken)?.let {
                    AuthResponse(
                        it.userId,
                        accessToken,
                        refreshToken
                    )
                }
            }
        }
    }

    /**
     * Login a user and return access token and refresh token
     */
    suspend fun authenticate(verifiedUserRequest: VerifiedUserRequest): AuthResponse? {

        val foundUser = userRepository.findByEmail(verifiedUserRequest.email)

        return if (foundUser != null) {
            val accessToken = jwtService.createAccessToken(foundUser.email)
            val refreshToken = jwtService.createRefreshToken(foundUser.email)

            tokensRepository.save(foundUser.id.toString(), refreshToken)

            AuthResponse(foundUser.id.toString(), accessToken, refreshToken)
        } else null
    }

    suspend fun refreshToken(token: String): String? {
        val decodedRefreshToken = verifyRefreshToken(token)
        val persistedUserName = tokensRepository.findUserIdByRefreshToken(token)?.let { userId ->
            userRepository.findById(UUID.fromString(userId))?.email
        }

        return if (decodedRefreshToken != null && persistedUserName != null) {

            val foundUser = userRepository.findByEmail(persistedUserName)
            val userNameFromRefreshToken = decodedRefreshToken.getClaim("email").asString()
            if (foundUser != null && foundUser.email == userNameFromRefreshToken) {
                jwtService.createAccessToken(foundUser.email)
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


suspend fun getAccessTokenInfo(accessToken: String) {
    val response = getHTTPClient().post("https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=$accessToken")
    response.let {
        it.call.response.status
        it.call.response.bodyAsBytes().decodeToString().let { Json.decodeFromString<User>(it) }
    }
}

lateinit var httpClient: HttpClient

fun getHTTPClient(): HttpClient {
    if(::httpClient.isInitialized.not()) {
        httpClient = HttpClient(CIO) {
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }

    return httpClient
}