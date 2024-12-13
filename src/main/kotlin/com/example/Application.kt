package com.example

import com.example.model.MySqlTaskRepository
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
fun Application.module() {
    val repository = MySqlTaskRepository()
    configureSerialization(repository)
    configureDatabases()
    configureRouting()
}
