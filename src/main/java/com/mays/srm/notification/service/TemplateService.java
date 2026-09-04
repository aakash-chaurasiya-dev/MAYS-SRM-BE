package com.mays.srm.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.mays.srm.notification.repository.EmailTemplateRepository;
import java.util.Map;

@Service
public class TemplateService {

    @Autowired
    private EmailTemplateRepository templateRepository;

    // Optional: cache templates to avoid DB hits
    @Cacheable(value = "emailTemplates", key = "#templateId")
    public String getTemplate(String templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"))
                .getHtmlBody();
    }

    public String render(String templateId, Map<String, Object> variables) {
        String html = getTemplate(templateId);
        // Replace placeholders like {{key}}
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            html = html.replace(placeholder, value);
        }
        return html;
    }
}