package com.yogaveda.auth.service

import com.yogaveda.auth.repository.UserRepository
import com.yogaveda.auth.model.User
import java.util.UUID

class UserService (
    private val userRepository: UserRepository
) {

    fun findAll(): List<User> = userRepository.findAll()

    fun findById(id: String): User? = userRepository.findById(UUID.fromString(id))

    fun findByUsername(username: String): User? = userRepository.findByUsername(username)

    fun save(user: User): User? {
        return if(findByUsername(user.username) == null) {
            userRepository.save(user)
            user
        } else null
    }
}