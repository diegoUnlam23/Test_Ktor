package com.example.test

import androidx.room.vo.Database
import com.example.Model.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.staticResources
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.netty.*
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val repo = FakeTaskRepository()

    configureRouting( repo )
    configureDataBase()
}
fun Application.configureRouting( repo : FakeTaskRepository )
{
    routing {

        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        staticResources("/","mcaobra","mcaobra.html")
        route("/tasks") {
            //install(ContentNegotiation) {
            //    json()
            //}
            get {
                val tasks = FakeTaskRepository.allTasks()
                call.respond(tasks)
            }
            get("/obras")
            {
                try {
                    val obras = FakeTaskRepository.allTasks()
                    if (obras.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)
                    }
                    call.respond(obras)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }

            }
            get("/legajo")
            {

            }
            post {
                val formContent = call.receiveParameters()
                val params = Triple(
                    formContent["legajo"] ?: "",
                    formContent["description"] ?: "",
                    formContent["priority"] ?: ""
                )
                if (params.toList().any { it.isEmpty() }) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                try {
                    val priority = Priority.valueOf(params.third)
                    FakeTaskRepository.addTask(
                        task = Task(
                            id = params.first,
                            descr = params.second,
                            priority = priority
                        )
                    )

                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }




            }
        }
    }
}
fun Application.configureDataBase()
{
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres",
        user = "postgres",
        password = "postgres"
    )
}
