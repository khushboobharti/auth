package com.yogaveda.auth.routing.response

import kotlinx.serialization.Serializable

@Serializable
data class VerifiedUserResponse (
    val userId: String,
    val accessToken: String,
    val refreshToken: String
)