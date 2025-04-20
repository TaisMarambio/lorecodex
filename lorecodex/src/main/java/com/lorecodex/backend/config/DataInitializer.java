package com.lorecodex.backend.config;

import com.lorecodex.backend.service.serviceImpl.AuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthenticationServiceImpl authenticationService;

    @Autowired
    public DataInitializer(AuthenticationServiceImpl authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void run(String... args) {
        // Crear el admin por defecto si no existe
        authenticationService.createDefaultAdminIfNotExists();
    }
}