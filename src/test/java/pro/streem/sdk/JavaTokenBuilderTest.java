package pro.streem.sdk;

import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JavaTokenBuilderTest {
    @Test
    public void allValuesGetSerializedCorrectly() throws ParseException {
        final var externalUserId = "user123456";
        final var name = "Test";
        final var avatarUri = URI.create("https://robohash.org/user123456");
        final var email = "test@streem.pro";
        final var tokenExpiration = Duration.ofMinutes(10);
        final var sessionExpiration = Duration.ofHours(1);

        final var streem = Streem.getInstance(TEST_API_KEY_ID, TEST_API_KEY_SECRET, TEST_API_ENVIRONMENT);

        final var token = streem.buildToken(externalUserId)
                .name(name)
                .avatarUri(avatarUri)
                .email(email)
                .tokenExpiration(tokenExpiration)
                .sessionExpiration(sessionExpiration)
                .build();

        final var claims = assertDoesNotThrow(() -> SignedJWT.parse(token).getJWTClaimsSet());

        assertEquals(claims.getIssuer(), "streem:api:" + TEST_API_KEY_ID);
        assertEquals(claims.getSubject(), externalUserId);
        assertEquals(claims.getAudience(), List.of("https://api." + TEST_API_ENVIRONMENT + ".streem.cloud/"));

        assertEquals(claims.getStringClaim("name"), name);
        assertEquals(claims.getURIClaim("picture"), avatarUri);
        assertEquals(claims.getStringClaim("email"), email);

        // Expiration dates should be within one second
        assertTrue(Duration.between(Instant.now(), claims.getExpirationTime().toInstant())
                .minus(tokenExpiration)
                .abs()
                .compareTo(Duration.ofSeconds(1)) < 0
        );
        assertTrue(Duration.between(Instant.now(), claims.getDateClaim("session_exp").toInstant())
                .minus(sessionExpiration)
                .abs()
                .compareTo(Duration.ofSeconds(1)) < 0
        );
    }

    private static final String TEST_API_KEY_ID = "api_4l1HLLbAKfiNcyU5fyPxPu";
    private static final String TEST_API_KEY_SECRET = "eyJrdHkiOiJFQyIsImQiOiJzR3VqdFFrZEJkcmFvN0NMUmZTMlQ0N1M1STdwV2NleTNwMzV6RWtmLXVnIiwidXNlIjoic2lnIiwiY3J2IjoiUC0yNTYiLCJ4IjoiY3VMYi1oZTg1NkFRM2NNOW9xVnBPRWJWcEg2WGJsXy16ZmdQTEFPdGVIOCIsInkiOiJwQ180eEtVYUVvQ3B1X0p0MktVNXY3ZjR1VXZ4ZllnNDc4MHNWYkpqd2dNIiwiYWxnIjoiRVMyNTYifQ";
    private static final String TEST_API_ENVIRONMENT = "test-us";
}