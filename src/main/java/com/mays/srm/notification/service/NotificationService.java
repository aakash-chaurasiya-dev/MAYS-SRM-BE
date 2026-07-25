package com.mays.srm.notification.service;

import com.mays.srm.notification.entities.NotificationOutbox;
import com.mays.srm.notification.repository.NotificationOutboxDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private NotificationOutboxDao notificationOutboxDao;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Enqueues an email into the database to be sent by the scheduler.
     */
    public void enqueueEmail(String to, String subject, String templateId, Map<String, Object> variables) {
        String jsonVariables = "{}";
        try {
            if (variables != null) {
                jsonVariables = objectMapper.writeValueAsString(variables);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        NotificationOutbox outbox = NotificationOutbox.builder()
                .recipient(to)
                .subject(subject)
                .templateId(templateId)
                .messageBody(jsonVariables)
                .type("EMAIL")
                .status("PENDING")
                .retryCount(0)
                .build();
                
        notificationOutboxDao.save(outbox);
    }

    /**
     * Enqueues an SMS into the database to be sent by the scheduler.
     */
    public void enqueueSms(String mobileNo, String textBody) {
        NotificationOutbox outbox = NotificationOutbox.builder()
                .recipient(mobileNo)
                .messageBody(textBody)
                .type("SMS")
                .status("PENDING")
                .retryCount(0)
                .build();
                
        notificationOutboxDao.save(outbox);
    }
}
