package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.model.Notification;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.NotificationRepository;
import com.lorecodex.backend.repository.UserRepository;
import com.lorecodex.backend.service.EmailService;
import com.lorecodex.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public void notifyUser(Long recipientId, String message) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .build();

        notificationRepository.save(notification);

        // este es opcional
        if (recipient.isEmailNotificationsEnabled()) {
            emailService.sendNotificationEmail(
                    recipient.getEmail(),
                    "Nueva notificación en LoreCodex",
                    message
            );
        }
    }
}
