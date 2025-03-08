package com.yogaveda.auth.repository

import com.yogaveda.auth.data.entities.AuthenticationMethods
import com.yogaveda.auth.data.entities.UserAuthenticationMethodEntity
import com.yogaveda.auth.data.entities.UserEntity
import com.yogaveda.auth.data.entities.UserTable
import com.yogaveda.auth.data.entities.base.currentUtc
import com.yogaveda.auth.model.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UserRepository {

    private val users = mutableListOf<User>()

    fun save(user: User) : Boolean =
        users.add(user)

    /*fun add(user: User):  Boolean {
        val retrievedUser = UserEntity.find (UserTable.email eq user.username).singleOrNull()

        // Now make a call to the Google Server to make sure the retrieved value is correct
        // and get other information if required
        retrievedUser?.let {
            // throw exception as email already exists
        } ?: run {
            // If the transaction is successful return user response or just throw an error.
            transaction {
                val newUserEntity = UserEntity.new {
                    this.email = "email"
                    this.name = "name"
                    this.dob = currentUtc()
                    this.gender = "gender"
                }
                UserAuthenticationMethodEntity.new {
                    this.user_id = newUserEntity.id.toString()
                    this.auth_method = AuthenticationMethods.GOOGLE
                    this.value = "user_token"
                    this.isVerified = true

                    // A primary login method can be selected later
                    *//*this.isPrimary = true
                    this.isMFAEnabled = false
                    this.isActive = true*//*
                }
                newUserEntity.id.value
            }

        }
    }*/

    fun findAll() : List<User> =
        users

    fun findById(id: UUID) : User? =
        users.firstOrNull { it.id == id }

    fun findByUsername(username: String) : User? =
        users.firstOrNull { it.username == username }

}