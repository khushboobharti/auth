package com.yogaveda.auth.data.entities

import com.yogaveda.auth.data.entities.base.BaseIntEntity
import com.yogaveda.auth.data.entities.base.BaseIntEntityClass
import com.yogaveda.auth.data.entities.base.BaseIntIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.javatime.datetime

object UserTable: BaseIntIdTable("user") {
    val email = varchar("email", 255).uniqueIndex()
    val name = varchar("name", 255)
    val dob =  datetime("dob").nullable()
    override val primaryKey = PrimaryKey(id)
}

class UserEntity(id: EntityID<String>) : BaseIntEntity(id, UserTable) {
    companion object : BaseIntEntityClass<UserEntity>(UserTable)

    var email by UserTable.email
    var name by UserTable.name
    var dob by UserTable.dob

    //fun response() = UserResponse(UUID.fromString(id.value), email, name, dob, gender)
}
