package com.lorecodex.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class NoOpDataInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) {
        // No-op en tests para evitar creación de admin y dependencias de repos DB reales
    }
}

