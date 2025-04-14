package com.yogaveda.auth.repository

import com.yogaveda.auth.data.entities.UserAuthenticationMethodEntity
import com.yogaveda.auth.data.entities.UserEntity
import com.yogaveda.auth.data.entities.UserTable
import com.yogaveda.auth.data.entities.base.currentUtc
import com.yogaveda.auth.model.User
import com.yogaveda.auth.model.VerifiedUser
import com.yogaveda.auth.util.extension.query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID

class UserRepository {

    private val users = mutableListOf<User>()

    suspend fun save(user: VerifiedUser): String? {
        // If the transaction is successful return user response or null.
        return try {
            query {
                //UserEntity.find( UserTable.email eq "mailrahulkthakur@gmail.com").firstOrNull()
                val newUserEntity = UserEntity.new {
                    this.email = user.email
                    this.name = user.displayName
                    this.dob = currentUtc()
                }
                UserAuthenticationMethodEntity.new {
                    this.userId = newUserEntity.id.toString()
                    //this.authMethod = AuthenticationMethods.GOOGLE
                    this.accessToken = user.accessToken
                    this.refreshToken = user.refreshToken
                }
                newUserEntity.id.value
            }
        } catch (e: Exception) {
            // Handle the exception
            println("Transaction failed: ${e.message}")
            null
        }
    }


    suspend fun findAll(): List<User> {
        return query {
            UserEntity.all().map {
                User(
                    id = UUID.fromString(it.id.toString()),
                    email = it.email,
                    name = it.name,
                    dob = it.dob.toString()
                )
            }
        }
    }

    suspend fun findById(id: UUID): User? {
        return query {
            UserEntity.find(UserTable.id eq id.toString()).singleOrNull()?.let {
                User(
                    id = UUID.fromString(it.id.toString()),
                    email = it.email,
                    name = it.name,
                    dob = it.dob.toString()
                )
            }
        }
    }

    suspend fun findByEmail(email: String): User? {
        return query {
            UserEntity.find(UserTable.email eq email).singleOrNull()?.let {
                User(
                    id = UUID.fromString(it.id.toString()),
                    email = it.email,
                    name = it.name,
                    dob = it.dob.toString()
                )
            }
        }
    }

}
