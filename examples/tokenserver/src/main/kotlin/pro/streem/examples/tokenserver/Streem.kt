package pro.streem.examples.tokenserver

import io.github.cdimascio.dotenv.dotenv
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.util.AttributeKey
import pro.streem.sdk.Streem

val StreemInstance = AttributeKey<Streem>("Streem")

fun Application.configureStreem() {
    val dotenv = dotenv()
    val streem = Streem.getInstance(
        apiKeyId = dotenv["STREEM_API_KEY_ID"],
        apiKeySecret = dotenv["STREEM_API_KEY_SECRET"],
        apiEnvironment = dotenv["STREEM_API_ENV"],
    )

    intercept(ApplicationCallPipeline.Features) {
        call.attributes.put(StreemInstance, streem)
    }

}