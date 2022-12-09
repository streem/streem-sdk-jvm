package pro.streem.sdk

import com.nimbusds.jwt.SignedJWT
import org.junit.jupiter.api.assertDoesNotThrow
import java.net.URI
import java.time.Duration
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class TokenBuilderTest {
    @Test
    fun `all values get serialized correctly`() {
        val externalUserId = "user123456"
        val name = "Test"
        val avatarUri = URI.create("https://robohash.org/user123456")
        val email = "test@streem.pro"
        val tokenExpiration = Duration.ofMinutes(10)
        val sessionExpiration = Duration.ofHours(1)
        val reservationSid = "rsv_abc123"

        val streem = Streem.getInstance(
            apiKeyId = TEST_API_KEY_ID,
            apiKeySecret = TEST_API_KEY_SECRET,
            apiEnvironment = TEST_API_ENVIRONMENT
        )

        val token = streem.buildToken(externalUserId) {
            this.name = name
            this.avatarUri = avatarUri
            this.email = email
            this.tokenExpiration = tokenExpiration
            this.sessionExpiration = sessionExpiration
            this.reservationSid = reservationSid
        }

        val claims = assertDoesNotThrow { SignedJWT.parse(token).jwtClaimsSet }

        assertEquals(claims.issuer, "streem:api:$TEST_API_KEY_ID")
        assertEquals(claims.subject, externalUserId)
        assertEquals(claims.audience, listOf("https://api.$TEST_API_ENVIRONMENT.streem.cloud/"))

        assertEquals(claims.getStringClaim("name"), name)
        assertEquals(claims.getURIClaim("picture"), avatarUri)
        assertEquals(claims.getStringClaim("email"), email)
        assertEquals(claims.getStringClaim("streem:reservation_sid"), reservationSid)

        // Expiration dates should be within a few seconds
        assertTrue {
            Duration.between(Instant.now(), claims.expirationTime.toInstant()) in
                tokenExpiration.withErrorRange(Duration.ofSeconds(5))
        }
        assertTrue {
            Duration.between(Instant.now(), claims.getDateClaim("session_exp").toInstant()) in
                sessionExpiration.withErrorRange(Duration.ofSeconds(5))
        }
    }

    companion object {
        const val TEST_API_KEY_ID = "api_4l1HLLbAKfiNcyU5fyPxPu"
        const val TEST_API_KEY_SECRET =
            "eyJrdHkiOiJFQyIsImQiOiJzR3VqdFFrZEJkcmFvN0NMUmZTMlQ0N1M1STdwV2NleTNwMzV6RWtmLXVnIiwidXNlIjoic2lnIiwiY3J2IjoiUC0yNTYiLCJ4IjoiY3VMYi1oZTg1NkFRM2NNOW9xVnBPRWJWcEg2WGJsXy16ZmdQTEFPdGVIOCIsInkiOiJwQ180eEtVYUVvQ3B1X0p0MktVNXY3ZjR1VXZ4ZllnNDc4MHNWYkpqd2dNIiwiYWxnIjoiRVMyNTYifQ"
        const val TEST_API_ENVIRONMENT = "test-us"
    }
}

private fun Duration.withErrorRange(epsilon: Duration) = this.minus(epsilon)..this.plus(epsilon)
