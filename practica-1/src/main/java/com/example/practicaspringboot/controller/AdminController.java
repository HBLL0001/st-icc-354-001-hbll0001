package com.example.practicaspringboot.controller;

import com.example.practicaspringboot.domain.Role;
import com.example.practicaspringboot.domain.User;
import com.example.practicaspringboot.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // ─── List users ───────────────────────────────────────────────────────
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    // ─── Create user form ────────────────────────────────────────────────
    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("roles", Role.values());
        return "admin/user-form";
    }

    @PostMapping("/users/new")
    public String createUser(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam Role role,
                             RedirectAttributes ra) {
        if (userService.existsByUsername(username)) {
            ra.addFlashAttribute("error", "Username already exists");
            return "redirect:/admin/users/new";
        }
        userService.createUser(username, password, role);
        ra.addFlashAttribute("success", "User created successfully");
        return "redirect:/admin/users";
    }

    // ─── Update role ──────────────────────────────────────────────────────
    @PostMapping("/users/{id}/role")
    public String updateRole(@PathVariable Long id,
                             @RequestParam Role role,
                             RedirectAttributes ra) {
        userService.updateRole(id, role);
        ra.addFlashAttribute("success", "Role updated");
        return "redirect:/admin/users";
    }

    // ─── Toggle enabled ───────────────────────────────────────────────────
    @PostMapping("/users/{id}/toggle")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes ra) {
        userService.toggleEnabled(id);
        return "redirect:/admin/users";
    }
}
