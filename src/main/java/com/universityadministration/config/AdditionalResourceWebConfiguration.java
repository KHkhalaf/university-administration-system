package com.universityadministration.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdditionalResourceWebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        String uploadFolder = "file:///C:/Users/KHkha/Downloads/Telegram Desktop/universityadministration/src/main/upload/";
        registry.addResourceHandler("/upload/**").addResourceLocations(uploadFolder);
    }
}