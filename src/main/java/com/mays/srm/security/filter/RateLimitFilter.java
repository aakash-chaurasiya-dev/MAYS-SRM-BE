package com.mays.srm.security.filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS_PER_MINUTE = 5;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        // Only apply rate limiting to auth endpoints
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/send-otp")) {
            String clientIp = getClientIp(request);
            String key = "rate_limit:" + clientIp;
            
            String countStr = redisTemplate.opsForValue().get(key);
            
            if (countStr == null) {
                // First request, set count to 1 and expire in 5 minutes
                redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(15));
            } else {
                int count = Integer.parseInt(countStr);
                if (count >= MAX_REQUESTS_PER_MINUTE) {
                    // Rate limit exceeded
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.getWriter().write("Too many requests. Please try again later.");
                    return; // Stop the filter chain
                } else {
                    // Increment count
                    redisTemplate.opsForValue().increment(key);
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
