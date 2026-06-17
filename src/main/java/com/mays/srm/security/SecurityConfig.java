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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter; // The Security Guard

    @Autowired
    private UserDetailsService userDetailsService; // The Database Clerk

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS configured in the corsConfigurationSource bean
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Disable CSRF (Cross-Site Request Forgery) because we are building a REST API (JWTs are safe from this)
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configure exception handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // Set Authorization Rules (Who is allowed where?)
                .authorizeHttpRequests(auth -> auth
                        // 1. OPEN DOORS: Anyone can go to the login URL to get a wristband.
                        // Added Swagger UI URLs to permitAll
                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 2. EMPLOYEE MANAGEMENT: Only Manager can access
                        .requestMatchers(
                                "/api/employees/**",
                                "/api/users/**",
                                "/api/employee-specs/**"
                        ).hasRole("MANAGER")

                        // 2b. CUSTOMER ACCESS: Allow read metadata and ticket operations for customers (ROLE_USER)
                        .requestMatchers(HttpMethod.GET, "/api/devices/**").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/devicetypes/**").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/devicemodels/**").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/ticket-types/**").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/branches/**").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/brands/**").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/tickets").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/tickets/*").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/tickets/user/**").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/tickets/*").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/enquiries/user/**").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/enquiries").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/enquiries/*").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/enquiries/*").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers("/api/enquiries/**").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN")
                        .requestMatchers("/api/tickets/*/attachments").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN", "USER")
                        .requestMatchers("/api/ticket-logs/*").hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN")

                        // 3. TICKETS, DEVICES, BRANCHES, STATUSES: Accessible by Manager, Purchase, Engineer, and Admin
                        .requestMatchers(
                                "/api/tickets/**",
                                "/api/ticket-logs/**",
                                "/api/ticket-types/**",
                                "/api/devices/**",
                                "/api/devicetypes/**",
                                "/api/devicemodels/**",
                                "/api/branches/**",
                                "/api/statuses/**"
                        ).hasAnyRole("MANAGER", "PURCHASE", "ENGINEER", "ADMIN")

                        // 4. EVERYTHING ELSE (Purchase related): Accessible by Manager and Purchase Team
                        // Since employee management is matched above, they are excluded from this.
                        .anyRequest().hasAnyRole("MANAGER", "PURCHASE")
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
     * CORS Configuration Bean
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow the React frontend
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // Allow standard HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Allow headers including Authorization for JWT
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-auth-token"));
        // Expose headers if needed
        configuration.setExposedHeaders(List.of("x-auth-token"));
        // Allow credentials (like cookies or auth headers)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration to all endpoints
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
