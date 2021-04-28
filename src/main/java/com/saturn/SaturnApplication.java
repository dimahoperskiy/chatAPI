package com.saturn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Основной класс для запуска сервера
* @author Dmitriy Khoperskiy
 */

@SpringBootApplication
@EnableJpaAuditing
public class SaturnApplication {

    /**
     * Метод для запуска
     * @param args -
     */
    public static void main(String[] args) {
        SpringApplication.run(SaturnApplication.class, args);
    }

    /**
     * Конфигурауия для кросс-браузерных запросов
     * Разрешаем делать запросы с нашего клиента
     * @return void
     */
    @Bean
    public WebMvcConfigurer cors() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowCredentials(true)
                        .allowedOrigins("https://dimahoperskiy.ru")
                        .allowedHeaders("*");
            }
        };
    }

}
