package com.yogaveda.auth.routing.request

import com.yogaveda.auth.model.VerifiedUser
import kotlinx.serialization.Serializable

@Serializable
data class VerifiedUserRequest(
    val userId: String,
    val displayName: String,
    val email: String,
    val phoneNumber: String,
    val photoURL: String,
    val providerId: String,
    val accessToken: String,
    val refreshToken: String
) {
    fun toVerifiedUser(): VerifiedUser {
        return VerifiedUser(
            userId = userId,
            displayName = displayName,
            email = email,
            phoneNumber = phoneNumber,
            photoURL = photoURL,
            providerId = providerId,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}