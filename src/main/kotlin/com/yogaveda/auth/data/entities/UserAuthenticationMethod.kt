package com.yogaveda.auth.data.entities

import com.yogaveda.auth.data.entities.base.BaseIntEntity
import com.yogaveda.auth.data.entities.base.BaseIntEntityClass
import com.yogaveda.auth.data.entities.base.BaseIntIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

object UserAuthenticationMethodTable: BaseIntIdTable("user_authentication_method") {
    val user_id = varchar("user_id", 255).references(UserTable.id, onDelete = ReferenceOption.CASCADE)
    val auth_method = enumeration<AuthenticationMethods>("auth_method").default(AuthenticationMethods.GOOGLE)   //varchar("name", 255)
    val value =  varchar("value", 512)
    val isVerified = bool("is_verified").default(false)
    override val primaryKey = PrimaryKey(id)
}

class UserAuthenticationMethodEntity(id: EntityID<String>) : BaseIntEntity(id, UserAuthenticationMethodTable) {
    companion object : BaseIntEntityClass<UserAuthenticationMethodEntity>(UserAuthenticationMethodTable)

    var user_id by UserAuthenticationMethodTable.user_id
    var auth_method by UserAuthenticationMethodTable.auth_method
    var value by UserAuthenticationMethodTable.value
    var isVerified by UserAuthenticationMethodTable.isVerified

    //fun response() = UserResponse(UUID.fromString(id.value), email, name, dob, gender)
}

enum class AuthenticationMethods {
    GOOGLE,
    FACEBOOK,
    EMAIL,
    PHONE
}