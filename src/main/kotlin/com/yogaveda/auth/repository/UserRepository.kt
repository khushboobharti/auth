package com.yogaveda.auth.repository

import com.yogaveda.auth.model.User
import java.util.UUID

class UserRepository {

    private val users = mutableListOf<User>()

    fun save(user: User) : Boolean =
        users.add(user)

    fun findAll() : List<User> =
        users

    fun findById(id: UUID) : User? =
        users.firstOrNull { it.id == id }

    fun findByUsername(username: String) : User? =
        users.firstOrNull { it.username == username }

}