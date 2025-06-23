package org.example.hotelbooking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EmailNotificationRequest {
    @JsonProperty("email_to")
    private String emailTo;

    private String subject;

    @JsonProperty("template_name")
    private String templateName = "welcome.html";

    private Map<String, Object> context;
}