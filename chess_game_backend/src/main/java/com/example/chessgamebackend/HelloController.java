package com.example.chessgamebackend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * A simple controller that provides health, info and docs redirect endpoints.
 * Placed outside the /api/chess namespace to avoid collisions.
 */
@RestController
@Tag(name = "Hello Controller", description = "Basic endpoints for chessgamebackend")
public class HelloController {

    /**
     * PUBLIC_INTERFACE
     * Root welcome endpoint.
     * @return welcome string
     */
    @GetMapping("/")
    @Operation(summary = "Welcome endpoint", description = "Returns a welcome message")
    public String hello() {
        return "Hello, Spring Boot! Welcome to chessgamebackend";
    }

    /**
     * PUBLIC_INTERFACE
     * Redirect to Swagger UI while preserving scheme/host/port via forwarded headers.
     * @param request incoming request
     * @return redirect view to swagger-ui
     */
    @GetMapping("/docs")
    @Operation(summary = "API Documentation", description = "Redirects to Swagger UI preserving original scheme/host/port")
    public RedirectView docs(HttpServletRequest request) {
        String target = UriComponentsBuilder
                .fromHttpRequest(new ServletServerHttpRequest(request))
                .replacePath("/swagger-ui.html")
                .replaceQuery(null)
                .build()
                .toUriString();

        RedirectView rv = new RedirectView(target);
        rv.setHttp10Compatible(false);
        return rv;
    }

    /**
     * PUBLIC_INTERFACE
     * Simple health endpoint.
     * @return OK
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns application health status")
    public String health() {
        return "OK";
    }

    /**
     * PUBLIC_INTERFACE
     * App info endpoint.
     * @return info string
     */
    @GetMapping("/api/info")
    @Operation(summary = "Application info", description = "Returns application information")
    public String info() {
        return "Spring Boot Application: chessgamebackend";
    }
}
