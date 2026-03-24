package com.example.practicaspringboot.controller;

import com.example.practicaspringboot.domain.Project;
import com.example.practicaspringboot.domain.User;
import com.example.practicaspringboot.service.ProjectService;
import com.example.practicaspringboot.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    private User currentUser(UserDetails ud) {
        return userService.findByUsername(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = currentUser(ud);
        model.addAttribute("projects", projectService.findForUser(user));
        return "projects/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("project", new Project());
        return "projects/form";
    }

    @PostMapping("/new")
    public String create(@AuthenticationPrincipal UserDetails ud,
                         @RequestParam String name,
                         @RequestParam(required = false) String description,
                         RedirectAttributes ra) {
        User user = currentUser(ud);
        Project p = new Project();
        p.setName(name);
        p.setDescription(description);
        p.setOwner(user);
        projectService.save(p);
        ra.addFlashAttribute("success", "project.created");
        return "redirect:/projects";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails ud,
                           Model model) {
        User user = currentUser(ud);
        Project p = projectService.findById(id).orElseThrow();
        if (!projectService.canAccess(user, p)) return "redirect:/projects";
        model.addAttribute("project", p);
        return "projects/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails ud,
                         @RequestParam String name,
                         @RequestParam(required = false) String description,
                         RedirectAttributes ra) {
        User user = currentUser(ud);
        Project p = projectService.findById(id).orElseThrow();
        if (!projectService.canAccess(user, p)) return "redirect:/projects";
        p.setName(name);
        p.setDescription(description);
        projectService.save(p);
        ra.addFlashAttribute("success", "project.updated");
        return "redirect:/projects";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails ud,
                         RedirectAttributes ra) {
        User user = currentUser(ud);
        Project p = projectService.findById(id).orElseThrow();
        if (!projectService.canAccess(user, p)) return "redirect:/projects";
        projectService.delete(id);
        ra.addFlashAttribute("success", "project.deleted");
        return "redirect:/projects";
    }
}
