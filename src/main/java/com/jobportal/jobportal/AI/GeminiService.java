package com.jobportal.jobportal.AI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.List;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${google.gemini.api.key}")
    private String apiKey;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com").build();
    }

    public Mono<String> generateJobDescription(String role, String requirements) {
        String prompt = "Generate a job description for a "+ role + " using the following structure:"+
                "About the Role: [brief description] "+
                "Responsibilities:  (Use bullet points) " +
                "Qualifications: (Use bullet points)"+
                "Bonus Points: (Use bullet points)"+
                "Benefits: (Placeholder:  Insert company benefits here)"+
                "The description should include these requirements:" +requirements+
                        "Keep the language concise and professional.  Do not include a company name.";
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );

        System.out.println("🔹 Sending Request to Gemini: " + prompt);
        System.out.println("🔹 Request Payload: " + requestBody);  // Log request payload

        return webClient.post()
                .uri("/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(Map.of(
                        "contents", List.of(
                                Map.of("parts", List.of(Map.of("text", prompt)))
                        )
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    System.out.println("🔹 API Response: " + response);  // Debugging
                    List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                    if (candidates != null && !candidates.isEmpty()) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) ((Map<String, Object>) candidates.get(0).get("content")).get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            return (String) parts.get(0).get("text");  // Extract job description
                        }
                    }
                    return "Error: No job description generated.";
                })
                .doOnError(error -> System.out.println("❌ API Error: " + error.getMessage()))
                .onErrorReturn("Error: Failed to fetch job description");
    }

}
