package com.example.practicaspringboot.controller;

import com.example.practicaspringboot.domain.*;
import com.example.practicaspringboot.service.MockEndpointService;
import com.example.practicaspringboot.service.ProjectService;
import com.example.practicaspringboot.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mocks")
public class MockEndpointController {

    private final MockEndpointService mockService;
    private final ProjectService projectService;
    private final UserService userService;

    public MockEndpointController(MockEndpointService mockService,
                                  ProjectService projectService,
                                  UserService userService) {
        this.mockService = mockService;
        this.projectService = projectService;
        this.userService = userService;
    }

    private User currentUser(UserDetails ud) {
        return userService.findByUsername(ud.getUsername()).orElseThrow();
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = currentUser(ud);
        model.addAttribute("mocks", mockService.findForUser(user));
        return "mocks/list";
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal UserDetails ud, Model model) {
        User user = currentUser(ud);
        model.addAttribute("endpoint", new MockEndpoint());
        model.addAttribute("projects", projectService.findForUser(user));
        model.addAttribute("methods", HttpMethod.values());
        model.addAttribute("expirations", ExpirationOption.values());
        return "mocks/form";
    }

    @PostMapping("/new")
    public String create(@AuthenticationPrincipal UserDetails ud,
                         @RequestParam String name,
                         @RequestParam(required = false) String description,
                         @RequestParam String path,
                         @RequestParam HttpMethod method,
                         @RequestParam(required = false) List<String> headerKeys,
                         @RequestParam(required = false) List<String> headerValues,
                         @RequestParam int statusCode,
                         @RequestParam String contentType,
                         @RequestParam(required = false) String body,
                         @RequestParam ExpirationOption expirationOption,
                         @RequestParam int delaySeconds,
                         @RequestParam(defaultValue = "false") boolean jwtEnabled,
                         @RequestParam Long projectId,
                         RedirectAttributes ra) {
        User user = currentUser(ud);
        Project project = projectService.findById(projectId).orElseThrow();

        MockEndpoint ep = new MockEndpoint();
        ep.setName(name);
        ep.setDescription(description);
        ep.setPath(path);
        ep.setMethod(method);
        ep.setStatusCode(statusCode);
        ep.setContentType(contentType);
        ep.setBody(body);
        ep.setExpirationOption(expirationOption);
        ep.setDelaySeconds(delaySeconds);
        ep.setJwtEnabled(jwtEnabled);
        ep.setProject(project);
        ep.setOwner(user);

        // Build headers list
        List<MockHeader> headers = new ArrayList<>();
        if (headerKeys != null) {
            for (int i = 0; i < headerKeys.size(); i++) {
                String k = headerKeys.get(i);
                String v = (headerValues != null && i < headerValues.size()) ? headerValues.get(i) : "";
                if (k != null && !k.isBlank()) {
                    headers.add(new MockHeader(k.trim(), v.trim()));
                }
            }
        }
        ep.setHeaders(headers);

        mockService.save(ep);
        ra.addFlashAttribute("success", "mock.created");
        return "redirect:/mocks";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails ud,
                           Model model) {
        User user = currentUser(ud);
        MockEndpoint ep = mockService.findById(id).orElseThrow();
        if (!mockService.canAccess(user, ep)) return "redirect:/mocks";
        model.addAttribute("endpoint", ep);
        model.addAttribute("projects", projectService.findForUser(user));
        model.addAttribute("methods", HttpMethod.values());
        model.addAttribute("expirations", ExpirationOption.values());
        return "mocks/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails ud,
                         @RequestParam String name,
                         @RequestParam(required = false) String description,
                         @RequestParam String path,
                         @RequestParam HttpMethod method,
                         @RequestParam(required = false) List<String> headerKeys,
                         @RequestParam(required = false) List<String> headerValues,
                         @RequestParam int statusCode,
                         @RequestParam String contentType,
                         @RequestParam(required = false) String body,
                         @RequestParam ExpirationOption expirationOption,
                         @RequestParam int delaySeconds,
                         @RequestParam(defaultValue = "false") boolean jwtEnabled,
                         @RequestParam Long projectId,
                         RedirectAttributes ra) {
        User user = currentUser(ud);
        MockEndpoint ep = mockService.findById(id).orElseThrow();
        if (!mockService.canAccess(user, ep)) return "redirect:/mocks";

        ep.setName(name);
        ep.setDescription(description);
        ep.setPath(path);
        ep.setMethod(method);
        ep.setStatusCode(statusCode);
        ep.setContentType(contentType);
        ep.setBody(body);
        ep.setExpirationOption(expirationOption);
        ep.setDelaySeconds(delaySeconds);
        ep.setJwtEnabled(jwtEnabled);
        ep.setProject(projectService.findById(projectId).orElseThrow());

        List<MockHeader> headers = new ArrayList<>();
        if (headerKeys != null) {
            for (int i = 0; i < headerKeys.size(); i++) {
                String k = headerKeys.get(i);
                String v = (headerValues != null && i < headerValues.size()) ? headerValues.get(i) : "";
                if (k != null && !k.isBlank()) {
                    headers.add(new MockHeader(k.trim(), v.trim()));
                }
            }
        }
        ep.setHeaders(headers);

        mockService.save(ep);
        ra.addFlashAttribute("success", "mock.updated");
        return "redirect:/mocks";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails ud,
                         RedirectAttributes ra) {
        User user = currentUser(ud);
        MockEndpoint ep = mockService.findById(id).orElseThrow();
        if (!mockService.canAccess(user, ep)) return "redirect:/mocks";
        mockService.delete(id);
        ra.addFlashAttribute("success", "mock.deleted");
        return "redirect:/mocks";
    }

    /** Returns the generated JWT for a JWT-enabled endpoint. */
    @GetMapping("/{id}/token")
    public String getToken(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails ud,
                           Model model) {
        User user = currentUser(ud);
        MockEndpoint ep = mockService.findById(id).orElseThrow();
        if (!mockService.canAccess(user, ep)) return "redirect:/mocks";
        String token = mockService.generateToken(ep);
        model.addAttribute("token", token);
        model.addAttribute("endpoint", ep);
        return "mocks/token";
    }
}
