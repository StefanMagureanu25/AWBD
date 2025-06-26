package com.stefan.ecommerce.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/500")
    public String internalServerError(Model model) {
        model.addAttribute("errorMessage", "An internal server error occurred. Please try again later.");
        return "error/500";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorMessage", "You don't have permission to access this resource.");
        return "error/access-denied";
    }
} 