package com.mays.srm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// STEP 4: THE SECURITY GUARD (JwtAuthFilter)
// This Guard stands at the door of every single API endpoint.
// It intercepts the HTTP request, checks for a "Bearer Token" in the Headers.
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // The Wristband Maker
    private final UserDetailsService userDetailsService; // The Database Clerk

    @Autowired
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // This method runs EVERY SINGLE TIME an HTTP request hits our API
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Look inside the HTTP Headers for "Authorization"
        final String authHeader = request.getHeader("Authorization");

        // 2. If it's missing or doesn't start with "Bearer ", the user doesn't have a wristband!
        // We let them pass the guard, but Spring Security will block them at the door if the endpoint requires login.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. We found a wristband! Grab just the token part (skip the "Bearer " string)
        final String jwt = authHeader.substring(7);

        // 4. Use the Maker to read the mobile number from the wristband
        final String mobileNo = jwtService.extractUsername(jwt);

        // 5. If we found a mobile number AND the user is not already logged in for this request
        if (mobileNo != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Go to the DB and fetch their full details (Roles, isActive)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(mobileNo);

            // 7. Ask the Maker: "Is this token valid for this user?"
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Yes! Create an official "Authentication" pass to tell Spring they are allowed in.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // credentials (password) - we don't need it because the token proved it.
                        userDetails.getAuthorities() // Roles (e.g. ROLE_ENGINEER)
                );

                // Add extra details like IP Address
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 9. LOG THE USER IN for this specific HTTP Request!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Pass the request along to the Controller now that they are logged in!
        filterChain.doFilter(request, response);
    }
}
