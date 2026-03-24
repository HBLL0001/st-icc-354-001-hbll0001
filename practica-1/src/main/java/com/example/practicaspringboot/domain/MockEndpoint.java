package com.example.practicaspringboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mock_endpoints")
public class MockEndpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Identity ─────────────────────────────────────────────────────────
    @NotBlank
    private String name;

    private String description;

    // ─── Endpoint definition ──────────────────────────────────────────────
    @NotBlank
    private String path;

    @Enumerated(EnumType.STRING)
    @NotNull
    private HttpMethod method;

    // ─── Response ────────────────────────────────────────────────────────
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mock_headers", joinColumns = @JoinColumn(name = "endpoint_id"))
    private List<MockHeader> headers = new ArrayList<>();

    private int statusCode = 200;

    private String contentType = "application/json";

    @Column(columnDefinition = "TEXT")
    private String body;

    // ─── Timing ──────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    private ExpirationOption expirationOption = ExpirationOption.ONE_YEAR;

    private LocalDateTime expiresAt;

    private int delaySeconds = 0;

    // ─── JWT ─────────────────────────────────────────────────────────────
    private boolean jwtEnabled = false;

    // ─── Relations ───────────────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // ─── Lifecycle ───────────────────────────────────────────────────────
    @PrePersist
    public void computeExpiration() {
        if (expirationOption != null && expiresAt == null) {
            expiresAt = LocalDateTime.now().plusHours(expirationOption.getHours());
        }
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    // ─── Getters / Setters ────────────────────────────────────────────────
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public HttpMethod getMethod() { return method; }
    public void setMethod(HttpMethod method) { this.method = method; }
    public List<MockHeader> getHeaders() { return headers; }
    public void setHeaders(List<MockHeader> headers) { this.headers = headers; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public ExpirationOption getExpirationOption() { return expirationOption; }
    public void setExpirationOption(ExpirationOption expirationOption) { this.expirationOption = expirationOption; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public int getDelaySeconds() { return delaySeconds; }
    public void setDelaySeconds(int delaySeconds) { this.delaySeconds = delaySeconds; }
    public boolean isJwtEnabled() { return jwtEnabled; }
    public void setJwtEnabled(boolean jwtEnabled) { this.jwtEnabled = jwtEnabled; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
}
