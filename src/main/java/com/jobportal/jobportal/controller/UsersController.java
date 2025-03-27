package com.jobportal.jobportal.controller;

import com.jobportal.jobportal.entity.Users;
import com.jobportal.jobportal.entity.UsersType;
import com.jobportal.jobportal.services.EmailService;
import com.jobportal.jobportal.services.UsersService;
import com.jobportal.jobportal.services.UsersTypeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {
    private final UsersTypeService usersTypeService;
    private final UsersService usersService;
    private final EmailService emailService;

    @Autowired
    public UsersController(UsersTypeService usersTypeService, UsersService usersService, EmailService emailService) {
        this.usersTypeService = usersTypeService;
        this.usersService = usersService;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String register(Model model)
    {
        List<UsersType> usersTypes= usersTypeService.getAll();
        model.addAttribute("getAllTypes", usersTypes);
        model.addAttribute("user", new Users());
        return "register";
    }

    @PostMapping("/register/new")
    public String userRegistration(@Valid @ModelAttribute("user") Users users, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid email format. Please enter a valid email.");
            return "redirect:/register";
        }

        Optional<Users> optionalUsers = usersService.getUserByEmail(users.getEmail());
        if (optionalUsers.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already Registered, Try to Login");
            return "redirect:/register";
        }

        String token = UUID.randomUUID().toString();
        users.setVerificationToken(token);
        users.setActive(false);

        // Save user with disabled status
        usersService.addNew(users);

        // Send verification email
        emailService.sendVerificationEmail(users.getEmail(), token);

        // Add success message
        redirectAttributes.addFlashAttribute("message", "A verification email has been sent. Please verify your email.");

        return "redirect:/register"; // Redirect to avoid duplicate form submission on refresh
    }


    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        Optional<Users> userOptional = usersService.getUserByToken(token);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            user.setActive(true); // Activate user
            user.setVerificationToken(null); // Remove token after verification
            usersService.addNew(user);
            model.addAttribute("message", "Email verified successfully! You can now log in.");
            return "redirect:/register";
        } else {
            model.addAttribute("error", "Invalid verification link.");
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model)
    {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response)
    {

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null)
        {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }



}
