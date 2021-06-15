package logion.backend.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import logion.backend.model.DefaultAddresses;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class JWTTest {

    private static final String SECRET = "secret-key-that-no-one-could-possibly-know";
    private static final String SESSION_ID = "9f2a616b-4791-4fd4-a573-51b3f3e897b0";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJkZXYubG9naW9uLm5ldHdvcmsiLCJzdWIiOiI1R3J3dmFFRjV6WGIyNkZ6OXJjUXBEV1M1N0N0RVJIcE5laFhDUGNOb0hHS3V0UVkiLCJqdGkiOiI5ZjJhNjE2Yi00NzkxLTRmZDQtYTU3My01MWIzZjNlODk3YjAiLCJpYXQiOjE2MjM2NzQwOTksImV4cCI6MTgyMzY3NDA5OSwibGVnYWxPZmZpY2VyIjp0cnVlfQ.BkexoDdhnpBYN1x07F2aCANjaIyHRVPSLxjF8oUTe_Q";
    private static final String ISSUER = "dev.logion.network";

    @Test
    void encode() throws Exception {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String encoded = Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(DefaultAddresses.ALICE.getRawValue())
                .setId(SESSION_ID)
                .setIssuedAt(Date.from(Instant.ofEpochSecond(1623674099)))
                .setExpiration(Date.from(Instant.ofEpochSecond(1823674099)))
                .claim("legalOfficer", true)
                .signWith(secretKey)
                .compact();
        assertThat(encoded, equalTo(TOKEN));
    }

    @Test
    void decode() {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

        var jwt = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .requireIssuer(ISSUER)
                .requireSubject(DefaultAddresses.ALICE.getRawValue())
                .require("legalOfficer", true)
                .build()
                .parseClaimsJws(TOKEN);
        assertThat(jwt.getBody().getIssuer(), equalTo(ISSUER));
        assertThat(jwt.getBody().getSubject(), equalTo(DefaultAddresses.ALICE.getRawValue()));
        assertThat(jwt.getBody().get("legalOfficer", Boolean.class), equalTo(true));
    }
}
