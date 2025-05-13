package com.dam.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // Mapea la URL
                .addResourceLocations("file:uploads/") // Ubicación física de la carpeta
                .setCachePeriod(3600); // Permite cacheo de archivos
    }
}
