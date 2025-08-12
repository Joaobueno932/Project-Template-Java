package com.example.myproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal da aplicação Spring Boot.
 * 
 * Esta classe serve como ponto de entrada da aplicação e configura
 * as funcionalidades principais do Spring Boot.
 * 
 * @author Seu Nome
 * @version 1.0.0
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@ConfigurationPropertiesScan
public class MyProjectApplication {

    /**
     * Método principal que inicia a aplicação Spring Boot.
     * 
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(MyProjectApplication.class, args);
    }
}

