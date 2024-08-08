package com.example.OAuth.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.ui.Model

@Controller
class LoginController {

    @GetMapping("/login")
    fun login(): String {
        return "index"
    }

    @GetMapping("/loginSuccess")
    fun loginSuccess(@AuthenticationPrincipal principal: OAuth2User, model: Model): String {
        val name = principal.attributes["name"] as String?
        val email = principal.attributes["email"] as String?
        val provider = principal.attributes["provider"] as String?
        model.addAttribute("name", name)
        model.addAttribute("email", email)
        model.addAttribute("provider", provider)
        return "loginSuccess"
    }

    @GetMapping("/loginError")
    fun loginError(model: Model): String {
        model.addAttribute("error", "An error occurred during login. Please try again.")
        return "loginError"
    }
}