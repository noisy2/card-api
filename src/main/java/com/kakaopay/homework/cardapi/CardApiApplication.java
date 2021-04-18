package com.kakaopay.homework.cardapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value = "classpath:application.properties")
public class CardApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardApiApplication.class, args);
    }

}
