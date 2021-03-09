package com.saturn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SaturnApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaturnApplication.class, args);
    }

}
