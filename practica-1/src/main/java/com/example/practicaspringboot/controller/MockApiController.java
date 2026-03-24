package com.example.practicaspringboot.controller;

import com.example.practicaspringboot.domain.HttpMethod;
import com.example.practicaspringboot.domain.MockEndpoint;
import com.example.practicaspringboot.domain.MockHeader;
import com.example.practicaspringboot.service.MockEndpointService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * Dispatches every request to /api/** against registered mock endpoints.
 * Handles JWT validation, delay, response headers, status code, and body.
 */
@Controller
@RequestMapping("/api")
public class MockApiController {

    private final MockEndpointService mockService;

    public MockApiController(MockEndpointService mockService) {
        this.mockService = mockService;
    }

    @RequestMapping("/**")
    public void dispatch(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        // Extract the path after /api
        String fullPath = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = fullPath.substring((contextPath + "/api").length());
        if (path.isEmpty()) path = "/";

        // Map servlet method to our enum
        HttpMethod method;
        try {
            method = HttpMethod.valueOf(request.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method not supported");
            return;
        }

        // Look up mock
        MockEndpoint endpoint = mockService.findByPathAndMethod(path, method).orElse(null);
        if (endpoint == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), "No mock defined for " + method + " " + path);
            return;
        }

        // Expiration check
        if (endpoint.isExpired()) {
            response.sendError(HttpStatus.GONE.value(), "Mock endpoint has expired");
            return;
        }

        // JWT check
        if (endpoint.isJwtEnabled()) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "JWT required");
                return;
            }
            String token = authHeader.substring(7);
            if (!mockService.validateToken(token)) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired JWT");
                return;
            }
        }

        // Simulated delay
        if (endpoint.getDelaySeconds() > 0) {
            Thread.sleep(endpoint.getDelaySeconds() * 1000L);
        }

        // Set custom headers
        for (MockHeader h : endpoint.getHeaders()) {
            if (h.getHeaderKey() != null && !h.getHeaderKey().isBlank()) {
                response.setHeader(h.getHeaderKey(), h.getHeaderValue());
            }
        }

        // Build response
        response.setStatus(endpoint.getStatusCode());
        response.setContentType(endpoint.getContentType());
        if (endpoint.getBody() != null) {
            response.getWriter().write(endpoint.getBody());
        }
    }
}
