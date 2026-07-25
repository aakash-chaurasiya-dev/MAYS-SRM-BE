package com.mays.srm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC Configuration for serving uploaded static files.
 * Maps requests to /files/** directly to the local filesystem upload directory.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadURI = uploadPath.toUri().toString();
        if (!uploadURI.endsWith("/")) {
            uploadURI += "/";
        }

        registry.addResourceHandler("/files/**")
                .addResourceLocations(uploadURI);
    }
}
