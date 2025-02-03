package com.yogaveda.auth.repository

class RefreshTokenRepository {

    val tokens = mutableMapOf<String, String>()

    fun findUsernameByToken(token: String): String? {
        return tokens[token]
    }

    fun save(username: String, token: String) {
        tokens[token] = username
    }
}