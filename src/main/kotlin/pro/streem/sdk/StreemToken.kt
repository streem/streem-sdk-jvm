package pro.streem.sdk

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.util.Date

public class StreemToken internal constructor(
    public var userId: String
) {
    public var name: String? = null
    public var email: String? = null
    public var avatarUri: URI? = null
    public var tokenExpiration: Duration = DefaultTokenExpiration
    public var sessionExpiration: Duration = DefaultSessionExpiration
    public var reservationSid: String? = null

    public companion object {
        public val DefaultTokenExpiration: Duration = Duration.ofMinutes(5)
        public val DefaultSessionExpiration: Duration = Duration.ofHours(4)
    }
}

// Issuer prefix used for a Streem Token signed by an API Key. The API Key ID is provided after this
// prefix, and the API Key Secret is used to sign the Streem Token.
private const val STREEM_TOKEN_ISSUER_PREFIX = "streem:api:"

internal fun StreemToken.serialize(config: Config): String = SignedJWT(
    JWSHeader.Builder(JWSAlgorithm.ES256).build(),
    JWTClaimsSet.Builder().apply {
        issuer("$STREEM_TOKEN_ISSUER_PREFIX${config.apiKeyId}")
        subject(userId)
        audience(config.apiEndpoint.toString())
        issueTime(Date.from(Instant.now()))
        expirationTime(Date.from(Instant.now().plus(tokenExpiration)))
        claim("session_exp", Date.from(Instant.now().plus(sessionExpiration)))
        name?.let { claim("name", it) }
        email?.let { claim("email", it) }
        avatarUri?.let { claim("picture", it.toString()) }
        reservationSid?.let { claim("streem:reservation_sid", it) }
    }.build()
).apply {
    try {
        sign(
            try {
                ECDSASigner(config.apiKeySecret)
            } catch (e: JOSEException) {
                throw IllegalStateException("apiKeySecret could not be used for signing", e)
            }
        )
    } catch (e: JOSEException) {
        throw RuntimeException("Error signing token", e)
    }
}.serialize()
