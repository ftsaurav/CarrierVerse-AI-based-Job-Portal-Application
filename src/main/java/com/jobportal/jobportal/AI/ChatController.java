package com.jobportal.jobportal.AI;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Allow frontend requests
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/generate")
    public Mono<String> generateJobDescription(@RequestBody Map<String, String> request) {
        String role = request.get("role");
        String requirements = request.get("requirements");

        if (role == null || requirements == null) {
            return Mono.just("Error: Missing role or requirements.");
        }

        return geminiService.generateJobDescription(role, requirements);
    }
}
