package com.example.temp

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.*
import io.ktor.client.features.logging.*
import io.ktor.http.content.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.apache.http.HttpStatus
import org.slf4j.event.Level
import java.io.File


val staticfilesDir = File("resources/static")

fun Application.main() {
    install(DefaultHeaders)
    install(ContentNegotiation) {
        gson {
        }
    }
    install(CallLogging){
        level = Level.INFO
    }
    install(Routing){
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/html-dsl") {
            call.respondHtml {
                body {
                    h1 { +"HTML" }
                    ul {
                        for (n in 1..10) {
                            li { +"$n" }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }

        get("/music/{name}"){
            call.respondMP3(call.parameters["name"]!!)
        }

       /* static {
            route("static"){
                files(File("resources/static"))
            }
        }*/
    }

}

data class JsonSampleClass(val hello: String)

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend fun ApplicationCall.respondMP3(filename: String){
    println(filename)
    //val file = File(if (filename.contains(".mp3")) filename else "$filename.mp3")
    //println("fiel exists: ${file.exists()}")
    //resour

    this.respondFile(staticfilesDir, if (filename.contains(".mp3")) filename else "$filename.mp3")
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
