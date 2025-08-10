package me.valizadeh.practices.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Web controller for handling frontend pages
 */
@Controller
@Slf4j
public class WebController {

    /**
     * Home page
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }

    /**
     * Login page
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Dashboard page - requires authentication
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OidcUser oidcUser, Model model) {
        log.info("Dashboard accessed - OIDC User: {}", oidcUser);
        
        if (oidcUser != null) {
            log.info("User {} accessed dashboard", oidcUser.getSubject());
            model.addAttribute("user", oidcUser);
            model.addAttribute("username", oidcUser.getPreferredUsername());
            model.addAttribute("email", oidcUser.getEmail());
            model.addAttribute("fullName", oidcUser.getFullName());
            model.addAttribute("picture", oidcUser.getPicture());
            return "dashboard";
        } else {
            log.warn("Dashboard accessed without authentication - redirecting to login");
            return "redirect:/login";
        }
    }



    /**
     * Error page
     */
    @GetMapping("/error")
    public String error(@RequestParam(value = "message", required = false) String message, Model model) {
        log.error("Error page accessed with message: {}", message);
        model.addAttribute("errorMessage", message != null ? message : "An unexpected error occurred");
        return "error";
    }
}
