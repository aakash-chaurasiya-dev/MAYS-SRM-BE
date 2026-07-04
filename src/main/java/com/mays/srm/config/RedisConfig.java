package com.mays.srm.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

// This is Redies Changes: Enable caching in Spring Boot
@Configuration
@EnableCaching
public class RedisConfig {

    // This is Redies Changes: Configure Redis to store data as JSON instead of Java binary
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        // 1. Create a custom ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 2. Register the JavaTimeModule so it can handle LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        
        // 3. Keep the default typing so Redis knows what Java Class to convert the JSON back into!
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        // 4. Pass our custom ObjectMapper to the Serializer
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60)) // Cache expires after 60 minutes
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }
}
