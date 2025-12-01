package com.example.chessgamebackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PUBLIC_INTERFACE
 * The entry point for the Chess Game Backend application.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Chess Game Backend API",
                version = "0.1.0",
                description = "REST API for creating and playing chess games with basic rules enforcement.",
                contact = @Contact(name = "Chess Backend", email = "backend@example.com")
        )
)
@SpringBootApplication
public class ChessGameBackendApplication {

    /**
     * PUBLIC_INTERFACE
     * Starts the Spring Boot application.
     *
     * @param args CLI args
     */
    public static void main(String[] args) {
        SpringApplication.run(ChessGameBackendApplication.class, args);
    }
}
