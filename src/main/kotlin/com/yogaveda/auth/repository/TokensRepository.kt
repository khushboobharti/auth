package com.yogaveda.auth.repository

import com.yogaveda.auth.data.entities.TokensEntity
import com.yogaveda.auth.data.entities.TokensTable
import com.yogaveda.auth.util.extension.query

class TokensRepository {

    suspend fun save(userId: String, refreshToken: String): TokensEntity? {
        return try {
            query {
                val foundToken = TokensEntity.find {
                    TokensTable.userId eq userId
                }.firstOrNull()

                foundToken?.let {
                    //TokensEntity.delete(foundToken)
                    TokensEntity.findByIdAndUpdate(foundToken.id.toString()) {
                        //it.accessToken = accessToken
                        it.refreshToken = refreshToken
                    }
                } ?: run {
                    // Token not found, create a new entry for this user
                    TokensEntity.new {
                        this.userId = userId
                        //this.accessToken = accessToken
                        this.refreshToken = refreshToken
                    }
                }
            }
        } catch (e: Exception) {
            // Handle the exception
            println("Transaction failed: ${e.message}")
            null
        }
    }

    suspend fun findUserIdByRefreshToken(refreshToken: String): String? {
        return try {
            query {
                TokensEntity.find {
                    TokensTable.refreshToken eq refreshToken
                }.firstOrNull()?.userId
            }
        } catch (e: Exception) {
            // Handle the exception
            println("Transaction failed: ${e.message}")
            null
        }
    }

    suspend fun findRefreshTokenByUserId(userId: String): TokensEntity? {
        return try {
            query {
                TokensEntity.find {
                    TokensTable.userId eq userId
                }.firstOrNull()
            }
        } catch (e: Exception) {
            // Handle the exception
            println("Transaction failed: ${e.message}")
            null
        }
    }
}