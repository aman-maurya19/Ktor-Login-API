package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object UserTable : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val email = varchar("email", 50).uniqueIndex()
    val mobile = varchar("mobile", 15)
    val password = varchar("password", 100)
    override val primaryKey = PrimaryKey(id)
}

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            // Render ka URL yahan jayega ya environment variable se aayega
            jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/test"
            maximumPoolSize = 3
            isReadOnly = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(UserTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction { block() }
}