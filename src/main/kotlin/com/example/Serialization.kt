package com.example

import com.example.model.Priority
import com.example.model.Task
import com.example.model.TaskRepository
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import java.sql.Connection
import java.sql.DriverManager
import org.jetbrains.exposed.sql.*
import java.lang.IllegalArgumentException

fun Application.configureSerialization(repository: TaskRepository) {

    install(ContentNegotiation){
        json()
    }

    routing {

        route("/tasks"){
            get {
                val tasks = repository.allTasks()
                call.respond(tasks)
            }

            get("/byName/{name}"){
                val name = call.parameters["name"]
                if(name == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val task = repository.taskByName(name)
                if(task == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                call.respond(task)
            }


            get("/byPriority/{priority}"){
                val priorityAsText = call.parameters["priority"]
                if(priorityAsText == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                try {
                    val priority = Priority.valueOf(priorityAsText)
                    val tasks = repository.tasksByPriority(priority)

                    if(tasks.isEmpty()){
                        call.respond(HttpStatusCode.NotFound)
                        return@get
                    }
                    call.respond(tasks)

                }catch (e: IllegalArgumentException){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post{
                try {
                    val task = call.receive<Task>()
                    repository.addTask(task)
                    call.respond(HttpStatusCode.Created)
                }catch (e: IllegalStateException){
                    call.respond(HttpStatusCode.BadRequest)
                }catch (e: JsonConvertException){
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            delete("/{taskName}") {
                val name = call.parameters["taskName"]
                if(name == null){
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                if(repository.deleteTaskByName(name)){
                    call.respond(HttpStatusCode.NoContent)
                }else{
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}
