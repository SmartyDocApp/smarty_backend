package com.app.authservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, 
                       @RequestParam(required = false) String logout,
                       @RequestParam(required = false) String expired,
                       @RequestParam(required = false) String invalid,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Nom d'utilisateur ou mot de passe incorrect");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Vous avez été déconnecté avec succès");
        }
        
        if (expired != null) {
            model.addAttribute("error", "Votre session a expiré");
        }
        
        if (invalid != null) {
            model.addAttribute("error", "Session invalide");
        }
        
        return "login";
    }
} 