package com.yogaveda.auth.model

import com.yogaveda.auth.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val email:  String,
    val name: String,
    val dob: String
)
