package com.example.practicaspringboot.service;

import com.example.practicaspringboot.domain.*;
import com.example.practicaspringboot.repository.MockEndpointRepository;
import com.example.practicaspringboot.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MockEndpointService {

    private final MockEndpointRepository mockRepo;
    private final JwtUtil jwtUtil;

    public MockEndpointService(MockEndpointRepository mockRepo, JwtUtil jwtUtil) {
        this.mockRepo = mockRepo;
        this.jwtUtil = jwtUtil;
    }

    /** Returns all mocks for admins, or only the owner's mocks for regular users. */
    public List<MockEndpoint> findForUser(User user) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return mockRepo.findAll();
        }
        return mockRepo.findByOwner(user);
    }

    public List<MockEndpoint> findByProject(Long projectId) {
        return mockRepo.findByProject_Id(projectId);
    }

    public Optional<MockEndpoint> findById(Long id) {
        return mockRepo.findById(id);
    }

    public Optional<MockEndpoint> findByPathAndMethod(String path, HttpMethod method) {
        return mockRepo.findByPathAndMethod(path, method);
    }

    public MockEndpoint save(MockEndpoint endpoint) {
        // Compute expiresAt before persisting when it is a new record
        if (endpoint.getId() == null && endpoint.getExpiresAt() == null) {
            ExpirationOption opt = endpoint.getExpirationOption() != null
                    ? endpoint.getExpirationOption()
                    : ExpirationOption.ONE_YEAR;
            endpoint.setExpiresAt(LocalDateTime.now().plusHours(opt.getHours()));
        }
        return mockRepo.save(endpoint);
    }

    public void delete(Long id) {
        mockRepo.deleteById(id);
    }

    public boolean canAccess(User user, MockEndpoint endpoint) {
        return user.getRole() == Role.ROLE_ADMIN
                || endpoint.getOwner().getId().equals(user.getId());
    }

    /** Generate a JWT for an endpoint (subject = path). */
    public String generateToken(MockEndpoint endpoint) {
        return jwtUtil.generateToken(endpoint.getPath(), endpoint.getExpiresAt());
    }

    public boolean validateToken(String token) {
        return jwtUtil.isValid(token);
    }
}
