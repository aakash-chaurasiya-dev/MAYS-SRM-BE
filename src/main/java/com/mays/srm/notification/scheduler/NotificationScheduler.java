package com.mays.srm.notification.scheduler;

import com.mays.srm.notification.entities.NotificationOutbox;
import com.mays.srm.notification.repository.NotificationOutboxDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class NotificationScheduler {

    @Autowired
    private NotificationOutboxDao notificationOutboxDao;

    @Value("${msg91.auth-key}")
    private String msg91AuthKey;

    @Value("${msg91.sender-email}")
    private String msg91SenderEmail;

    @Value("${msg91.domain}")
    private String msg91Domain;

    @Value("${spring.mail.username}")
    private String smtpUsername;

     @Autowired
    private JavaMailSender javaMailSender;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedDelay = 10) // Runs every 10 seconds
    public void processPendingNotifications() {
        List<NotificationOutbox> pendingList = notificationOutboxDao.findByStatus("PENDING");

        for (NotificationOutbox outbox : pendingList) {
            try {
                if ("EMAIL".equalsIgnoreCase(outbox.getType())) {
                    sendEmail(outbox);
                } else if ("SMS".equalsIgnoreCase(outbox.getType())) {
                    sendSms(outbox);
                }
                
                outbox.setStatus("SENT");
            } catch (Exception e) {
                outbox.setRetryCount(outbox.getRetryCount() + 1);
                if (outbox.getRetryCount() >= 3) {
                    outbox.setStatus("FAILED");
                }
                System.err.println("Failed to send notification ID: " + outbox.getOutboxId() + ". Error: " + e.getMessage());
            } finally {
                notificationOutboxDao.save(outbox);
            }
        }
    }

    public void sendEmail(NotificationOutbox outbox) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(outbox.getRecipient());
        helper.setSubject(outbox.getSubject() != null ? outbox.getSubject() : "Notification");
        helper.setFrom(smtpUsername, "MAYS SRM");
        helper.setText(outbox.getMessageBody()); // true = HTML format
        try {
            javaMailSender.send(message);
//            System.out.println("✅ Email sent to: " + outbox.getRecipient());
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendEmailmsg91(NotificationOutbox outbox) throws Exception {
        String url = "https://control.msg91.com/api/v5/email/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("authkey", msg91AuthKey);

        Map<String, Object> payload = new HashMap<>();

        Map<String, String> toRecipient = new HashMap<>();
        toRecipient.put("email", outbox.getRecipient());
        toRecipient.put("name", "User");

        Map<String, Object> recipientItem = new HashMap<>();
        recipientItem.put("to", List.of(toRecipient));

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> variables = new HashMap<>();
        try {
            variables = mapper.readValue(outbox.getMessageBody(), new TypeReference<Map<String, String>>() {});
            variables.put("company_name", "MAYS SRM");
        } catch (Exception e) {
            System.err.println("Could not parse variables from messageBody: " + e.getMessage());
        }
        if (outbox.getTemplateId() != null && !outbox.getTemplateId().isEmpty()) {
            payload.put("template_id", outbox.getTemplateId());
            recipientItem.put("variables", variables);
            payload.put("recipients", List.of(recipientItem));
        } else {
            // Fallback for non-template raw emails
            payload.put("recipients", List.of(recipientItem));
            payload.put("subject", outbox.getSubject() != null ? outbox.getSubject() : "Notification");

            Map<String, String> emailBody = new HashMap<>();
            emailBody.put("type", "text/html");
            emailBody.put("data", outbox.getMessageBody()); // In this fallback, messageBody holds raw HTML

            payload.put("body", emailBody);
        }

        Map<String, String> fromSender = new HashMap<>();
        fromSender.put("email", msg91SenderEmail);
        fromSender.put("name", "MAYS SRM");

        payload.put("from", fromSender);
        payload.put("domain", msg91Domain);

        System.out.println("MSG91 EMAIL PAYLOAD:");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("MSG91 Email API failed: " + response.getBody());
        }
    }

    private void sendSms(NotificationOutbox outbox) {
        // Here you would integrate Twilio or your SMS provider
        System.out.println("Mock sending SMS to " + outbox.getRecipient() + ": " + outbox.getMessageBody());
    }
}
