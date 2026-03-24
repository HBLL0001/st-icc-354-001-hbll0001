package com.example.practicaspringboot.repository;

import com.example.practicaspringboot.domain.HttpMethod;
import com.example.practicaspringboot.domain.MockEndpoint;
import com.example.practicaspringboot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MockEndpointRepository extends JpaRepository<MockEndpoint, Long> {
    List<MockEndpoint> findByOwner(User owner);
    Optional<MockEndpoint> findByPathAndMethod(String path, HttpMethod method);
    List<MockEndpoint> findByProject_Id(Long projectId);
}
