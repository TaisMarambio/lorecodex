package com.lorecodex.backend.service.serviceImpl;

import com.lorecodex.backend.dto.response.NotificationResponse;
import com.lorecodex.backend.mapper.NotificationMapper;
import com.lorecodex.backend.model.Notification;
import com.lorecodex.backend.model.User;
import com.lorecodex.backend.repository.NotificationRepository;
import com.lorecodex.backend.repository.UserRepository;
import com.lorecodex.backend.service.EmailService;
import com.lorecodex.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationMapper notificationMapper;

    @Override
    public void notifyUser(Long recipientId, String message) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .build();

        notificationRepository.save(notification);

        if (recipient.isEmailNotificationsEnabled()) {
            emailService.sendNotificationEmail(
                    recipient.getEmail(),
                    "Nueva notificación en LoreCodex",
                    message
            );
        }
    }

    @Override
    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public List<Long> getFollowersOfUser(Long userId) {
        return List.of();
    }

}
