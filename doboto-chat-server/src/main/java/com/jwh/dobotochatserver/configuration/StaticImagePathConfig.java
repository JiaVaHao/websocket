package com.jwh.dobotochatserver.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;

@Configuration
public class StaticImagePathConfig extends WebMvcConfigurerAdapter {

    @Value("${file.imagesAccessPath}")
    private String imagesAccessPath;
    @Value("${file.imagesPath}")
    private String imagesPath;

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(imagesPath);
        return factory.createMultipartConfig();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(imagesAccessPath).addResourceLocations("file:" + imagesPath);
    }
}
