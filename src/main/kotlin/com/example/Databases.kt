package com.example

import org.jetbrains.exposed.sql.Database

fun configureDatabases() {
    Database.connect(
        url =  "jdbc:mysql://localhost:3306/ktor_task_db",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root",
        password = "Rubysankar@04"
    )
}
