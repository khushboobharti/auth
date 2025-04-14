package com.yogaveda.auth.data.entities

import com.yogaveda.auth.data.entities.base.BaseIntEntity
import com.yogaveda.auth.data.entities.base.BaseIntEntityClass
import com.yogaveda.auth.data.entities.base.BaseIntIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Stores the JWT tokens issued by Yogaveda .
 * @property userId The user that owns the tokens.
 * // @property accessToken The access token issued by Yogaveda.
 * @property refreshToken The refresh token issued by Yogaveda.
 */
object TokensTable: BaseIntIdTable("tokens") {
    val userId = varchar("user_id", 255).references(UserTable.id, onDelete = ReferenceOption.CASCADE)
    //val accessToken =  varchar("value", 512)
    val refreshToken = varchar("refreshToken", 512)
    override val primaryKey = PrimaryKey(id)
}

class TokensEntity(id: EntityID<String>) : BaseIntEntity(id, TokensTable) {
    companion object : BaseIntEntityClass<TokensEntity>(TokensTable)

    var userId by TokensTable.userId
    //var accessToken by TokensTable.accessToken
    var refreshToken by TokensTable.refreshToken

    //fun response() = UserResponse(UUID.fromString(id.value), email, name, dob, gender)
}