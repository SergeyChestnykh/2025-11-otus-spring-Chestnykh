package ru.otus.hw.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class ErrorPageController {

    @GetMapping("/forbidden")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbidden(Model model) {
        model.addAttribute("errorTitle", "Access Denied");
        model.addAttribute("errorMessage",
                "403 - You do not have permission to access this resource.");
        return "error";
    }
}