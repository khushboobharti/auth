package com.yogaveda.auth.util.extension

import com.yogaveda.auth.util.CommonException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

fun String.notFoundException(): CommonException {
    return CommonException("$this does not exist")
}

fun String.alreadyExistException(secondaryInfo: String = ""): CommonException {
    return if (secondaryInfo.isEmpty()) CommonException("$this already exists")
    else CommonException("$this $secondaryInfo already exists")
}

suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction {
        block()
    }
}