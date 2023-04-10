package pro.streem.examples.tokenserver

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import kotlinx.serialization.Serializable
import java.net.URI

fun Application.configureRouting() {
    routing {
        authenticate {
            post("/token") {
                // In a real-life deployment the user info would probably come from an internal database or
                // authentication service rather than being provided by the client in the request.
                val req = call.receive<TokenRequest>()

                val streem = call.attributes[StreemInstance]
                val token = streem.buildToken(req.id) {
                    name = req.name
                    email = req.email
                    avatarUri = URI.create("https://robohash.org/$userId.png")
                }

                call.respond(TokenResponse(token = token))
            }
        }
    }
}

@Serializable
data class TokenRequest(val id: String, val name: String, val email: String)

@Serializable
data class TokenResponse(val token: String)
