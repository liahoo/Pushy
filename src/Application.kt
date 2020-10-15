package com.fascode

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import io.ktor.content.*
import io.ktor.http.content.*
import io.ktor.sessions.*
import io.ktor.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    routing {
        get("/") {
            call.respondHtmlTemplate(IndexPage()) {
                token = call.request.queryParameters["token"]
            }
        }
        post("/Push/Message") {
            val params = call.receiveParameters()
            params["token"]?.let { token ->
                val title = params["title"] ?: ""
                val body = params["message"] ?: ""
                val result = ApnsUtils.sendMessageToDevice(token, title, body)
                call.respondHtmlTemplate(ResultPage(result)) {
                    this.token = token
                }
            } ?: call.respond(HttpStatusCode.BadRequest, "token is not set")
        }
        post("/Push/Uninstall") {
            val params = call.receiveParameters()
            params["token"]?.let { token ->
                val result = ApnsUtils.verifyUninstall(token)
                call.respondHtmlTemplate(ResultPage(result)) {
                    this.token = token
                }
            } ?: call.respond(HttpStatusCode.BadRequest, "token is not set")
        }

        post("/Push/Retargeting") {
            val params = call.receiveParameters()
            params["token"]?.let { token ->
                val result = ApnsUtils.retarget(
                    token,
                    title = params.getOrFail("title"),
                    body =  params.getOrFail("body"),
                    pid = params.getOrFail("pid"),
                    c = params.getOrFail("c"),
                    development = false
                )
                call.respondHtmlTemplate(ResultPage(result)) {
                    this.token = token
                }
            } ?: call.respond(HttpStatusCode.BadRequest, "token is not set")
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") {
            resources("static")
        }

        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
    }
}

data class MySession(val count: Int = 0)

