package pro.streem.sdk

import java.net.URI
import java.time.Duration

public class TokenBuilder internal constructor(private val config: Config, userId: String) {
    private val token = StreemToken(userId)

    public val userId: String get() = token.userId

    public fun userId(userId: String): TokenBuilder = apply {
        token.userId = userId
    }

    public val name: String? get() = token.name

    public fun name(name: String?): TokenBuilder = apply {
        token.name = name
    }

    public val email: String? get() = token.email

    public fun email(email: String?): TokenBuilder = apply {
        token.email = email
    }

    public val avatarUri: URI? get() = token.avatarUri

    public fun avatarUri(avatarUri: URI?): TokenBuilder = apply {
        token.avatarUri = avatarUri
    }

    public val tokenExpiration: Duration get() = token.tokenExpiration

    public fun tokenExpiration(tokenExpiration: Duration?): TokenBuilder = apply {
        token.tokenExpiration = tokenExpiration ?: StreemToken.DefaultTokenExpiration
    }

    public val sessionExpiration: Duration get() = token.sessionExpiration

    public fun sessionExpiration(sessionExpiration: Duration?): TokenBuilder = apply {
        token.sessionExpiration = sessionExpiration ?: StreemToken.DefaultSessionExpiration
    }

    public val reservationSid: String? get() = token.reservationSid

    public fun reservationSid(reservationSid: String?): TokenBuilder = apply {
        token.reservationSid = reservationSid
    }

    public fun build(): String = token.serialize(config)
}
