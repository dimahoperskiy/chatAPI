package com.saturn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableJpaAuditing
public class SaturnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaturnApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer cors() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
//                        .allowedOrigins("http://192.168.1.67:3000", "http://localhost:3000")
                        .allowCredentials(true)
                        .allowedOrigins("http://89.108.65.167:3000")
                        .allowedHeaders("*");
            }
        };
    }

}
