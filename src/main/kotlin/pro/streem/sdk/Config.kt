package pro.streem.sdk

import com.nimbusds.jose.jwk.ECKey
import java.net.URI
import java.net.URISyntaxException
import java.text.ParseException
import java.util.Base64

internal data class Config(val apiKeyId: String, val apiKeySecret: ECKey, val apiEndpoint: URI) {
    @Throws(IllegalArgumentException::class)
    constructor(apiKeyId: String, apiKeySecret: String, apiEnvironment: String) : this(
        apiKeyId = apiKeyId,

        apiKeySecret = try {
            ECKey.parse(Base64.getDecoder().decode(apiKeySecret).decodeToString(throwOnInvalidSequence = true))
        } catch (e: CharacterCodingException) {
            throw IllegalArgumentException("apiKeySecret contains invalid UTF-8", e)
        } catch (e: ParseException) {
            throw IllegalArgumentException("apiKeySecret does not contain a JWK after base64-decoding", e)
        },

        apiEndpoint = try {
            URI("https", "api.$apiEnvironment.streem.cloud", "/", null)
        } catch (e: URISyntaxException) {
            throw IllegalArgumentException("apiEnvironment contains invalid characters", e)
        },
    )

    init {
        require(apiKeySecret.isPrivate) { "apiKeySecret is missing private key information" }
    }
}
