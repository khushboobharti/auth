package com.yogaveda.auth.data.entities

import com.yogaveda.auth.data.entities.base.BaseIntEntity
import com.yogaveda.auth.data.entities.base.BaseIntEntityClass
import com.yogaveda.auth.data.entities.base.BaseIntIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object UserAuthenticationMethodTable: BaseIntIdTable("user_authentication_method") {
    val userId = varchar("user_id", 255).references(UserTable.id, onDelete = ReferenceOption.CASCADE)
    //val authMethod = enumeration<AuthenticationMethods>("auth_method").default(AuthenticationMethods.GOOGLE)   //varchar("name", 255)
    val accessToken =  varchar("accessToken", 512)
    val refreshToken = varchar("refreshToken", 512)
    override val primaryKey = PrimaryKey(id)
}

class UserAuthenticationMethodEntity(id: EntityID<String>) : BaseIntEntity(id, UserAuthenticationMethodTable) {
    companion object : BaseIntEntityClass<UserAuthenticationMethodEntity>(UserAuthenticationMethodTable)

    var userId by UserAuthenticationMethodTable.userId
    //var authMethod by UserAuthenticationMethodTable.authMethod
    var accessToken by UserAuthenticationMethodTable.accessToken
    var refreshToken by UserAuthenticationMethodTable.refreshToken

    //fun response() = UserResponse(UUID.fromString(id.value), email, name, dob, gender)
}

enum class AuthenticationMethods {
    GOOGLE,
    FACEBOOK,
    EMAIL,
    PHONE
}