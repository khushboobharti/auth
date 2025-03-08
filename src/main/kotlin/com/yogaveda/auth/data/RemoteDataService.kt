package com.yogaveda.auth.data

import com.yogaveda.auth.data.entities.UserAuthenticationMethodTable
import com.yogaveda.auth.data.entities.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import javax.sql.DataSource

// class RemoteDataService() {}

lateinit var postgresDB : Database

fun configureDatabase() {
    initDB()
    if(::postgresDB.isInitialized) {
        transaction(postgresDB) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(
                UserTable,
                UserAuthenticationMethodTable
            )
        }
    }
}

/**
 * Hikari is used for database connection pooling
 * Check the [hikari documentation](https://github.com/brettwooldridge/HikariCP) for more information
 */
private fun initDB() {
    // database connection is handled from hikari properties
    val config = HikariConfig("/hikari.properties")
    val dataSource = HikariDataSource(config)
    //runFlyway(dataSource)
    postgresDB = Database.connect(dataSource)
}

/**
 * Flyway migration - can be used to migrate database schema
 * it checks the schema_table for the last migration and then apply the current migration if the version is higher
 * Check the [flyway documentation](https://documentation.red-gate.com/flyway/getting-started-with-flyway/quickstart-guides/quickstart-api) for more information
 */
private fun runFlyway(datasource: DataSource) {
    val flyway = Flyway.configure().dataSource(datasource).load()
    try {
        flyway.info()
        flyway.migrate()
    } catch (e: Exception) {
        throw e
    }
}