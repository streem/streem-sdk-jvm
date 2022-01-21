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

public class TokenBuilder internal constructor(
    private val config: Config,
    public val userId: String
) {
    public var name: String? = null
        private set

    public fun name(name: String?): TokenBuilder = apply {
        this.name = name
    }

    public var email: String? = null
        private set

    public fun email(email: String?): TokenBuilder = apply {
        this.email = email
    }

    public var avatarUri: URI? = null
        private set

    public fun avatarUri(avatarUri: URI?): TokenBuilder = apply {
        this.avatarUri = avatarUri
    }

    public var tokenExpiration: Duration = DEFAULT_TOKEN_EXPIRATION
        private set

    public fun tokenExpiration(tokenExpiration: Duration?): TokenBuilder = apply {
        this.tokenExpiration = tokenExpiration ?: DEFAULT_TOKEN_EXPIRATION
    }

    public var sessionExpiration: Duration = DEFAULT_SESSION_EXPIRATION
        private set

    public fun sessionExpiration(sessionExpiration: Duration?): TokenBuilder = apply {
        this.sessionExpiration = sessionExpiration ?: DEFAULT_SESSION_EXPIRATION
    }

    public fun build(): String = SignedJWT(
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

    public companion object {
        // Issuer prefix used for a Streem Token signed by an API Key. The API Key ID is provided after this
        // prefix, and the API Key Secret is used to sign the Streem Token.
        private const val STREEM_TOKEN_ISSUER_PREFIX = "streem:api:"

        private val DEFAULT_TOKEN_EXPIRATION = Duration.ofMinutes(5)
        private val DEFAULT_SESSION_EXPIRATION = Duration.ofHours(4)
    }
}
