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
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJkZXYubG9naW9uLm5ldHdvcmsiLCJzdWIiOiI1R3J3dmFFRjV6WGIyNkZ6OXJjUXBEV1M1N0N0RVJIcE5laFhDUGNOb0hHS3V0UVkiLCJpYXQiOjE2MjM2NzQwOTksImV4cCI6MTgyMzY3NDA5OSwibGVnYWxPZmZpY2VyIjp0cnVlfQ.DYfQDAugVG7Sp3IBtwK_1rN_eHlr4-7axU4KTDvVCho";
    private static final String ISSUER = "dev.logion.network";
    private static final String ROLE_LEGAL_OFFICER = "legalOfficer";

    @Test
    void encode() {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String encoded = Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(DefaultAddresses.ALICE.getRawValue())
                .setIssuedAt(Date.from(Instant.ofEpochSecond(1623674099)))
                .setExpiration(Date.from(Instant.ofEpochSecond(1823674099)))
                .claim(ROLE_LEGAL_OFFICER, true)
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
                .require(ROLE_LEGAL_OFFICER, true)
                .build()
                .parseClaimsJws(TOKEN);
        assertThat(jwt.getBody().getIssuer(), equalTo(ISSUER));
        assertThat(jwt.getBody().getSubject(), equalTo(DefaultAddresses.ALICE.getRawValue()));
        assertThat(jwt.getBody().get(ROLE_LEGAL_OFFICER, Boolean.class), equalTo(true));
    }
}
