package com.example

import com.example.model.Priority
import com.example.model.Task
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun taskCanBeFoundByPriority() = testApplication {
        application {
            val repository = SampleTaskRepository()
            configureRouting()
            configureSerialization(repository)
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }
        val response = client.get("/tasks/byPriority/Medium")
        val result = response.body<List<Task>>()

        assertEquals(HttpStatusCode.OK, response.status)

        val expectedList = listOf("gardening", "painting")
        val actualList = result.map { it.name }

        assertEquals(expectedList.size, actualList.size)
        assertContentEquals(expectedList, actualList)
    }

    @Test
    fun invalidPriorityProduces400() = testApplication {
        application {
            val repository = SampleTaskRepository()
            configureRouting()
            configureSerialization(repository)
        }

        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }

        val response = client.get("/tasks/byPriority/Invalid")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun unusedPriorityProduces404() = testApplication {
        application {
            val repository = SampleTaskRepository()
            configureRouting()
            configureSerialization(repository)
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }

        val response = client.get("/tasks/byPriority/Vital")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun taskCanBeAdded() = testApplication {
        application {
            val repository = SampleTaskRepository()
            configureRouting()
            configureSerialization(repository)
        }
        val client = createClient {
            install(ContentNegotiation){
                json()
            }
        }

        val task = Task(
            name = "swimming",
            description = "Going to the beach",
            priority = Priority.Low
        )

        val response1 = client.post("/tasks"){
            header(HttpHeaders.ContentType, ContentType.Application.Json)

            setBody(task)
        }

        assertEquals(HttpStatusCode.Created, response1.status)

        val response2 = client.get("/tasks")

        assertEquals(HttpStatusCode.OK, response2.status)

        val body = response2.body<List<Task>>().map { it.name }

        assertContains(body, "swimming")

    }



}
