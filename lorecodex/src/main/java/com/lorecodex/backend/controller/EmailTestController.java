package com.lorecodex.backend.controller;

import com.lorecodex.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-email")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<Void> sendTestEmail(@RequestParam String to) {
        emailService.sendNotificationEmail(to, "Prueba LoreCodex", "Este es un correo de prueba desde Mailtrap!");
        return ResponseEntity.ok().build();
    }
}
