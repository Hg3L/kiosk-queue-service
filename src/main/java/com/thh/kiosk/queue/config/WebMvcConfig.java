package com.thh.kiosk.queue.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(
            @NonNull ResourceHandlerRegistry registry
    ) {
        try {
            Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

            Files.createDirectories(uploadDir);
            String uploadUri = uploadDir.toUri().toString();

            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations(uploadUri);

            registry.addResourceHandler("/audio/**")
                    .addResourceLocations("classpath:/audio/");

        } catch (IOException e) {
            log.error("Can't create directory!", e);
        }
    }
}