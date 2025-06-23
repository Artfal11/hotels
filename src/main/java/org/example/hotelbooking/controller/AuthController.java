package org.example.hotelbooking.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.hotelbooking.exception.UserAlreadyExistsException;
import org.example.hotelbooking.model.UserRole;
import org.example.hotelbooking.security.UserDetailsServiceImpl;
import org.example.hotelbooking.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

@Controller
public class AuthController {

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthController(UserService userService, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "accessDenied", required = false) String accessDenied,
                            Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Неверный email или пароль");
        }
        if (accessDenied != null) {
            model.addAttribute("accessDenied", "Для доступа войдите в аккаунт");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("roles", UserRole.values());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam("role") UserRole role,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               Model model,
                               HttpServletRequest request) {
        try {
            userService.registerUser(email, password, role, firstName, lastName);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return "redirect:/profile";
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("registerError", e.getMessage());
            model.addAttribute("roles", UserRole.values());
            return "register";
        }
    }
}
