package org.example.hotelbooking.service;

import org.example.hotelbooking.dto.EmailNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public NotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendEmailNotification(EmailNotificationRequest request) {
        String url = notificationServiceUrl + "/notifications/email";

        logger.info("Sending email notification to: {}", request.getEmailTo());
        logger.debug("Notification request payload: {}", request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EmailNotificationRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        logger.info("Notification service responded with: {}", response.getBody());

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to send notification. Status: {}", response.getStatusCode());
            throw new RuntimeException("Failed to send email notification");
        }
    }
}
