package com.yogaveda.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class VerifiedUser(
    val userId: String,
    val displayName: String,
    val email: String,
    val phoneNumber: String,
    val photoURL: String,
    val providerId: String,
    val accessToken: String,
    val refreshToken: String
)