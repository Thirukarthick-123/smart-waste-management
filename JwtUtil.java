    package com.example.smartwaste.security;

    import io.jsonwebtoken.*;
    import io.jsonwebtoken.security.Keys;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Component;

    import java.security.Key;
    import java.util.Date;

    /**
     * Utility class for generating and validating JWT tokens.
     */
    @Component
    @Slf4j
    public class JwtUtil {

        private final Key key;
        private final long jwtExpirationMs;

        public JwtUtil(@Value("${jwt.secret}") String secret,
                @Value("${jwt.expirationMs}") long expirationMs) {
            this.key = Keys.hmacShaKeyFor(secret.getBytes());
            this.jwtExpirationMs = expirationMs;
        }

        public String generateToken(String username, String role) {
            return Jwts.builder()
                    .setSubject(username)
                    .claim("role", role)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }

        public boolean validateToken(String token) {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token);
                return true;
            } catch (JwtException ex) {
                log.warn("Invalid JWT token: {}", ex.getMessage());
            }
            return false;
        }

        public String extractUsername(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }

        public String extractRole(String token) {
            return (String) Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role");
        }
    }
