package com.mays.srm.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// STEP 5: THE RULE BOOK (SecurityConfig)
// This class sets the "Rules of the Club". What doors are open? Who is allowed in?
@Configuration // Tells Spring Boot this is a configuration file.
@EnableWebSecurity // Turns on Spring Security Features
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter; // The Security Guard

    @Autowired
    private UserDetailsService userDetailsService; // The Database Clerk

    /**
     * RULE BOOK 1: The SecurityFilterChain intercepts ALL HTTP requests.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (Cross-Site Request Forgery) because we are building a REST API (JWTs are safe from this)
                .csrf(AbstractHttpConfigurer::disable)

                // Set Authorization Rules (Who is allowed where?)
                .authorizeHttpRequests(auth -> auth
                        // 1. OPEN DOORS: Anyone can go to the login URL to get a wristband.
                        .requestMatchers("/api/auth/**").permitAll() 
                        
                        // 2. RESTRICTED DOORS (Examples):
//                         .requestMatchers("/api/admin/**").hasRole("MANAGER")
//                         .requestMatchers("/api/engineer/**").hasRole("ENGINEER")

                        // 3. CLOSED DOORS: Every other URL requires a valid wristband to enter!
                        .anyRequest().permitAll()
                )

                // Set Session Management (Make the API Stateless)
                // Meaning: Do NOT remember users between clicks. Force them to show the wristband EVERY time.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Tell Spring how to verify passwords against the DB
                .authenticationProvider(authenticationProvider())

                // Add our custom Security Guard (JwtFilter) BEFORE the standard username/password filter
                // So it checks for the Bearer token first!
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * RULE BOOK 2: Setup the component that fetches user details and compares the hashed password.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Go find the User
        authProvider.setPasswordEncoder(passwordEncoder());     // Compare their Password
        return authProvider;
    }

    /**
     * RULE BOOK 3: Setup the Authentication Manager (The Boss that we call in AuthController to attempt a login)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * RULE BOOK 4: Setup the Password Encoder (Tells Spring we are using BCrypt hashes in the database)
     * e.g., "password123" becomes "$2a$10$wK.T..."
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
