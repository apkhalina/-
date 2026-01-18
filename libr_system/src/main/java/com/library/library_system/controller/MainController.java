package com.library.library_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Главный контроллер приложения.
 */
@Controller
public class MainController {

    /**
     * Главная страница приложения.
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * Панель управления.
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}