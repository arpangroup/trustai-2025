package com.trustai.common_base.security.jwt;

import com.trustai.common_base.constants.SecurityConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtProvider {
    private final SecretKey key;
    private final String issuer;
    //private final long expirationMs;
    public static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";

    //private static final long EXPIRE_INTERVAL = 1000 * 60 * 30; // 1000 ms (1 second) * 60 = 1 minute × 30 = 30 minutes → 1,800,000 ms

   /* public JwtProvider(AppProperties appProperties) {
        String secret = appProperties.getJwt().getSecret();
        if (secret == null || secret.trim().isEmpty()) {
            // for dev only — recommend overriding with real secret
            secret = "dev-secret-key-change-me-please-very-long-string";
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = appProperties.getJwt().getExpirationMs();
    }*/

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.issuer}") String issuer,
                       @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.issuer = issuer;
        //this.expirationMs = expirationMs;
    }

    public String generateToken(String username) { // Use email as username
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + SecurityConstants.JWT_EXPIRE_MILLS);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                //.signWith(key, SignatureAlgorithm.HS256)
                .compact();

        /*Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claim("sub", username)
                .claim("jti", UUID.randomUUID().toString())
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();*/
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /*public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }*/

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                //.setSigningKey(key)
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
