package com.whatsontonight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WotApplication {
    public static void main(String[] args) {
        SpringApplication.run(WotApplication.class, args);
    }
}
