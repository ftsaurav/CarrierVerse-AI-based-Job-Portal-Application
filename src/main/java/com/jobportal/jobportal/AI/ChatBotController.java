package com.jobportal.jobportal.AI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    private final Map<String, String> faqResponses = Map.of(
            "How to apply?", "You can apply by visiting the job listings page and clicking 'Apply'.",
            "Resume upload issue", "Ensure your file is in PDF format and under 2MB.",
            "Contact support", "You can contact support via email at ft.saurav17@gmail.com.",
            "How to create an account?", "Go to the signup page, enter your details, and verify your email.",
            "How to edit my profile?", "You can edit your profile by visiting the 'My Profile' section and updating the required fields.",
            "Can I apply for multiple jobs?", "Yes, you can apply for multiple job listings that match your skills and experience.",
            "What if I don’t receive a response after applying?", "Companies may take time to review applications. If there's no update after a week, you can follow up through the job portal.",
            "Do I need to pay to apply for jobs?", "No, job applications on this portal are completely free for candidates."
    );



    // Endpoint to return all available questions
    @GetMapping("/questions")
    public ResponseEntity<List<String>> getQuestions() {
        return ResponseEntity.ok(List.copyOf(faqResponses.keySet()));
    }

    // Endpoint to return the answer for the selected question
    @GetMapping("/ask")
    public ResponseEntity<String> getResponse(@RequestParam String question) {
        String response = faqResponses.getOrDefault(question, "Sorry, I don't have an answer for that.");
        return ResponseEntity.ok(response);
    }
}
