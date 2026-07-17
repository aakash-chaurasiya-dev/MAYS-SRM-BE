package com.mays.srm.notification.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notification_outbox")
public class NotificationOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_id")
    private Integer outboxId;

    @Column(name = "recipient", length = 100, nullable = false)
    private String recipient;

    @Column(name = "subject")
    private String subject;
    
    @Column(name = "template_id")
    private String templateId;

    @Lob
    @Column(name = "message_body", columnDefinition = "LONGTEXT", nullable = false)
    private String messageBody;

    @Column(name = "type", length = 10, nullable = false)
    private String type; // EMAIL or SMS

    @Column(name = "status", length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
