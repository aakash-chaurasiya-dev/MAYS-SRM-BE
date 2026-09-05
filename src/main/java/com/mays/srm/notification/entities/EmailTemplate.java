/**
 * Entity class for email templates
 * @author Mays Computer Repair & Solutions
 * @version 1.0
 * @since 2026-09-02
 */
package com.mays.srm.notification.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "email_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailTemplate {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;   // template identifier, e.g., "ticket-notification"

    @Lob
    @Column(name = "html_body", columnDefinition = "TEXT", nullable = false)
    private String htmlBody; // the HTML content with placeholders like {{variable}}

}