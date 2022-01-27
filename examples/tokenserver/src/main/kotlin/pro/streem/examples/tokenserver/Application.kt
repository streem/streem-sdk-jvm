package pro.streem.examples.tokenserver

import io.github.cdimascio.dotenv.dotenv
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val dotenv = dotenv()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(Authentication) {
            // Use a hard-coded secret between server and client to avoid abuse if the server is deployed to a public
            // URL
            basic {
                val appSecret = dotenv["APP_SECRET"]
                validate { credentials ->
                    credentials.takeIf { it.password == appSecret }?.let { UserIdPrincipal(credentials.name) }
                }
            }
        }
        install(CallLogging)
        install(ContentNegotiation) {
            json()
        }

        configureStreem()
        configureRouting()
    }.start(wait = true)
}