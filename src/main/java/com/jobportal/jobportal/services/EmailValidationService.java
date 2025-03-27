package com.jobportal.jobportal.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class EmailValidationService {
    private static final String API_URL = "https://emailvalidation.abstractapi.com/v1/?api_key=YOUR_API_KEY&email=";

    public boolean isEmailValid(String email) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(API_URL + email, String.class);

        return Objects.requireNonNull(response.getBody()).contains("\"is_valid_format\":true") &&
                response.getBody().contains("\"deliverability\":\"DELIVERABLE\"");
    }
}

