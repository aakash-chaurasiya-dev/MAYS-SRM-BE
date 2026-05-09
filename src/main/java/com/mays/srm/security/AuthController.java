package com.mays.srm.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// STEP 6: THE TICKET BOOTH (AuthController)
// The user needs an actual URL (`/api/auth/login`) to send their mobile number and password to.
// This is a standard REST Controller that receives the JSON payload.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // The Boss who checks the password

    @Autowired
    private JwtService jwtService; // The Wristband Maker

    @Autowired
    private UserDetailsService userDetailsService; // The Database Clerk

    /**
     * POST /api/auth/login
     * User sends JSON: { "mobileNo": "123", "password": "password" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        // 1. Grab what the user typed in
        String mobileNo = request.get("mobileNo");
        String password = request.get("password");

        // 2. Tell the Boss (AuthenticationManager) to check if the credentials match.
        // It automatically goes to the DB (via UserDetailsServiceImpl) and hashes the password to verify.
        // If the password is wrong or user is locked out, it automatically throws a 403 Forbidden Exception here!
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(mobileNo, password)
        );

        // 3. If it reaches this line, the login was successful! 
        // We fetch their full details (Role, Active Status)
        final UserDetails userDetails = userDetailsService.loadUserByUsername(mobileNo);

        // 4. We ask the Maker to create a shiny new Token for this user
        final String jwt = jwtService.generateToken(userDetails);

        // 5. Return the Token to the User's browser/app as a JSON response!
        return ResponseEntity.ok(Map.of("token", jwt));
    }
}
