package com.thh.kiosk.queue.config;

import static com.thh.kiosk.queue.core.constant.PathConstants.IMG_UPLOAD_DIR;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(
            @NonNull ResourceHandlerRegistry registry
    ) {
        String uploadPath = IMG_UPLOAD_DIR.toAbsolutePath().toString().replace("\\", "/");
        if (!uploadPath.endsWith("/")) {
            uploadPath += "/";
        }
        String uploadLocation = "file:" + uploadPath;

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        uploadLocation,
                        "classpath:/static/uploads/",
                        "classpath:/uploads/"
                );

        registry.addResourceHandler("/audio/**")
                .addResourceLocations("classpath:/audio/");
    }
}