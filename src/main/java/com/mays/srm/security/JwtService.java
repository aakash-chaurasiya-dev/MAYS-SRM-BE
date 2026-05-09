package com.mays.srm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

// STEP 3: THE WRISTBAND MAKER (JWT Service)
// We need a machine to make and verify "Tokens" (Wristbands) so users don't have to login on every click.
@Service
public class JwtService {

    // 1. The Secret Key (The ink used to stamp the wristband). Only this server knows it.
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 2. The Expiration Time (How long does the wristband last?)
    private final long JWT_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day in milliseconds

    /**
     * ACTION 1: Generate the Token when someone successfully logs in.
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // Who is this for? (Mobile No)
                .claim("roles", userDetails.getAuthorities()) // Add extra info like their role
                .setIssuedAt(new Date()) // When was it made? Now.
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) // When does it die? 1 day.
                .signWith(SECRET_KEY) // Sign it with our secret ink so nobody can forge it.
                .compact(); // Create the final String token.
    }

    /**
     * ACTION 2: Read the Token to find out who holds it.
     */
    public String extractUsername(String token) {
        // Read the "Subject" (Mobile No) from the parsed token
        return extractAllClaims(token).getSubject(); 
    }

    /**
     * ACTION 3: Verify the Token is real and belongs to the user trying to use it.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String mobileNoFromToken = extractUsername(token); // Get mobile from wristband
        final String mobileNoFromDB = userDetails.getUsername(); // Get mobile from DB records

        // Check if the mobile numbers match AND if the token is not expired
        return mobileNoFromToken.equals(mobileNoFromDB) && !isTokenExpired(token);
    }

    // Helper: Check if the token is expired
    private boolean isTokenExpired(String token) {
        Date expirationDate = extractAllClaims(token).getExpiration();
        return expirationDate.before(new Date()); // Is the expiration date before right now?
    }

    // Helper: Parse the token open using our secret key
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody(); // Return the body containing Subject, Expiration, etc.
    }
}
