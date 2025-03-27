package com.jobportal.jobportal.AI;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/chatbot")
    public String chatbotPage() {
        return "chatbot";  // Refers to chatbot.html in templates folder
    }
}

