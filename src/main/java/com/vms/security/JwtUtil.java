package com.vms.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility component for JSON Web Token (JWT) generation and validation.
 *
 * <p>Uses HMAC-SHA256 signing with a configurable secret key and expiration
 * period. Tokens carry the user's email address as the subject claim.</p>
 *
 * @see com.vms.filter.JwtAuthenticationFilter
 */
@Component
public class JwtUtil {

    /** Base64-encoded or plain-text secret used for HMAC-SHA256 signing. */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /** Token time-to-live in milliseconds. */
    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Derives the HMAC-SHA signing key from the configured secret.
     *
     * <p>If the secret is shorter than 43 characters it is padded to meet
     * the minimum 256-bit key length requirement. Otherwise, a Base64 decode
     * is attempted first, falling back to raw bytes.</p>
     *
     * @return the {@link SecretKey} used for signing and verification
     */
    private SecretKey getSigningKey() {
        // Ensure the key is at least 256 bits for HS256
        byte[] keyBytes;
        if (jwtSecret.length() < 43) {
            // Pad the secret to meet minimum key length
            String padded = jwtSecret + "0".repeat(43 - jwtSecret.length());
            keyBytes = padded.getBytes();
        } else {
            try {
                keyBytes = Decoders.BASE64.decode(jwtSecret);
            } catch (Exception e) {
                keyBytes = jwtSecret.getBytes();
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT for the authenticated principal.
     *
     * @param authentication the current authentication containing a {@link UserDetails} principal
     * @return the signed JWT string
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateToken(userDetails.getUsername());
    }

    /**
     * Generates a JWT for the given email address.
     *
     * @param email the user's email used as the token subject
     * @return the signed JWT string
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the email (subject claim) from a signed JWT.
     *
     * @param token the JWT string
     * @return the email address stored in the token's subject claim
     * @throws JwtException if the token is invalid or expired
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates whether a JWT is well-formed, correctly signed, and not expired.
     *
     * @param token the JWT string to validate
     * @return {@code true} if the token is valid; {@code false} otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
